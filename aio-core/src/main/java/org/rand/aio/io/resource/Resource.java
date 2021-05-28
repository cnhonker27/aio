package org.rand.aio.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public interface Resource {

    URL getURL() throws IOException;

    InputStream getInputStream() throws IOException;

    String getFileName();

    String getFilePath();

    String getAbsolutePath();

    File getFile();

    boolean isFile();
}
