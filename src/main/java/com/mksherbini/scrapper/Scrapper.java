package com.mksherbini.scrapper;

import com.mksherbini.scrapper.model.Environments;
import com.mksherbini.scrapper.util.DriverProvider;
import com.mksherbini.scrapper.util.FileDownloader;
import com.mksherbini.scrapper.util.Timer;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Scrapper {
    private final WebDriver driver = new DriverProvider().getDriver();

    private static final String SEARCH_URL_TEMPLATE = "http://localhost:7780/til/grep.jsp?file=/%s/%s/&searchStr=%s";
    private static final String FILE_URL_TEMPLATE = "http://localhost:7780/til/fileBrowse.jsp?file=/%s";


    public void findLogs(LocalDate date, String id, String env) {
        log.info("Starting search for logs using id: {}, and date: {}", id, date);
        var timer = new Timer(env + "/" + id);
        timer.mark();
        try {
            final List<String> files = findFilesInPage(createUrl(env, date, id));
            log.info("Found {} log file(s) in {}", files.size(), env);
            files.forEach(log::info);
            downloadFiles(env, date, files);
        } catch (Exception e) {
            log.error("web didn't respond correctly");
            cleanUp();
        } finally {
            timer.log();
        }
    }

    public void findLogs(LocalDate date, String id) {
        getEnvironmentsStream().forEach(env ->
                findLogs(date, id, env.name())
        );
    }

    private Stream<Environments> getEnvironmentsStream() {
        return Arrays.stream(Environments.values()).filter(env -> env.ordinal() != 0);
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

    private static void downloadFiles(String env, LocalDate date, List<String> files) {
        files.forEach(s -> FileDownloader.saveFile(s,
                env + "_" + date + "_" +
                        s.substring(s.lastIndexOf('/') + 1) + ".html"));
    }

    public void cleanUp() {
        driver.quit();
    }
}
