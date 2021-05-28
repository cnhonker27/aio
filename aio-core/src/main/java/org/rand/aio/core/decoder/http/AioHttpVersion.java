package org.rand.aio.core.decoder.http;

public enum AioHttpVersion {
	HTTP_1_0(false,"HTTP/1.0"),

	HTTP_1_1(true,"HTTP/1.1");

	private boolean keepAliveDefault;

	private String name;

	AioHttpVersion(boolean keepAliveDefault, String name){
		this.keepAliveDefault = keepAliveDefault;
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public boolean isKeepAliveDefault() {
		return keepAliveDefault;
	}
	@Override
	public String toString() {
		return name;
	}
}
