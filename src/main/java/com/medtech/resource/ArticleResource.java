package com.medtech.resource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.medtech.database.Database;
import com.medtech.model.Article;
import com.medtech.model.LabelBean;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {
	@GET
	public String getArticles() {
		// get all articles from article service
		return "This is a list. Good job!";
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/recommend")
	public String getRecommendations(LabelBean bean) {
		Database db = new Database();
		String response = null;
		try {
			response = db.getRelevantArticles(bean.getHeading(), bean.getSubHeading());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}


	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getArticleById(@PathParam("id") String articleId)
			throws JsonGenerationException, JsonMappingException, IOException {
		// get an article from article service

		Database db = new Database();
		System.out.println(articleId);
		Article article = db.mongoFindById(new ObjectId(articleId));
		System.out.println(article.getFileName());
		ResponseBuilder response = Response.ok((Object) article.getData());

		response.header("Content-Disposition", "attachment; filename=" + article.getFileName());
		return response.build();
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String postArticle(@DefaultValue("true") @FormDataParam("enabled") boolean enabled,
			@FormDataParam("uploadedfile") InputStream fileInputStream,
			@FormDataParam("uploadedfile") FormDataContentDisposition fileDisposition,
			@FormDataParam("row") String heading, @FormDataParam("col") String subHeading) {

		Article newArticle = new Article(fileInputStream, fileDisposition.getFileName());

		// create the document and store it
		Map<String, Object> docMap = new HashMap<String, Object>();
		docMap.put("Name", newArticle.getFileName());
		docMap.put("Data", newArticle.getData());
		docMap.put("_id", newArticle.getId());
		// docMap.put("WordMap", newArticle.getWordMap());
		Document doc = new Document(docMap);
		Database dbConnections = new Database();
		dbConnections.mongoUpload(doc);
		try { // replace these test values with values retrieved from MP/FD
				// payload
			dbConnections.neoUpload(newArticle, heading, subHeading);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Work Complete";
	}

}