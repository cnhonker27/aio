package org.rand.aio.core.decoder;


import org.rand.aio.core.decoder.http.AioHttpMethod;
import org.rand.aio.core.decoder.http.AioHttpVersion;

public interface AioRequest {

    String getHeader(String name);

    AioHeader getHeaders();

    String getParameter(String name);

    AioHttpMethod getMethod();

    AioHttpVersion getVersion();

    String getUri();
}
