package es.jfp.localclientproject.utils;

import java.nio.file.Path;

public class StringProps {

    private static StringProps instance;

    private Path stringFile;

    private StringProps() {
        this.stringFile = Path.of("src/main/resources/es/jfp/localclientproject");
    }

    public static StringProps getInstance() {
        synchronized (StringProps.class) {
            if (instance == null) {
                instance = new StringProps();
            }
            return instance;
        }
    }



}
