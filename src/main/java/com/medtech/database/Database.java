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

//Create a session factory class for neo, later.
public class Database {

	private final String mongoURL = "mongodb://BillyBob:1234qwer@ds029615.mongolab.com:29615/heroku_jddvvzdm";
	private final String mongoDB = "heroku_jddvvzdm";

	private final String filesCollection = "files";
	private final String benchmarkCollection = "benchmark";
	
	//User and password for the graphstory connection
	private final String neoUser = "neo_heroku_jaida_reinger_darkred";
	private final String neoPW = "JzgKfQ4NM8vGbuPljyDeFnnohoIOMUKwi5wgzUsn";

	// private final String neoConString = "jdbc:neo4j://localhost:7474/";

	// Graphene

	// This is the url for the heroku GraphStory server
	private final String neoConString = "jdbc:neo4j:https://neo-heroku-jaida-reinger-darkred.digital-ocean.graphstory.com:7473/";

	public Database() {

	}

	
	public void getBenchmark(){
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> benchmark = db.getCollection(benchmarkCollection);
		System.out.println(benchmark);
		client.close();
	}
	
	public void mongoUpload(Document doc){
		MongoClientURI uri  = new MongoClientURI(mongoURL); 
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase(mongoDB);
		MongoCollection<Document> files = db.getCollection(filesCollection);
		files.insertOne(doc);
		client.close();
	}

	// Object Id should be passed in after being retrieved from neo by the front
	// end
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

	// Need to provide a solution later to handle the whole set of labels
	// Just trying to get stuff up and running for now
	public void neoUpload(Article a, String element, String heading) throws SQLException, ClassNotFoundException {
		String query = "Match (E {name:\"" + element + "\"})-[SubElement]->(H{name:\"" + heading + "\"})"
				+ "Create (A:Article{name:\"" + a.getFileName() + "\", mongoId: \"" + a.getId() + "\"}), "
				+ "H-[:RELEVANT]->A;";

		// Connect
		Connection con = DriverManager.getConnection(neoConString, neoUser, neoPW);
		// Querying
		Statement stmt = con.createStatement();
		stmt.executeQuery(query);

	}

	// This map will need to be parsed over to JSON
	public String getRelevantArticles(String heading, String subHeading) throws SQLException {
		System.out.println(heading);
		System.out.println(subHeading);
		// Test this query with a full neoDB
		String query = "MATCH (H {name:\""+heading+"\"})-[:SubElement]-(S {name:\""+subHeading+"\"})-[:RELEVANT]->(A :Article) return A;";

		// Connect
		Connection con = DriverManager.getConnection(neoConString, neoUser, neoPW);
		// Querying
		Statement stmt = con.createStatement();
		ResultSet results = stmt.executeQuery(query);

		System.out.println(results.getMetaData().getColumnTypeName(1));
		return resultSetToJson(results);

	}
	//takes a result set, loops through it and creates JSON objects, then puts them in a JSON array, returns the .toString();
	public static String resultSetToJson(ResultSet rs) throws SQLException {
		JSONArray set = new JSONArray();
		JSONObject obj = new JSONObject();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		while (rs.next()) {			
			int numColumns = rsmd.getColumnCount();		
			for (int i = 1; i < numColumns + 1; i++) {
				String column_name = rsmd.getColumnName(i);
				obj.put(column_name, rs.getObject(column_name));
				set.put(obj);
			}
		}

		return set.toString();
	}
}