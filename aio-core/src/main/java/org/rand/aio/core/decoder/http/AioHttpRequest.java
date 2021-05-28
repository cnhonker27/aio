package org.rand.aio.core.decoder.http;


import org.rand.aio.core.decoder.AioHeader;
import org.rand.aio.core.decoder.AioRequest;

import java.util.HashMap;
import java.util.Map;

public class AioHttpRequest implements AioRequest {

    private AioHeader header;

    private AioHttpMethod method;

    private AioHttpVersion version;

    private Map<String, String> paramMap;

    private String uri;

    public AioHttpRequest(AioHttpMethod method,AioHttpVersion version,String uri){
        this(method,version,uri,new AioHttpHeader(),new HashMap<>());
    }

    public AioHttpRequest(AioHttpMethod method, AioHttpVersion version, String uri, AioHeader header, Map<String, String> paramMap){
        this.method=method;
        this.version=version;
        this.uri=uri;
        this.header=header;
        this.paramMap=paramMap;
    }

    @Override
    public String getHeader(String name) {
        return header.getHeader(name);
    }

    @Override
    public AioHeader getHeaders() {
        return header;
    }

    @Override
    public String getParameter(String name) {
        if(this.paramMap==null){
            this.paramMap=new HashMap<>();
        }
        return this.paramMap.get(name);
    }

    @Override
    public AioHttpMethod getMethod() {
        return method;
    }

    @Override
    public AioHttpVersion getVersion() {
        return version;
    }
    @Override
    public String getUri() {
        return uri;
    }


}
