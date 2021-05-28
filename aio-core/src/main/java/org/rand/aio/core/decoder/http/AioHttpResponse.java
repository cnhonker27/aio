package org.rand.aio.core.decoder.http;


import org.rand.aio.core.decoder.AioResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AioHttpResponse implements AioResponse {
    private Map<String,Object > headers=new HashMap<>();

    private int status=200;

    private byte[] renderBody;

    private String body;

    private String contentType;

    public AioHttpResponse(){

    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {
        headers.put(name,date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        headers.put(name,date);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name,value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.put(name,value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.put(name,value);
    }

    @Override
    public void setStatus(int sc) {
        this.status=sc;
    }


    @Override
    public int getStatus() {
        return  this.status;
    }

    @Override
    public String getHeader(String name) {
        return (String) headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public void setBody(String body) {
        this.body=body;
    }

    @Override
    public byte[] getRenderBody() {
        if(this.renderBody!=null){
            return this.renderBody;
        }
        if(this.body==null){
            return new byte[0];
        }
        return body.getBytes();
    }

    @Override
    public void setRenderBody(byte[] renderBody) {
        this.renderBody=renderBody;
    }


    @Override
    public void setContentType(String contentType) {
        this.contentType=contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

}
