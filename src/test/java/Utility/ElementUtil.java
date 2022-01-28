package Utility;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ElementUtil {
    public static void click(WebElement w) {
        w.click();
    }

    public static void sendKeys(WebElement w, String s) {
        w.sendKeys(s);
    }

    public static WebElement element(WebDriver d, By b) {
        return d.findElement(b);
    }

    public static void clear(WebElement w) {
        w.clear();
    }

    public static boolean contains(WebDriver d, String s) {
        return d.getPageSource().contains(s);
    }

    public static void wait(WebDriver d, WebElement w) {
        new WebDriverWait(d, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOf(w));
    }
}
