package org.rand.aio.core.protocol.http;

import org.apache.commons.lang3.StringUtils;
import org.rand.aio.core.annotation.mvc.AioResponseBody;
import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.decoder.http.AioHttpContentTypeEnums;
import org.rand.aio.core.dispatcher.AioRequestMethodMapping;
import org.rand.aio.core.factory.AioBeanFactory;
import org.rand.aio.core.factory.BaseBeanFactory;
import org.rand.aio.core.render.AioJsonRender;
import org.rand.aio.core.render.AioRender;
import org.rand.aio.core.render.AioRenderModel;
import org.rand.aio.core.render.AioViewRender;
import org.rand.aio.io.JarAndFileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fengliang on 2021/4/2.
 */
public class HttpRenderHandler {
    Logger logger = LoggerFactory.getLogger(HttpRenderHandler.class);

    private Map<String, AioRequestMethodMapping> requestMapping=new ConcurrentHashMap<>();

    private BaseBeanFactory baseBeanFactory;

    private AioRender aioRender;

    public HttpRenderHandler(AioBeanFactory baseBeanFactory){
        this.baseBeanFactory= (BaseBeanFactory) baseBeanFactory;
        this.requestMapping=this.baseBeanFactory.getRequestMapping();
    }
    public void invoke(AioRequest request, AioResponse response, AioPacket data) {
        String requestPath=request.getUri();
        int i = requestPath.indexOf("?");
        if(i>-1){
            // TODO 查找第一个?出现的位置
            requestPath=requestPath.substring(0,i);
        }
        logger.info("requestPath{}",requestPath);
        AioRequestMethodMapping aioRequestMapping = requestMapping.get(requestPath);
        if(aioRequestMapping!=null){
            Object controller = aioRequestMapping.getController();
            try {
                Method method = aioRequestMapping.getMappingMethod();
                AioRenderModel aioRenderModel=null;
                Object renderData = method.invoke(controller, convertToObject(request, response, aioRequestMapping));
                if(renderData instanceof AioRenderModel){
                    aioRenderModel= (AioRenderModel) renderData;
                }else{
                    aioRenderModel=new AioRenderModel();
                    AioResponseBody declaredAnnotation = method.getDeclaredAnnotation(AioResponseBody.class);
                    if(declaredAnnotation!=null){
                        aioRenderModel.setRenderObject(renderData);
                    }else {
                        aioRenderModel.setViewName(renderData!=null?renderData.toString():null);
                    }
                }
                doRender(request, response,aioRequestMapping,aioRenderModel);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("调用出错",e);
                response.setStatus(AioResponse.SC_INTERNAL_SERVER_ERROR);
                response.setBody("500");
            }
        }else{
            if("/".equals(requestPath)){
                response.setStatus(AioResponse.SC_NOT_FOUND);
                response.setBody(AioResponse.SC_NOT_FOUND+"");
                return;
            }
            Class<?> applicationClass = baseBeanFactory.getApplicationClass();
            InputStream mappingFile = JarAndFileResolver.getMappingFile(applicationClass, "static"+requestPath);
            if(mappingFile==null){
                response.setStatus(AioResponse.SC_NOT_FOUND);
                response.setBody(AioResponse.SC_NOT_FOUND+"");
                return;
            }
            byte[] bytes = JarAndFileResolver.getBytes(mappingFile);
            response.setRenderBody(bytes);
            setContentType(response,requestPath);
        }
    }

    private void doRender(AioRequest request,AioResponse response, AioRequestMethodMapping aioRequestMapping, AioRenderModel renderModel) {
        switch (aioRequestMapping.getAioRenderEnum()) {
            case JSON:
                this.aioRender=new AioJsonRender();
                break;
            case VIEW:
                this.aioRender=new AioViewRender(this);
                break;
            case WEBSERVICE:
                doRenderWebService(request,response,renderModel);
                break;
            default:
                doRenderOther(request,response,renderModel);
        }
        this.aioRender.doRender(request,response,renderModel);

    }

    private void doRenderWebService(AioRequest request, AioResponse response, AioRenderModel renderModel) {

    }

    private void doRenderOther(AioRequest request, AioResponse response, AioRenderModel renderModel) {
    }

    public void setContentType(AioResponse response,String fileName){
        if(StringUtils.isEmpty(response.getContentType())){
            if(fileName.lastIndexOf(".")==-1){
                return;
            }
            String suffix= fileName.substring(fileName.lastIndexOf("."));
            try {
                AioHttpContentTypeEnums aioHttpContentTypeEnums = AioHttpContentTypeEnums.formatterKey(suffix);
                response.setContentType(aioHttpContentTypeEnums.getValue());
            } catch (Exception e) {
                logger.error("ContentType：{}",e.getMessage());
                response.setContentType(AioHttpContentTypeEnums.All.getValue());
            }
        }
    }
    /**
     *
     * @param request
     * @param response
     * @param requestMapping
     * @return
     *
     */
    // TODO 转换成对应的类型
    private Object[] convertToObject(AioRequest request,AioResponse response, AioRequestMethodMapping requestMapping){
        Class<?>[] parameterTypes = requestMapping.getParameterType();
        Object[] obj=new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if(parameterType.isAssignableFrom(AioRequest.class)){
                obj[i]=request;
            }
            if(parameterType.isAssignableFrom(AioResponse.class)){
                obj[i]=response;
            }
        }
        return obj;
    }
    public BaseBeanFactory getBaseBeanFactory() {
        return baseBeanFactory;
    }
}
