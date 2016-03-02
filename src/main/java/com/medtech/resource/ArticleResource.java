package com.medtech.resource;

import java.io.InputStream;
import java.sql.SQLException;
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

import com.medtech.database.Database;
import com.medtech.model.Article;


@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {
	@GET
	public String getArticles() {
		// get all articles from article service
		return "This is a list. Good job!";
	}

	@Path("{articleId}")
	@GET
	public String getArticle() {
		// get all articles from article service
		return "This is a list. Good job!";
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String postArticle(@DefaultValue("true") @FormDataParam("enabled") boolean enabled,
			@FormDataParam("uploadedfile") InputStream fileInputStream,
			@FormDataParam("uploadedfile") FormDataContentDisposition fileDisposition) {
		
		Article newArticle = new Article(fileInputStream, fileDisposition.getFileName());
		
		// create the document and store it
		Map<String, Object> docMap = new HashMap<String, Object>();
		docMap.put("Name", newArticle.getFileName());
		docMap.put("Data", newArticle.getData());
		// docMap.put("WordMap", newArticle.getWordMap());
		Document doc = new Document(docMap);
		Database dbConnections = new Database();
		dbConnections.mongoUpload(doc);
		try {
			dbConnections.neoUpload(newArticle, "StandardProcesses", "Performing");
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Booya!";
	}

}
