package com.medtech.database;

import java.sql.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.jdbc.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.FindOptions;
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
	private final String neoUser = "neo4j";
	private final String neoPW = "1234qwer";
	private final String neoConString = "jdbc:neo4j://localhost:7474/";
	
	//This is the url for the heroku Graphene server
	//private final String neoConString = "jdbc:neo4j:http://app46842636-cwN6qG:6RYFAurdumaXb0gPajrt@app46842636cwn6qg.sb02.stations.graphenedb.com:24789";
	
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
	
	//Object Id should be passed in after being retrieved from neo by the front end
	public Article mongoFindById(ObjectId id)
	{
		Document match;
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(mongoCollection);
		match = files.find(new Document("_id", id)).first();
		client.close();
		Binary data = (Binary) match.get("Data");
		
		Article article = new Article(data.getData(), match.getString("Name"));
		return article;
	}	
	
	//Need to provide a solution later to handle the whole set of labels
	//Just trying to get stuff up and running for now
	public void neoUpload(Article a, String element, String heading) throws SQLException, ClassNotFoundException
	{
		String query = 
				"Match (E:"+element+")-[SubElement]->(H:"+heading+")"
				+"Create (A:Article{name:\"" +a.getFileName()+"\","
						+"mongoId:\"" +a.getId() +"\"}),"
				        +"H-[:RELEVANT]->A";
		
		// Connect
		Connection con = DriverManager.getConnection(neoConString,neoUser,neoPW);
		// Querying
		Statement stmt = con.createStatement();
		stmt.executeQuery(query);
		
	}
	
	//This map will need to be parsed over to JSON
	
	public ResultSet getRelevantArticles(Map<String, String> headings) throws SQLException
	{
		Iterator<Entry<String, String>> it = headings.entrySet().iterator();
		StringBuilder sbRows = new StringBuilder();
		StringBuilder sbCols = new StringBuilder();
		
		while(it.hasNext())
		{
			Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
			
			if(it.hasNext())
			{
				sbRows.append("\"" +pair.getKey() +"\",");
				sbCols.append("\"" +pair.getValue() +"\",");
			}
			else
			{
				sbRows.append("\"" +pair.getKey() +"\"");
				sbCols.append("\"" +pair.getValue() +"\"");
			}
		}
		
		//Test this query with a full neoDB
		String query = "MATCH (R :Row)-[:SubElement]-(C :Column)-[:Relevant]->(A :Article) "
				+ "WHERE R.name IN[" + sbRows.toString() +"] "
						+ "AND C.name IN [" +sbCols.toString() +"] Return (A);" ;
		
		// Connect
		Connection con = DriverManager.getConnection(neoConString,neoUser,neoPW);
		// Querying
		Statement stmt = con.createStatement();
		return stmt.executeQuery(query);
		
	}
}
