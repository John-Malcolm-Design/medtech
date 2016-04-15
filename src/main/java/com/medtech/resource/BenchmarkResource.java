package com.medtech.resource;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/benchmark")
@Produces(MediaType.APPLICATION_JSON)
public class BenchmarkResource {
	
	@GET
	public String getMessages(){
		return "Hello World!";
	}

}
