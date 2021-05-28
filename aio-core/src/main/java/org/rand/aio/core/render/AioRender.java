package org.rand.aio.core.render;

import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;

public interface AioRender {
    void doRender(AioRequest request, AioResponse response, AioRenderModel renderData);
}
