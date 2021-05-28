package org.rand.aio.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class UrlResource implements Resource{
    public final static String SEPARATOR="/";

    public final static String URL_PROTOCOL_FILE = "file";

    private URL url;


    public UrlResource(URL url){
        this.url=url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection = url.openConnection();
        try {
            return urlConnection.getInputStream();
        } catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
            throw ex;
        }
    }


    @Override
    public String getFileName() {
        String path = this.url.getPath();
        int i = path.lastIndexOf(SEPARATOR);
        return path.substring(i+1);
    }


    @Override
    public String getFilePath() {
        return this.url.getPath();
    }

    @Override
    public String getAbsolutePath() {
        return this.url.getPath();
    }

    @Override
    public File getFile() {
        URI uri = null;
        try {
            uri = this.url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String schemeSpecificPart = uri.getSchemeSpecificPart();
        return new File(schemeSpecificPart);
    }

    @Override
    public boolean isFile() {
        try {
            return this.url.toURI().getScheme().equals(URL_PROTOCOL_FILE);
        } catch (URISyntaxException e) {
            return false;
        }
    }


    @Override
    public URL getURL() {
        return this.url;
    }
}
