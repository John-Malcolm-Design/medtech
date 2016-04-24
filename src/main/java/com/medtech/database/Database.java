package com.medtech.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.medtech.model.Article;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//This class contains connection information and provides methods for accessing both MongoLabs and GraphStory 
//Currently the connections are created and closed within each method, while this is somewhat acceptable in development,
//we will be switching to pooled database connections over summer.

public class Database {

	// Mongo Connection Strings
	private final String mongoURL = "mongodb://BillyBob:1234qwer@ds029615.mongolab.com:29615/heroku_jddvvzdm";
	private final String mongoDB = "heroku_jddvvzdm";
	
	//Mongo Collections
	private final String filesCollection = "files";
	private final String benchmarkCollection = "benchmark";
	
	//GraphStory Auth info
	private final String neoUser = "neo_heroku_jaida_reinger_darkred";
	private final String neoPW = "JzgKfQ4NM8vGbuPljyDeFnnohoIOMUKwi5wgzUsn";

	//String for local neo4j db development
	// private final String neoConString = "jdbc:neo4j://localhost:7474/";

	//GraphStory connection string
	private final String neoConString = "jdbc:neo4j:https://neo-heroku-jaida-reinger-darkred.digital-ocean.graphstory.com:7473/";


	public Database() {
	}
	
	//Provides the benchmark text for the benchmark page on the front end
	public void getBenchmark(){
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> benchmark = db.getCollection(benchmarkCollection);
		System.out.println(benchmark);
		client.close();
	}
	
	//Uploads a document to MongoLabs
	public void mongoUpload(Document doc){
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(filesCollection);
		files.insertOne(doc);
		client.close();
	}

	//Gets an Article from MongoLabs
	//Pass in the ObjectId from GraphStory
	public Article mongoFindById(ObjectId id) {
		Document match;
		MongoClientURI uri = new MongoClientURI(mongoURL);
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(filesCollection);
		
		match = files.find(new Document("_id", id)).first();
		client.close();
		Binary data = (Binary) match.get("Data");
		
		Article article = new Article(data.getData(), match.getString("Name"));
		return article;
	}

	//Uploads a single article to a single set of headings
	//In the future we will be handling lists of headings
	public void neoUpload(Article a, String element, String heading) throws SQLException, ClassNotFoundException {
		String query = "Match (E {name:\""+element+"\"})-[SubElement]->(H{name:\""+heading
				+"\"}) Create (A:Article{name:\""+a.getFileName()+"\", mongoId: \""+a.getId()+"\"}), H-[:RELEVANT]->A;";

		Connection con = DriverManager.getConnection(neoConString, neoUser, neoPW);
		Statement stmt = con.createStatement();
		stmt.executeQuery(query);

	}

	//Queries GraphStory for relevant articles
	//Passes in one set of Headings/SubHeadings
	//In the future we will need to handle the entire benchmark for the summary page.
	public String getRelevantArticles(String heading, String subHeading) throws SQLException {

		String query = "MATCH (H {name:\""+heading+"\"})-[:SubElement]-(S {name:\""+subHeading+"\"})-[:RELEVANT]->(A :Article) return A;";
		Connection con = DriverManager.getConnection(neoConString, neoUser, neoPW);
		Statement stmt = con.createStatement();
		ResultSet results = stmt.executeQuery(query);

		return resultSetToJson(results);

	}
	
	//takes a result set, loops through it and creates JSON objects, then puts them in a JSON array, returns the .toString();
	public static String resultSetToJson(ResultSet rs) throws SQLException {
		JSONArray set = new JSONArray();		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		while (rs.next()) {			
			int numColumns = rsmd.getColumnCount();		
			for (int i = 1; i < numColumns + 1; i++) {
				JSONObject obj = new JSONObject();
				String column_name = rsmd.getColumnName(i);
				obj.put(column_name, rs.getObject(column_name));
				System.out.println(obj);
				set.put(obj);
			}
		}

		return set.toString();
	}
}