package com.mksherbini.scrapper.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

public class DriverProvider {
    @Getter
    private final WebDriver driver;

    public DriverProvider() {
        this(false);
    }

    public DriverProvider(boolean isVisible) {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver(getFirefoxOptions(isVisible));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private EdgeOptions getFirefoxOptions(boolean isVisible) {
        var options = new EdgeOptions();
        options.setHeadless(!isVisible);
        return options;
    }
}
