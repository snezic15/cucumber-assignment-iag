package Pages;

import Utility.ElementUtil;
import Utility.ExcelReader;
import Utility.GoibiboException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;
import java.util.List;

public class GoibiboFlightSelection {
    private final WebDriver driver;

    //Flight selection
    @FindBy(className = "srp-card-uistyles__BookButton-sc-3flq99-21")
    private WebElement book;

    @FindBy(className = "orange")
    private WebElement bookMulti;

    //Fare details
    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[1]/div[2]/span[1]/span")
    private WebElement baseMulti;

    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[2]/div[2]/span[1]/span")
    private WebElement feeMulti;

    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[3]/div[2]/span/span[1]/span")
    private WebElement addonMulti;

    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[1]/div[4]/span[2]/span/span/span")
    private WebElement totalMulti;

    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[2]/div[3]/div[2]/span")
    private WebElement addon;

    @FindBy(xpath = "//span[@class='padR5 font18']")
    private List<WebElement> fare;

    @FindBy(xpath = "//*[@id=\"fareSummary\"]/div[1]/div[4]/div/div[2]/div/span")
    private WebElement total;

    public GoibiboFlightSelection(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void flightSelection(String path, int row, int style) throws GoibiboException, IOException {
        // Check for available flights. Select 'Book' button (different element ID for Multi and Oneway/Return types)
        if (style == 3) {
            if (ElementUtil.contains(driver, "Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", path, row);
            ElementUtil.wait(driver, bookMulti);
            ElementUtil.click(bookMulti);
        }

        else {
            if (ElementUtil.contains(driver, "Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", path, row);

            ElementUtil.wait(driver, book);
            ElementUtil.click(book);
        }
    }

    public void fareDetails(String path, int row, int style) throws GoibiboException, IOException {
        String[] ar = {"", "", "", ""};

        // Xpath of elements change for multi flights, so change what to search for depending on that
        if (style == 3) {
            try {
                ElementUtil.wait(driver, addonMulti);
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", path, row);
            }

            ar[0] = baseMulti.getText();
            ar[1] = feeMulti.getText();
            ar[2] = addonMulti.getText();
            ar[3] = totalMulti.getText();
        }

        else {
            try {
                ElementUtil.wait(driver, addon);
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", path, row);
            }

            ar[0] = fare.get(0).getText();
            ar[1] = fare.get(1).getText();
            ar[2] = fare.get(2).getText();
            ar[3] = total.getText();
        }

        setFareExcel(path, row, ar);
    }

    private void setFareExcel(String path, int row, String[] ar) throws IOException {
        // Once reached the end, print 'N' in Excel doc and output booking details
        ExcelReader.setData(path, "Output", "N", "N/A", row, ar);
    }
}