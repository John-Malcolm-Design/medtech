package com.medtech.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.IOUtils;

public class Article {
	
	private long id;//probably needs to change data type according to mongo
	private String fileName;
	private byte[] data = null;
	private Map<String, Integer> wordMap;
	private List<String> labels;
	//private created;
	
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

	public Map<String, Integer> getWordMap() {
		return wordMap;
	}

	public void setWordMap(Map<String, Integer> wordMap) {
		this.wordMap = wordMap;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
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
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
