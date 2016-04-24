package com.medtech.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.poi.util.IOUtils;
import org.bson.types.ObjectId;

//Represents the Articles

public class Article {
	
	private ObjectId id;
	private String fileName;
	private byte[] data = null;
	private Set<String> labels;

	//Additional potential properties
	//private ??? creationDate;
	//private Map<String, Integer> wordMap;
	//private string Uploader
	//private int Downloads //store the downloads solely on Neo4j?
	
	public Article()
	{
		
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

//	public Map<String, Integer> getWordMap() {
//		return wordMap;
//	}
//
//	public void setWordMap(Map<String, Integer> wordMap) {
//		this.wordMap = wordMap;
//	}

	public void addLabel(String s) {
		labels.add(s);
	}
	//Drop whichever we dont end up using, set or add... pick one.
	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}
	
	public Article(InputStream in, String fileName)
	{
		try {
				data = IOUtils.toByteArray(in);
//				wordMap = WordMapGenerators.getXWPFWordMap(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.fileName = fileName;
		this.id = new ObjectId();
	}
	
	public Article(byte[] data, String fileName)
	{
		this.data = data;
		this.fileName = fileName;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
}