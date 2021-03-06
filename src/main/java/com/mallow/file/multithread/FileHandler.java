package com.mallow.file.multithread;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

/**
 * Created by lcy on 2017/2/20.
 */
public class FileHandler implements Runnable {
    private Path file;
    private BasicFileAttributes attrs;
    private Map fingerPrints;
    private Logger logger = LoggerFactory.getLogger(FileHandler.class);
    private int filterSize;

    public FileHandler(Path dir, BasicFileAttributes attrs, Map fingers, int filterSize) {
        this.file = dir;
        this.attrs = attrs;
        this.fingerPrints = fingers;
        this.filterSize = filterSize;
    }

    @Override
    public void run() {
        if (attrs.size() > filterSize * 1024 * 1024) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file.toFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // use md5 as file's finerPrint
            String md5 = null;
            try {
                md5 = DigestUtils.md5Hex(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fingerPrints.containsKey(md5)) {
                fingerPrints.put(md5, fingerPrints.get(md5) + "_" + file.toString());
                logger.info(System.currentTimeMillis() + " duplicate files: " + fingerPrints.get(md5));
            } else {
                fingerPrints.put(md5, file.toString());
            }
        }
    }
}
