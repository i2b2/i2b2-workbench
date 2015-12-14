package edu.harvard.i2b2.eclipse.login;

public class Project {
	private String name;
	private String method;
	private String url;
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method.toUpperCase();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		if(this.method.equals("SOAP")) {
			this.url = url;
		}
		else { 
			if (!(url.endsWith("/"))) {
				this.url = url+"/";
			}
			else {
				this.url = url;
			}
		}
	//	System.out.println(this.url);
	}
}
