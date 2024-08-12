package com.bartheme.githubscanner.util;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileReader {
    public static String readJsonFile(String filePath) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + filePath);
        return new String(Files.readAllBytes(Paths.get(file.getPath())));
    }
}
