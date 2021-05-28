package org.rand.aio.core.decoder.http;

public enum AioHttpMethod {
	GET("GET"),
	POST("POST");

	private String name;

	AioHttpMethod(String name){
		this.name=name;
	}
	public static AioHttpMethod getHttpMethod(String name){
		if(name.equals("GET")){
			return GET;
		}else if(name.equals("POST")){
			return POST;
		}else
			return null;
	}

	public String getName() {
		return name;
	}
}
