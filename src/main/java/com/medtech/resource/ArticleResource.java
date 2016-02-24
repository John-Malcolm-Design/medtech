package com.medtech.resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.medtech.model.Article;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)

public class ArticleResource {
	    @GET    
	    public String getArticles() {
	    	//get all articles from article service
	        return "This is a list. Good job!";
	    }
	    @Path("{articleId}")
	    @GET    
	    public String getArticle() {
	    	//get all articles from article service
	        return "This is a list. Good job!";
	    }
	    @POST
	    @Consumes(MediaType.MULTIPART_FORM_DATA)
	    public String postArticle(
	    	    @DefaultValue("true") @FormDataParam("enabled") boolean enabled,
	    	    
	    	    @FormDataParam("uploadedfile") InputStream fileInputStream,
	    	    @FormDataParam("uploadedfile") FormDataContentDisposition fileDisposition) {
	    	
	    	Article newArticle = new Article(fileInputStream, "Test.docx");
	    	
	        //Connect to DB and create //mongodb://<dbuser>:<dbpassword>@ds029615.mongolab.com:29615/heroku_jddvvzdm
			MongoClientURI uri  = new MongoClientURI("mongodb://BillyBob:1234qwer@ds029615.mongolab.com:29615/heroku_jddvvzdm"); 
			MongoClient client = new MongoClient(uri);
			MongoDatabase db = client.getDatabase("heroku_jddvvzdm");
			MongoCollection<Document> files = db.getCollection("files");
	         
			//NEO4J magic happens
			
			//
			
			//create the document and store it
			Map<String, Object> docMap = new HashMap<String, Object>();
			docMap.put("Name", newArticle.toString());
			docMap.put("Data", newArticle.getData());
			//docMap.put("WordMap", newArticle.getWordMap());
			Document doc = new Document(docMap);        
			files.insertOne(doc);
	      
	        client.close();
		        return "Booya!";
	   }
	    	    
}
