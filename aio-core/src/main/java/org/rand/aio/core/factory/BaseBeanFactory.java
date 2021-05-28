package org.rand.aio.core.factory;

import org.apache.commons.lang3.StringUtils;
import org.rand.aio.core.annotation.AioComponent;
import org.rand.aio.core.annotation.mvc.*;
import org.rand.aio.core.annotation.plugin.AioPlugin;
import org.rand.aio.core.annotation.plugin.ExcludeAioPlugin;
import org.rand.aio.core.dispatcher.AioRequestMethodMapping;
import org.rand.aio.core.factory.definition.BeanInstance;
import org.rand.aio.core.plugin.AioIPlugin;
import org.rand.aio.io.JarAndFileResolver;
import org.rand.aio.io.resource.Resource;
import org.rand.aio.util.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public  class BaseBeanFactory implements AioBeanFactory {

    private final static Logger logger= LoggerFactory.getLogger(BaseBeanFactory.class);

    public final static String POINT=".";

    public final static String DOLLAR_SIGN="$";

    public final static String DOUBLE_DOLLAR_SIGN=DOLLAR_SIGN+DOLLAR_SIGN;

    private Map<String ,Object> beansObjectInstance=new ConcurrentHashMap<>();

    private List<Object> beansInitInstance=new LinkedList<>();

    private Map<String ,Class<?>> beansCache=new ConcurrentHashMap<>();

    private final Map<String, AioRequestMethodMapping> requestMapping=new ConcurrentHashMap<>();

    private Class<?> aClass;

    private Class<? extends Annotation>[] applicationAnnotations=new Class[0];

    private final static String SERVER_FILE="aioserver.properties";


    public BaseBeanFactory(Class<?> aClass){
        this.aClass=aClass;
        loadApplicationAnnotations();
        loadProperties();
        initBeanFactory(aClass);
        initRequestMapping();
        initPlugin();
        finishInit();
    }

    private void loadApplicationAnnotations(){
        ExcludeAioPlugin annotation1 = this.aClass.getAnnotation(ExcludeAioPlugin.class);
        if (annotation1 != null) {
            applicationAnnotations = annotation1.value();
        }
    }

    private void loadProperties(){
        String mainClassPackageName = JarAndFileResolver.getMainClassPackageName(this.aClass);
        loadProperties(mainClassPackageName,SERVER_FILE);
    }

    private void loadProperties(String rootPath,String mather){
        Resource[] resources = JarAndFileResolver.getResources(mather);
        Stream.of(resources).forEach(resource->{
            InputStream inputStream = null;
            try {
                inputStream = resource.getInputStream();
                Properties properties = new Properties();
                properties.load(inputStream);
                Set<Map.Entry<Object, Object>> entries = properties.entrySet();
                entries.forEach(e->{
                    Object key = e.getKey();
                    if(!beansObjectInstance.containsKey(key.toString())){
                        beansObjectInstance.put(key.toString(),e.getValue());
                    }
                });
            } catch (IOException e) {
                logger.error("{}文件解析出错",rootPath);
                logger.error("错误：",e);
            }finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void initRequestMapping(){
        Map<String, Object> beansObjectInstance = getBeansObjectInstance();
        beansObjectInstance.forEach((k,v)->{
            AioController annotation = v.getClass().getAnnotation(AioController.class);
            if(annotation!=null){
                initRequestMapping(annotation.value(),v);
            }
        });
    }

    private void initRequestMapping(String rootPath,Object obj){
        Method[] declaredMethods = obj.getClass().getDeclaredMethods();
        String absolutelyPath=resolvePath(rootPath);
        for (Method method : declaredMethods) {
            AioRequestMapping mapping = method.getAnnotation(AioRequestMapping.class);
            if(mapping==null){
                continue;
            }
            String methodPath = resolvePath(mapping.value());
            if(StringUtils.isEmpty(methodPath)){
                throw new IllegalArgumentException(mapping+"不能为空或空字符串");
            }
            String requestPath=absolutelyPath+methodPath;
            AioRequestMethodMapping aioRequestMethodMapping = new AioRequestMethodMapping(requestPath,method,obj);
            Parameter[] parameters = method.getParameters();
            String[] paramNames=new String[parameters.length];
            Class<?>[] classType=new Class<?>[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter=parameters[i];
                AioParam annotation = parameter.getAnnotation(AioParam.class);
                if(annotation!=null){
                    paramNames[i]=annotation.value();
                }
                classType[i]=parameter.getType();
            }
            if(StringUtils.isNotEmpty(requestPath)&&requestMapping.containsKey(requestPath)){
                throw new IllegalArgumentException(methodPath+"已存在");
            }
            aioRequestMethodMapping.setParameterName(paramNames);
            aioRequestMethodMapping.setParameterType(classType);
            AioResponseBody declaredAnnotation = method.getDeclaredAnnotation(AioResponseBody.class);
            if(declaredAnnotation!=null){
                aioRequestMethodMapping.setAioRenderEnum(declaredAnnotation.value());
            }
            requestMapping.put(requestPath,aioRequestMethodMapping);
        }
    }

    private void initPlugin() {

    }

    private void finishInit() {
        this.beansInitInstance.forEach(instance->{
            try {
                populate(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Object getBean(String beanName) {
        return getBean(beanName,null);
    }

    @Override
    public <T> T getBean(Class<T> classType) {
        return getBean(getAioComponentBeanName(classType),classType);
    }

    @Override
    public <T> T getBean(String beaName, Class<T> classType) {
        return doGetBean(beaName, classType);
    }


    private Object getBeanInstance(String beanName) {
        //StringKit.hasText(beanName,"beanName不能为空");
        return beansObjectInstance.get(beanName);
    }

    private <T>T doGetBean(String beanName,Class<T> clazz){
        Object bean =null;
        if(StringKit.isEmpty(beanName)&&clazz!=null){
            beanName=clazz.getSimpleName();
        }
        bean=getBeanInstance(beanName);
        if(bean!=null){
            return (T) bean;
        }
        bean=creatBeanInstance(beanName,clazz);
        return (T)bean;
    }

    private void addObject(Object object){
        addObject(object.getClass().getSimpleName(),object);
    }

    private void addObject(String beanName,Object object){
        StringKit.hasText(beanName,"beanName不能为空");
        if(beansObjectInstance.containsKey(beanName)){
            logger.warn("{}已存在,不进行添加",beanName);
            return;
        }
        if (object == null) {
            throw new RuntimeException(beanName+"对象不能为空");
        }
        if (beansObjectInstance.containsValue(object)) {
            logger.warn("{}对象已存在,不进行添加",beanName);
            return;
        }
        beansObjectInstance.put(beanName,object);
    }

    public void initBeanFactory(Class<?> aClass){
        Set<Class<?>> classes = JarAndFileResolver.findClassByPackageName(aClass);
        classes.forEach(clazz ->{
            String beanName = getAioComponentBeanName(clazz);
            if(StringKit.isNotEmpty(beanName)){
                beansCache.put(beanName,clazz);
            }
        } );
        this.beansCache.entrySet().iterator().forEachRemaining(set->{
            creatBeanInstance(set.getKey(),set.getValue());
        });
        if(logger.isDebugEnabled()){
            this.beansObjectInstance.forEach((s, o) -> {
                logger.info("已缓存的实例化对象有：{}:{}",s,o);
            });
        }
    }

    public Object creatBeanInstance(String beanName,Class<?> aClass){
        if(logger.isDebugEnabled()){
            logger.info("本次创建的对象：{}",aClass);
        }
        Object object;
        if(aClass.isInterface()){
            aClass=findImplementClassByCache(aClass);
            if(aClass==null){
                return null;
            }
        }
        try {
            object = aClass.newInstance();
        } catch (InstantiationException e) {
            logger.error("{}",aClass);
            logger.error("创建实例出错",e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            logger.error("{}",aClass);
            logger.error("访问权限异常",e);
            throw new RuntimeException(e);
        }
        if (object == null) {
            return null;
        }
        try {
            populate(object);
        } catch (IllegalAccessException e) {
            logger.error("填充属性出错",e);
        }
        if(AioIPlugin.class.isAssignableFrom(aClass)){
            AioIPlugin<?> plugin = (AioIPlugin<?>) object;
            try {
                Object o = plugin.create();
                addObject(o);
                List<BeanInstance> instances = plugin.getInstances();
                instances.forEach(instance->{
                    addObject(instance.getBeanName(),instance.getObject());
                });
            } catch (Exception e) {
                plugin.destroy();
                throw new RuntimeException("创建插件异常",e);
            }
        }
        addObject(beanName,object);
        return object;
    }

    private void populate(Object object) throws IllegalAccessException {
        autowired(object,AioAutowired.class);
        autowiredValue(object,AioValue.class);
    }

    void autowired(Object bean,Class<? extends Annotation > autowiredAnnotation) throws IllegalAccessException {
        if(autowiredAnnotation!=null){
            Field[] fields = bean.getClass().getDeclaredFields();
            if(fields.length == 0){
                return;
            }
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(autowiredAnnotation);
                if (annotation!=null) {
                    Class<?> type = field.getType();
                    String value = getAioAnnotationValue(annotation);
                    Object autowiredBean;
                    if(StringKit.isNotEmpty(value)){
                        autowiredBean=getBean(value,type);
                    }else{
                        autowiredBean = resolverBean(type);
                        if(autowiredBean==null){
                            autowiredBean= getBean(type);
                        }
                        if (autowiredBean == null) {
                            addInitInstance(bean);
                            continue;
                        }
                    }
                    if(autowiredBean!=null){
                        field.setAccessible(true);
                        field.set(bean,autowiredBean);
                    }else{
                        addInitInstance(bean);
                        if(logger.isDebugEnabled()){
                            logger.warn("无法找到{}实现类，本次不进行注入",type.getSimpleName());
                        }
                    }

                }
            }
        }
    }

    void autowiredValue(Object bean,Class<? extends Annotation > autowiredAnnotation) throws IllegalAccessException {
        if(autowiredAnnotation!=null){
            Field[] fields = bean.getClass().getDeclaredFields();
            if(fields.length == 0){
                return;
            }
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(autowiredAnnotation);
                if (annotation!=null) {
                    String value = getAioAnnotationValue(annotation);
                    Object autowiredBean=null;
                    if(StringKit.isNotEmpty(value)){
                        autowiredBean= beansObjectInstance.get(value);
                    }
                    if(autowiredBean!=null){
                        field.setAccessible(true);
                        field.set(bean,autowiredBean);
                    }else{
                        if(logger.isDebugEnabled()){
                            logger.warn("AioValue无法找到{}，本次不进行注入",value);
                        }
                    }

                }
            }
        }
    }

    private void addInitInstance(Object instance){
        if (!this.beansInitInstance.contains(instance)) {
            this.beansInitInstance.add(instance);
        }
    }

    /**
     * 查找接口的实现类，并且实现类是有注解Component
     * @param clazz
     * @return
     */
    private Class<?> findImplementClassByCache(Class<?> clazz){
        for (Map.Entry<String, Class<?>> stringClassEntry : this.beansCache.entrySet()) {
            Class<?> value = stringClassEntry.getValue();
            if(clazz.isAssignableFrom(value)&&!clazz.equals(value)&&isComponent(value)){
                return value;
            }
        }
        return null;
    }

    private Object resolverBean(Class<?> clazz){
        Collection<Object> values = this.beansObjectInstance.values();
        List<Object> collect = values.stream().filter(obj -> clazz.isAssignableFrom(obj.getClass())).collect(Collectors.toList());
        if(collect.isEmpty()){
            return null;
        }
        if(logger.isDebugEnabled()){
            String simpleName = clazz.getSimpleName();
            if(collect.size()>1){
                logger.warn("{}找到多个实例，请核对",simpleName);
            }else{
                logger.info("{}找到一个实例",simpleName);
            }
        }
        return collect.get(0);
    }

    private String resolvePath(String path){
        if(!path.startsWith("/")){
            path="/"+path;
        }
        if(path.endsWith("/")){
            path=path.substring(0,path.length()-1);
        }
        return path;
    }

    public String getAioComponentBeanName(Class<?>  clazz) {
        if(clazz.isAnnotation()){
            return null;
        }
        List<Annotation> collect = Arrays.stream(clazz.getAnnotations()).filter(annotation -> {
            Class<? extends Annotation> aClass = annotation.annotationType();
            for (Class<? extends Annotation> applicationAnnotation : this.applicationAnnotations) {
                boolean isPresent=applicationAnnotation.isAssignableFrom(aClass);
                if(isPresent){
                    return false;
                }
            }
            // 判断该注解类上是否有AioComponent或AioIPlugin
            return aClass.isAnnotationPresent(AioComponent.class)||(aClass.isAnnotationPresent(AioPlugin.class));
        }).collect(Collectors.toList());
        if(collect.isEmpty()){
            return null;
        }
        if(collect.size()>1){
            throw new RuntimeException("AioComponent注解只能有一个");
        }
        String value="";
        if(AioIPlugin.class.isAssignableFrom(clazz)){
            getAioAnnotationValue(collect.get(0),"name");
        }else{
            value = getAioAnnotationValue(collect.get(0));
        }
        return StringKit.isEmpty(value)?getShortName(clazz.getName()):value;
    }

    public String getAioAnnotationValue(Annotation  annotation) {
        return getAioAnnotationValue(annotation,"value");
    }

    public String getAioAnnotationValue(Annotation  annotation,String methodName) {
        String name;
        try {
            name = (String) JarAndFileResolver.invokeMethod(annotation, methodName);
        } catch (Exception e) {
            throw new RuntimeException("获取注解value异常，请核对该注解是否有value方法"+annotation.toString(),e);
        }
        return name;
    }

    public static String getShortName(String className) {
        int lastDotIndex = className.lastIndexOf(POINT);
        int nameEndIndex = className.indexOf(DOUBLE_DOLLAR_SIGN);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(DOLLAR_SIGN, POINT);
        return shortName;
    }

    private boolean isComponent(Class<?> clazz){
        Annotation[] annotations = clazz.getAnnotations();
        return Stream.of(annotations).anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(AioComponent.class));
    }

    public Map<String, Object> getBeansObjectInstance() {
        return beansObjectInstance;
    }

    public Class<?> getApplicationClass() {
        return aClass;
    }

    public Map<String, AioRequestMethodMapping> getRequestMapping(){
        return this.requestMapping;
    }

}
