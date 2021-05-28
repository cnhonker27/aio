package org.rand.aio.core.render;

import org.apache.commons.lang3.StringUtils;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.protocol.http.HttpRenderHandler;
import org.rand.aio.io.JarAndFileResolver;

import java.io.IOException;
import java.io.InputStream;

public class AioViewRender implements AioRender{
    private final static String DEFAULT_RENDER_SUFFIX=".html";

    private final static String DEFAULT_STATIC_FOLDER="static";

    private final static String DEFAULT_TEMPLATE_FOLDER="template";

    private HttpRenderHandler defaultDispatcherHandler;

    public AioViewRender(HttpRenderHandler defaultDispatcherHandler){
        this.defaultDispatcherHandler=defaultDispatcherHandler;
    }
    @Override
    public void doRender(AioRequest request, AioResponse response,AioRenderModel renderModel) {
        String filepath=renderModel.getViewName();
        if(StringUtils.isEmpty(filepath)){
            response.setStatus(AioResponse.SC_NOT_FOUND);
            response.setBody(AioResponse.SC_NOT_FOUND+"");
            return;
        }
        if(StringUtils.isNotEmpty(filepath)){
            if(!filepath.startsWith("/")){
                filepath="/"+filepath;
            }
            if(filepath.endsWith("/")){
                filepath+=filepath.substring(0,filepath.length()-1);
            }
        }
        filepath=DEFAULT_STATIC_FOLDER+filepath+DEFAULT_RENDER_SUFFIX;
        Class<?> applicationClass = defaultDispatcherHandler.getBaseBeanFactory().getApplicationClass();

        InputStream mappingFile = JarAndFileResolver.getMappingFile(applicationClass, filepath);
        if(mappingFile==null){
            response.setStatus(AioResponse.SC_NOT_FOUND);
            response.setBody(AioResponse.SC_NOT_FOUND+"");
        }else{
            byte[] bytes = JarAndFileResolver.getBytes(mappingFile);
            response.setRenderBody(bytes);
            defaultDispatcherHandler.setContentType(response,filepath);
        }
    }
}
