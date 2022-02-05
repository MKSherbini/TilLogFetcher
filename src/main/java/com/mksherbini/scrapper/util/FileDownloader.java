package com.mksherbini.scrapper.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
public class FileDownloader {
    private static final String DOWNLOAD_PATH = "logs/";
    private static final double BYTES_TO_MEGABYTES = 1.0 / (1024 * 1024);
    private static final int DOWNLOAD_INCREMENTS = 10485760;

    private FileDownloader() {
    }

    public static void saveFile(String url, String fileName) {
        File file = new File(FileDownloader.DOWNLOAD_PATH + fileName);
        if (file.exists()) {
            log.info("File {} already exists. Skipping...", fileName);
            return;
        }
        log.info("Downloading {}...", fileName);

        // todo add progress bar?
        try (var input = new BufferedInputStream(new URL(url).openStream())) {
            int totalDownloaded = 0;

            while (input.available() > 0) {
                var bytes = input.readNBytes(FileDownloader.DOWNLOAD_INCREMENTS);
                totalDownloaded += bytes.length;
                Files.write(file.toPath(), bytes, CREATE, APPEND);
                log.info("Downloaded {} mb to {}",
                        totalDownloaded * FileDownloader.BYTES_TO_MEGABYTES,
                        fileName);
            }
        } catch (IOException e) {
            log.error("File saving failed");
        }
    }
}
