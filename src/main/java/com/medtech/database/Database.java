package com.medtech.database;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Database {
	private String mongoURL = "mongodb://BillyBob:1234qwer@ds029615.mongolab.com:29615/heroku_jddvvzdm";
	private String mongoDB = "heroku_jddvvzdm";
	private String mongoCollection = "files";
	
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
	
	public void neoUpload()
	{
		
	}
}
