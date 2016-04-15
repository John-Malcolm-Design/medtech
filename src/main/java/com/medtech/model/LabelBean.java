package com.medtech.model;

public class LabelBean {
	private String heading;
	private String subHeading;
	
	public LabelBean(){}//jax needs this?

	public LabelBean(String heading, String subHeading) {
		super();
		this.heading = heading;
		this.subHeading = subHeading;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}
	
}
