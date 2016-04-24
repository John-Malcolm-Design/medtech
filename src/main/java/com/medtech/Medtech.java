package com.medtech;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.medtech.database.Database;
import com.medtech.heroku.CORSFilter;
import com.medtech.resource.ArticleResource;
import com.medtech.resource.BenchmarkResource;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

@ApplicationPath("/")
public class Medtech extends ResourceConfig {
	public Medtech() {
		//All classes needed by the endpoints are to be included here
		super(MultiPartFeature.class, ArticleResource.class, BenchmarkResource.class, Database.class, CORSFilter.class);
	}
}
