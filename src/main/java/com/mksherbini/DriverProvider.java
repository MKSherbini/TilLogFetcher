package com.mksherbini;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

public class DriverProvider {
    @Getter
    private WebDriver driver;
    private boolean isVisible = false;

    public DriverProvider() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver(getFirefoxOptions());
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(1));
    }

    private EdgeOptions getFirefoxOptions() {
        var options = new EdgeOptions();
        options.setHeadless(!isVisible);
        return options;
    }
}
