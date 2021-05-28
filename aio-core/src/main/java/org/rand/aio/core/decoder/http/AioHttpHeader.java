package org.rand.aio.core.decoder.http;


import org.rand.aio.core.decoder.AioHeader;

import java.util.HashMap;
import java.util.Map;

public class AioHttpHeader implements AioHeader {
    private Map<String,String> headers;

    public AioHttpHeader(){
        this(new HashMap<>());
    }

    public AioHttpHeader(Map<String,String> headers){
        this.headers=headers;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
}
