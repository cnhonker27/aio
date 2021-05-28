package org.rand.aio.core.decoder;

import java.util.Map;

public interface AioHeader {

    String getHeader(String name);

    Map<String,String> getHeaders();
}
