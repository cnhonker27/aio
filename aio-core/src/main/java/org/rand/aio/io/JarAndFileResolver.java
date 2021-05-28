package org.rand.aio.io;

import org.apache.commons.lang3.StringUtils;
import org.rand.aio.io.resource.FileResource;
import org.rand.aio.io.resource.Resource;
import org.rand.aio.io.resource.UrlResource;
import org.rand.aio.util.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class JarAndFileResolver {

    private final static Logger logger= LoggerFactory.getLogger(JarAndFileResolver.class);

    public final static String URL_PROTOCOL_FILE = "file";

    public final static String URL_PROTOCOL_JAR = "jar";

    // File.separator 文件分割符 不同的系统是分割符不一样windows(\) linux(/)
    public final static String REP_PREFIX="/classes/";

    public final static String POINT_SEPARATOR="\\.";

    public final static String POINT=".";

    public final static String REP_SUFFIX=".class";

    public final static String DOLLAR_SIGN="$";

    public final static String CLASSPATH="classpath*:";

    public final static String STAR="*";


    /**
     * 获取主程序的包名 clazz必须为main程序，且放在最外层
     * 类似springboot的主程序放在最外层方便获取包名进行注解扫描注入。
     * springboot框架方法获取的主程序包名很复杂，先注册到BeanFactory，再取出来解析
     * 本质上就是获取主程序根据包名解析，在这里直接传进来解析
     * @param clazz
     */
    public static String getMainClassPackageName(Class<?> clazz){
        String packageName = clazz.getPackage().getName();
        if(StringUtils.isEmpty(packageName)){
            packageName="";
        } else{
            packageName=packageName.replaceAll(POINT_SEPARATOR, "/")+"/";
        }
        return packageName;
    }

    public static Resource[] getResources(){
        return getResources("");
    }
    // 取对应包名下的url
    public static Resource[] getResources(String pattern){
        StringKit.hasText(pattern,"ant表达式不能为空");
        if(pattern.startsWith(CLASSPATH)){
            if(AioAntPathResolver.isPattern(pattern.substring(CLASSPATH.length()))){
                return findResource(pattern);
            }else{
                return findClassPathResource(pattern.substring(CLASSPATH.length()));
            }
        }else{
            return findClassPathResource(pattern);
        }
    }

    private static Resource[] findResource(String pattern)  {
        String rootPath=AioAntPathResolver.determineRootDir(pattern);
        String subPattern=pattern.substring(rootPath.length());
        Resource[] resources = getResources(rootPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource resource : resources) {
            URL url = null;
            try {
                url = resource.getURL();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String protocol = url.getProtocol();
            if(isJarProtocol(protocol)){
                System.out.println("jar");
                result.addAll(findJarResource(resource,subPattern));
            }
            if(isFileProtocol(protocol)){
                result.addAll(findAllResource(resource,subPattern));
            }
        }
        return result.toArray(new Resource[0]);
    }

    private static Resource[] findClassPathResource(String location){
        if(location.startsWith("/")){
            location=location.substring(1);
        }
        if(location.startsWith("/")){
            findClassPathResource(location);
        }
        ArrayList<Resource> results = new ArrayList<>();
        try {
            ClassLoader classLoader = ClassLoaderUtil.getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(location);
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                results.add(JarAndFileResolver.convertFileResource(url));
            }
        } catch (IOException e) {
            logger.error("获取资源出错");
        }
        return results.toArray(new Resource[0]);
    }

    static Set<Resource> findAllResource(Resource resource,String pattern){
        File file = resource.getFile();
        Set<File> files = retrieveResource(file, pattern);
        Set<Resource> result = new LinkedHashSet<>();
        files.forEach(f->{
            result.add(new FileResource(f));
        });
        return result;
    }

    static Set<File> retrieveResource(File file,String pattern){
        if(!file.exists()){
            return Collections.emptySet();
        }
        if(!file.isDirectory()){
            return Collections.emptySet();
        }
        if(!file.canRead()){
            return Collections.emptySet();
        }
        String absolutePath = file.getAbsolutePath();
        String fullPattern = absolutePath.replaceAll("\\\\", "/");
        if(!pattern.startsWith("/")){
            fullPattern+="/";
        }
        fullPattern=fullPattern+pattern.replaceAll("\\\\", "/");
        LinkedHashSet<File> result = new LinkedHashSet<>();
        doRetrieveFiles(fullPattern,file,result);
        return result;
    }

    static void doRetrieveFiles(String fullPattern,File file,Set<File> set){
        File[] files = file.listFiles();
        for (File f : files) {
            String currPath = f.getAbsolutePath().replaceAll("\\\\", "/");
            if(isExistsAndFileAndCanRead(f)&&AioAntPathResolver.doMatch(currPath,fullPattern)){
                doRetrieveFiles(fullPattern,f,set);
            }
            if(AioAntPathResolver.doMatch(currPath,fullPattern)){
                set.add(f);
            }
        }
    }

    static  Set<Resource> findJarResource(Resource resource,String pattern){
        try {
            URL url = resource.getURL();
            URLConnection urlConnection = url.openConnection();
            JarFile jarFile=null;
            String jarFileUrl="";
            String rootEntryPath="";
            boolean closeJarFile=false;
            if(urlConnection instanceof  JarURLConnection){
                JarURLConnection connection=(JarURLConnection)urlConnection;
                jarFile=connection.getJarFile();
                jarFileUrl = connection.getJarFileURL().toExternalForm();
                JarEntry jarEntry = connection.getJarEntry();
                rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
                System.out.println(urlConnection);
                closeJarFile=true;
            }
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Looking for matching resources in jar file [" + jarFileUrl + "]");
                }
                if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                    // Root entry path must end with slash to allow for proper matching.
                    // The Sun JRE does not return a slash here, but BEA JRockit does.
                    rootEntryPath = rootEntryPath + "/";
                }
                Set<Resource> result = new LinkedHashSet<>(8);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryPath = entry.getName();
                    if (entryPath.startsWith(rootEntryPath)) {
                        String relativePath = entryPath.substring(rootEntryPath.length());
                        if (AioAntPathResolver.doMatch(relativePath, pattern)) {
                            URL url1 = new URL(url, relativePath);
                            result.add(new UrlResource(url1));
                        }
                    }
                }
                return result;
            }
            finally {
                if (closeJarFile) {
                    jarFile.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }

    public static Set<Class<?>> findClassByPackageName(Class<?> clazz)  {
        return findClassByPackageName(getMainClassPackageName(clazz));
    }

    public static Set<Class<?>> findClassByPackageName(String packageName) {
        Set<Class<?>> aClass = new HashSet<>();
        Resource[] resources= getResources(packageName);
        for (Resource resource : resources) {
            URL url = null;
            try {
                url = resource.getURL();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String protocol =url.getProtocol();
            if(URL_PROTOCOL_FILE.startsWith(protocol)){
                findClassByFile(aClass,url);
            }else if(URL_PROTOCOL_JAR.startsWith(protocol)){
                findClassByJar(aClass,url);
            }
        }
        return aClass;
    }


    public static void findClassByFile(Set<Class<?>> aClass,URL url){
        try {
            URI uri = url.toURI();
            String path = uri.getPath();
            File file = new File(path);
            File[] files = file.listFiles();
            scanFile(files,aClass);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static List<InputStream> findFilesByJar(URL url,String reg){
        List<InputStream> fileInputStreams = new ArrayList<>();
        try {
            URLConnection urlConnection = url.openConnection();
            if(urlConnection !=null){
                JarURLConnection jarURLConnection=(JarURLConnection)urlConnection;
                String rootPath = jarURLConnection.getEntryName();
                JarFile jarFile = jarURLConnection.getJarFile();
                if(jarFile!=null){
                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry entry = jarEntryEnumeration.nextElement();
                        boolean directory = entry.isDirectory();
                        String jarEntryName = entry.getName();
                        if(directory||!jarEntryName.startsWith(rootPath)||!jarEntryName.endsWith(reg)){
                            continue;
                        }
                        InputStream inputStream = jarFile.getInputStream(entry);
                        fileInputStreams.add(inputStream);
                    }
                }
            }
        }  catch (IOException e) {
            logger.error("打开异常",e);
        }
        return fileInputStreams;
    }

    public static void findClassByJar(Set<Class<?>> aClass,URL url){
        try {
            URLConnection urlConnection = url.openConnection();
            if(urlConnection !=null){
                JarURLConnection jarURLConnection=(JarURLConnection)urlConnection;
                String rootPath = jarURLConnection.getEntryName();
                JarFile jarFile = jarURLConnection.getJarFile();
                if(jarFile!=null){
                    //得到该jar文件下面的类实体
                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry entry = jarEntryEnumeration.nextElement();
                        boolean directory = entry.isDirectory();
                        String jarEntryName = entry.getName();
                        if(directory||!jarEntryName.startsWith(rootPath)||!jarEntryName.endsWith(REP_SUFFIX)){
                            continue;
                        }
                        String clazzName = getClazzName(jarEntryName);
                        if(StringUtils.isNotEmpty(clazzName)){
                            try {
                                aClass.add(Class.forName(clazzName));
                            } catch (ClassNotFoundException e) {
                                logger.error("{}不存在",clazzName);
                            }
                        }
                    }
                }
            }
        }  catch (IOException e) {
            logger.error("打开异常",e);
        }
    }



    public static void scanFile(File[] files, Set<Class<?>> aClass){
        for(File file: files){
            if(file.isDirectory()){
                scanFile(file.listFiles(),aClass);
            }else{
                String path = file.getPath();
                String clazzName = getClazzName(path);
                if(StringUtils.isNotEmpty(clazzName)){
                    try {
                        aClass.add(Class.forName(clazzName));
                    } catch (ClassNotFoundException e) {
                        logger.error("{}不存在",clazzName);
                    }
                }
            }
        }
    }


    static String getClazzName(String absolutePathName){
        String absoluteFilePath = absolutePathName.replace("\\","/");
        if(absoluteFilePath.contains(REP_PREFIX)){
            absoluteFilePath=absoluteFilePath.substring(absoluteFilePath.indexOf(REP_PREFIX)+REP_PREFIX.length(), absoluteFilePath.lastIndexOf(REP_SUFFIX));
        }else{
            absoluteFilePath=absolutePathName.substring(0, absolutePathName.lastIndexOf(REP_SUFFIX));
        }
        String absoluteClassName = absoluteFilePath.replace("/", POINT);
        if(absoluteClassName.contains(DOLLAR_SIGN)){
            if(logger.isDebugEnabled()){
                logger.info("absolutePathName:{}",absolutePathName);
                logger.info("{}含有\"{}\",是内部类丢弃它",absoluteClassName,DOLLAR_SIGN);
            }
            absoluteClassName="";
        }
        return absoluteClassName;
    }

    public static InputStream getMappingFile(Class<?> clazz,String fileName)  {
        String mainClassPackageName = getMainClassPackageName(clazz);
        Resource[] resources = getResources(mainClassPackageName);
        for (Resource resource : resources) {
            URL url = null;
            try {
                url = resource.getURL();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String rootPath = "/"+mainClassPackageName;
            String protocol = url.getProtocol();
            if(URL_PROTOCOL_FILE.startsWith(protocol)){
                String filePath=url.getPath().replace(rootPath,"/"+fileName);
                File file = new File(filePath);
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(URL_PROTOCOL_JAR.startsWith(protocol)){
                // 读取文件时不能以/开头 否则读取不到。这是为什么？
                return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            }
        }
        return null;
    }

    public static byte[] getBytes(InputStream inputStream){
        byte[] data=new byte[0];
        if(inputStream==null){
            return data;
        }
        try {
            byte[] readCache = new byte[1];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((inputStream.read(readCache))!=-1){
                outputStream.write(readCache);
            }
            outputStream.flush();
            data=outputStream.toByteArray();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getShortName(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace("$", ".");
        return shortName;
    }


    public static Object invokeMethod(Object object,String methodName,Object... args) throws Exception {
        Method method = getMethodByName(object.getClass(), methodName);
        if (method == null) {
            throw new RuntimeException(methodName+"方法不存在");
        }
        return method.invoke(object,args);
    }

    public static Method getMethodByName(Class<?> clazz,String methodName){
        Method[] declaredMethods = clazz.getDeclaredMethods();
        List<Method> collect = Arrays.stream(declaredMethods).filter(method -> method.getName().equals(methodName)).collect(Collectors.toList());
        return collect.isEmpty()?null:collect.get(0);
    }

    public static Resource convertFileResource(URL url){
        return new UrlResource(url);
    }

    public static boolean isJarProtocol(String protocol){
        return protocol.startsWith(URL_PROTOCOL_JAR);
    }

    public static boolean isFileProtocol(String protocol){
        return protocol.startsWith(URL_PROTOCOL_FILE);
    }

    public static boolean isExistsAndFileAndCanRead(File file){
        if(!file.exists()){
            return false;
        }
        if(!file.isDirectory()){
            return false;
        }
        if(!file.canRead()){
            return false;
        }
        return true;
    }




}
