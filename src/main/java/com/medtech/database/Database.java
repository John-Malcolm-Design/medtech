package com.medtech.database;

import java.sql.*;
import java.util.Collections;
import java.util.Map;
import org.neo4j.jdbc.*;
import org.bson.Document;

import com.medtech.model.Article;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//Create a session factory class for neo, later.
public class Database {
	
	private final String mongoURL = "mongodb://BillyBob:1234qwer@ds029615.mongolab.com:29615/heroku_jddvvzdm";
	private final String mongoDB = "heroku_jddvvzdm";
	private final String mongoCollection = "files";
	
	public Database()
	{
		
	}
	//change return statement when neo gets plugged in
	//should return some identifier from mongo to store in the graphDB
	public void mongoUpload(Document doc){
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(mongoCollection);
		files.insertOne(doc);
		client.close();
	}
	
	//Requires we actually build a document first. We could change this to just take in
	//the mongoId attached to the doc requested
	public FindIterable<Document> mongoFind(Document doc)
	{
		FindIterable<Document> matches;
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(mongoCollection);
		matches = files.find(doc);
		client.close();
		return matches;
	}	
	
//Implement proper OGM if possible, individual labels per element makes this a bit weird
//	public void neoUpload(NeoArticle article)
//	{
//		SessionFactory sessionFactory = new SessionFactory("base.domain");
//		Session session = sessionFactory.openSession(System.getenv("GRAPHENEDB_URL"));
//		session.save(article);	
//	}
	
	//Need to provide a solution later to handle the whole set of labels
	//Just trying to get stuff up and running for now
	public void neoUpload(Article a, String element, String heading) throws SQLException, ClassNotFoundException
	{
		String query = 
				"Match (E:"+element+")-[SubElement]->(H:"+heading+")"
				+"Create (A:Article{name:\"" +a.getFileName()+"\","
						+"mongoId:\"" +a.getId() +"\"}),"
				        +"H-[:RELEVANT]->A";
		
		//This is legacy stuff, shouldn't be necessary but if it is,
		//this is throwing a class not found exception... Y U DO DIS JAVAAA
		Class.forName("org.neo4j.jdbc.Driver");
		
		// Connect
		Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
		// Querying
		try(Statement stmt = con.createStatement())
		{
		    ResultSet rs = stmt.executeQuery(query);
		    while(rs.next())
		    {
		        System.out.println(rs.getString("n.name"));
		    }
		}	
	}
	//This map will need to be parsed over to JSON
	//gson library could do it...?Question Mark?
//	public Iterable<Map<String, Object>> getRelevantArticles(String element, String heading)
//	{
//		String query = "Match (E:"+element+"-[SUBHEADING]->(H:"+heading+")-[RELEVANT]->(A)"
//				+ "Return A;";
//		SessionFactory sessionFactory = new SessionFactory("base.domain");
//		//Session session = sessionFactory.openSession(System.getenv("GRAPHENEDB_URL"));
//		Session session = sessionFactory.openSession("http://localhost:7474");
//		return session.query(query, Collections.<String, Object> emptyMap());
//	}
}
