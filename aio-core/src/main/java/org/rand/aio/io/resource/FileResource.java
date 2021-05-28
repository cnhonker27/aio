package org.rand.aio.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileResource implements Resource{

    public final static String SEPARATOR="/";

    public final static String URL_PROTOCOL_FILE = "file";

    private File file;

    private Path filepath;

    public FileResource(File file){
        this.file=file;
        this.filepath=file.toPath();
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(filepath);
    }


    @Override
    public String getFileName() {
        return file.getName();
    }


    @Override
    public String getFilePath() {
        return this.file.getPath();
    }

    @Override
    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public boolean isFile() {
        return this.file.toURI().getScheme().equals(URL_PROTOCOL_FILE);
    }


    @Override
    public URL getURL() throws MalformedURLException {
        return file.toURI().toURL();
    }
}
