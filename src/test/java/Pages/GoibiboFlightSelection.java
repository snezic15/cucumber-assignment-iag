package Pages;

import Utility.ExcelReader;
import Utility.GoibiboException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

public class GoibiboFlightSelection {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public GoibiboFlightSelection(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void flightSelection(String path, int row, int style) throws GoibiboException, IOException {
        // Check for available flights. Select 'Book' button (different element ID for Multi and Oneway/Return types)
        if (style == 3) {
            if (driver.getPageSource().contains("Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", path, row);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("orange")));
            driver.findElement(By.className("orange")).click();
        } else {
            if (driver.getPageSource().contains("Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", path, row);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("srp-card-uistyles__BookButton-sc-3flq99-21")));
            driver.findElements(By.className("srp-card-uistyles__BookButton-sc-3flq99-21")).get(0).click();
        }
    }

    public void fareDetails(String path, int row, int style) throws GoibiboException, IOException {
        String[] ar = {"", "", "", ""};

        // Xpath elements change for multi flights, so change what to search for depending on that
        if (style == 3) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[3]/div[2]/span/span[1]/span")));
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", path, row);
            }

            ar[0] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[1]/div[2]/span[1]/span")).getText();
            ar[1] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[2]/div[2]/span[1]/span")).getText();
            ar[2] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[3]/div[2]/span/span[1]/span")).getText();
            ar[3] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[4]/span[2]/span/span/span")).getText();
        } else {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[2]/div[3]/div[2]/span")));
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", path, row);
            }

            ar[0] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(0).getText();
            ar[1] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(1).getText();
            ar[2] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(2).getText();
            ar[3] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[4]/div/div[2]/div/span")).getText();
        }

        setFareExcel(path, row, ar);
    }

    private void setFareExcel(String path, int row, String[] ar) throws IOException {
        // Once reached the end, print 'N' in Excel doc and output booking details
        ExcelReader ex = new ExcelReader();
        ex.setData(path, "Output", "N", "N/A", row, ar);
    }
}
