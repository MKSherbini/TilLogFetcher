package com.mksherbini;

import com.mksherbini.DriverProvider;
import com.mksherbini.Timer;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
public class Scrapper {
    private final WebDriver driver = new DriverProvider().getDriver();

    private static final List<String> ENVIRONMENTS = List.of("T1_EMS", "T3_EMS", "T4_EMS", "T7_EMS");
    private static final String SEARCH_URL_TEMPLATE = "http://localhost:7780/til/grep.jsp?file=/%s/%s/&searchStr=%s";
    private static final String FILE_URL_TEMPLATE = "http://localhost:7780/til/fileBrowse.jsp?file=/%s";
    private static final String DOWNLOAD_PATH = "logs/";
    private static final double BYTES_TO_MEGABYTES = 1.0 / (1024 * 1024);

    public void findLogs(LocalDate date, String id, String env) {
        log.info("Starting search for logs using id: {}, and date: {}", id, date);
        var timer = new Timer(env + "/" + id);
        timer.mark();
        try {
            downloadFiles(env, findFilesInPage(createUrl(env, date, id)));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            cleanUp();
        } finally {
            timer.log();
        }
    }

    public void findLogs(LocalDate date, String id) {
        ENVIRONMENTS.forEach(env ->
                findLogs(date, id, env)
        );
    }

    private String createUrl(String env, LocalDate date, String id) {
        return String.format(SEARCH_URL_TEMPLATE, env,
                date.format(DateTimeFormatter.ofPattern("MMMyyyy/dd")),
                id);
    }

    private List<String> findFilesInPage(String url) {
        log.info("Scrapping url: {}", url);
        driver.get(url);
        return driver.findElements(By.xpath("//a[contains(@href, 'log')]")).stream().map(el -> String.format(FILE_URL_TEMPLATE, el.getText())).collect(Collectors.toList());
    }

    private void downloadFiles(String env, List<String> files) {
        log.info("Found {} log files in {}", files.size(), env);
        files.forEach(s -> saveFile(s, env + "_" + s.substring(s.lastIndexOf('/') + 1) + ".html"));
    }

    private void saveFile(String url, String fileName) {
        File file = new File(DOWNLOAD_PATH + fileName);
        if (file.exists()) {
            log.info("File {} already exists. Skipping...", fileName);
            return;
        }
        log.info("Downloading {}...", fileName);
        int total = 0;
        try (var input = new BufferedInputStream(new URL(url).openStream())) {
            while (input.available() > 0) {
                var bytes = input.readNBytes(10485760);
                total += bytes.length;
                Files.write(file.toPath(), bytes, CREATE, APPEND);
                log.info("Downloaded {} mb to {}", total * BYTES_TO_MEGABYTES, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        driver.quit();
    }
}
