package org.rand.aio.core.render;

import com.google.gson.Gson;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;

public class AioJsonRender implements AioRender{
    @Override
    public void doRender(AioRequest request, AioResponse response, AioRenderModel renderModel) {
        Object renderObject = renderModel.getRenderObject();
        if(renderObject==null){
            return;
        }
        if(renderObject instanceof String){
            response.setBody((String)renderObject);
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(renderObject);
        response.setBody(json);

    }
}
