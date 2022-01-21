package Steps;

import Pages.Goibibo;
import Utility.ExcelReader;
import Utility.GoibiboException;
import Utility.Screenshot;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightStep {
    // Variables for WebDriver, Goibibo website class, row, and automation type
    private final String PATH = "src/test/java/Excel/FlightData.xlsx";
    public static WebDriver driver = null;
    private Goibibo g;
    private int style;
    private int row;

    @Given("the user navigates to website homepage using data from spreadsheet row {int}")
    public void theUserNavigatesToWebsiteHomepage(int row) throws IOException, GoibiboException {
        // Instantiate driver and navigate to website
        System.setProperty("webdriver.chrome.driver", "src/test/java/Drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("https://www.goibibo.com/");

        // Website has two variants that completely change the layout/element ID's. Seems to be a coin-flip which
        // version loads by default so keep restarting scenario until the variant chosen to automate loads
        if (driver.getPageSource().contains("FlightHomeNewWidget")) {
            driver.quit();
            theUserNavigatesToWebsiteHomepage(row);
        }

        // Instantiate Goibibo website class for page elements
        g = new Goibibo(PATH, row);
        this.row = row;
    }

    @And("an option from is selected for One-Way, Roundtrip or Multi-City")
    public void anOptionFromIsSelectedForOneWayOrRoundtrip() throws GoibiboException, IOException {
        // Different cases for flight type
        switch (g.getFlightType()) {
            case "Oneway":
                driver.findElement(By.id("oneway")).click();
                style = 1;
                return;
            case "Return":
                driver.findElement(By.id("roundTrip")).click();
                style = 2;
                return;
            case "Multi":
                driver.findElement(By.id("multiCity")).click();
                style = 3;
                return;
            default:
                throw new GoibiboException("Flight type not found", PATH, row);
        }
    }

    @And("a starting and final destination are entered")
    public void aStartingAndFinalDestinationAreEntered() throws GoibiboException, IOException {
        // New wait variable for slow-loading elements (autocomplete takes time occasionally)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int y = 0;

        // Find search element, fill with Excel data, select first result. Finally, check if selection contains
        // original input to ensure correct option selection
        driver.findElement(By.id("gosuggest_inputSrc")).sendKeys(g.getDepartureLocation());

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
            driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();
        } catch (TimeoutException e) {
            throw new GoibiboException("Autosuggest element for departure location not found. Timeout", PATH, row);
        }

        if (!driver.findElement(By.id("gosuggest_inputSrc")).getAttribute("value").contains(g.getDepartureLocation()))
            throw new GoibiboException("Departure location does not match dataset", PATH, row);

        if (style == 3 && Integer.parseInt(g.getMultiExtra()) > 0) y = Integer.parseInt(g.getMultiExtra());

        for (int i = 0; i <= y; i++) {
            if (i > 1) driver.findElement(By.className("padL5")).click();

            driver.findElements(By.id("gosuggest_inputDest")).get(i).sendKeys(g.getArrivalLocation(i));

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
                driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();
            } catch (TimeoutException e) {
                throw new GoibiboException("Autosuggest element for arrival location" + (i + 1) + " not found. Timeout", PATH, row);
            }

            if (!driver.findElements(By.id("gosuggest_inputDest")).get(i).getAttribute("value").contains(g.getArrivalLocation(i)))
                throw new GoibiboException("Arrival location " + (i + 1) + " does not match dataset", PATH, row);
        }
    }

    @And("a departure and return date are selected")
    public void aDepartureAndReturnDateAreSelected() throws IOException, GoibiboException {
        //TL:DR Get current date, break requested date in day/month/year substrings, determine clicks from requested
        // month - current month (or requested month -
        // previous requested month for returns/multi city flights), do the same for years (if required), perform
        // required clicks, validate date, repeat if necessary
        int diff, y = 0;
        String day, month, year;
        DateTimeFormatter mm = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yy = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter MMMM = DateTimeFormatter.ofPattern("MMMM");
        LocalDateTime now = LocalDateTime.now();
        LocalDate d;

        //Check for multi to determine number of loops
        if (style == 3) y = Integer.parseInt(g.getMultiExtra());

        //Loop for multi
        for (int i = 0; i <= y; i++) {
            driver.findElements(By.id("departureCalendar")).get(i).click();

            //Substring breakdown
            day = g.getDepartureDate(i).substring(0, 2);
            month = g.getDepartureDate(i).substring(2, 4);
            year = g.getDepartureDate(i).substring(4);
            //Rearrange to match page element ID
            String depFare = g.getDepartureDate(i).substring(4) + g.getDepartureDate(i).substring(2, 4) + g.getDepartureDate(i).substring(0, 2);

            //Calculate number of clicks for month/year change. If Multi, use previous requested date. If
            //oneway/return, use current date
            if (i > 0) {
                diff = Integer.parseInt(month) - Integer.parseInt(g.getDepartureDate(i - 1).substring(2, 4));
                diff = Integer.parseInt(year) > Integer.parseInt(g.getDepartureDate(i - 1).substring(4)) ? diff + (12 * (Integer.parseInt(year) - Integer.parseInt(g.getDepartureDate(i - 1).substring(4)))) : diff;
            } else {
                diff = Integer.parseInt(month) - Integer.parseInt(mm.format(now));
                diff = Integer.parseInt(year) > Integer.parseInt(yy.format(now)) ? diff + (12 * (Integer.parseInt(year) - Integer.parseInt(yy.format(now)))) : diff;
            }

            //Perform clicks
            if (diff != 0) {
                for (int x = 0; x < diff; x++) {
                    driver.findElement(By.cssSelector("[aria-label='Next Month']")).click();
                }
            }

            //Click requested date
            driver.findElement(By.id("fare_" + depFare)).click();

            //Validate input
            d = LocalDate.parse(year + "-" + month + "-" + day);
            if (!driver.findElements(By.id("departureCalendar")).get(i).getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)))
                throw new GoibiboException("Departure date " + (i + 1) + " does not match dataset", PATH, row);
        }

        //Exit if not return (oneway/multi do not have return flight)
        if (style != 2) return;

        //Repeat above process for return details
        day = g.getReturnDate().substring(0, 2);
        month = g.getReturnDate().substring(2, 4);
        year = g.getReturnDate().substring(4);
        String retFare = g.getReturnDate().substring(4) + g.getReturnDate().substring(2, 4) + g.getReturnDate().substring(0, 2);

        //Use departure date instead of current date
        diff = Integer.parseInt(month) - Integer.parseInt(g.getDepartureDate(0).substring(2, 4));
        diff = Integer.parseInt(year) > Integer.parseInt(g.getDepartureDate(0).substring(4)) ? diff + (12 * (Integer.parseInt(year) - Integer.parseInt(g.getDepartureDate(0).substring(4)))) : diff;

        //Perform clicks
        if (diff != 0) {
            for (int i = 0; i < diff; i++) {
                driver.findElement(By.cssSelector("[aria-label='Next Month']")).click();
            }
        }

        driver.findElement(By.id("fare_" + retFare)).click();

        //Validate input
        d = LocalDate.parse(year + "-" + month + "-" + day);
        if (!driver.findElement(By.id("returnCalendar")).getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)))
            throw new GoibiboException("Return date does not match dataset", PATH, row);
    }

    @And("the user selects the number of travelers and travel class")
    public void theUserSelectsTheNumberOfTravelersAndTravelClass() throws IOException, GoibiboException {
        driver.findElement(By.id("pax_label")).click();

        // Set traveller details
        driver.findElement(By.id("adultPaxBox")).clear();
        driver.findElement(By.id("adultPaxBox")).sendKeys(g.getAdults());
        if (!driver.findElement(By.id("adultPaxBox")).getAttribute("value").contains(g.getAdults()))
            throw new GoibiboException("Number of adults does not match dataset", PATH, row);

        driver.findElement(By.id("childPaxBox")).clear();
        driver.findElement(By.id("childPaxBox")).sendKeys(g.getChildren());
        if (!driver.findElement(By.id("childPaxBox")).getAttribute("value").contains(g.getChildren()))
            throw new GoibiboException("Number of children does not match dataset", PATH, row);

        driver.findElement(By.id("infantPaxBox")).clear();
        driver.findElement(By.id("infantPaxBox")).sendKeys(g.getInfants());
        if (!driver.findElement(By.id("infantPaxBox")).getAttribute("value").contains(g.getInfants()))
            throw new GoibiboException("Number of infants does not match dataset", PATH, row);

        // Select option from dropdown that matches data and assert correct option has been selected
        Select s = new Select(driver.findElement(By.id("gi_class")));
        int i = 0;
        for (WebElement option : s.getOptions()) {
            if (option.getText().equalsIgnoreCase(g.getClassType())) break;
            i++;
        }

        s.selectByIndex(i);
        if (!s.getFirstSelectedOption().getText().equalsIgnoreCase(g.getClassType()))
            throw new GoibiboException("Flight class does not match dataset", PATH, row);
    }

    @When("the user selects the Search button")
    public void theUserSelectsTheSearchButton() throws IOException, GoibiboException {
        // Search
        driver.findElement(By.id("gi_search_btn")).click();

        if (driver.getPageSource().contains("Please enter a valid"))
            throw new GoibiboException("All inputs are not filled adequately", PATH, row);
    }

    @Then("the flight selection page should be displayed")
    public void theFlightSelectionPageShouldBeDisplayed() throws IOException, GoibiboException {
        // Check for available flights. Select 'Book' button (different element ID for Multi and Oneway/Return types)
        if (style == 3) {
            if (driver.getPageSource().contains("Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", PATH, row);

            driver.findElement(By.className("orange")).click();
        } else {
            if (driver.getPageSource().contains("Sorry, we could not find any flights for this route"))
                throw new GoibiboException("No flight results", PATH, row);

            driver.findElements(By.className("srp-card-uistyles__BookButton-sc-3flq99-21")).get(0).click();
        }
    }

    @Then("the fare details should be stored in the spreadsheet")
    public void theFareDetailsShouldBeStoredInTheSpreadsheet() throws IOException, GoibiboException, AWTException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String[] ar = {"", "", "", ""};

        // Xpath elements change for multi flights, so change what to search for depending on that
        if (style == 3) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[3]/div[2]/span/span[1]/span")));
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", PATH, row);
            }

            ar[0] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[1]/div[2]/span[1]/span")).getText();
            ar[1] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[2]/div[2]/span[1]/span")).getText();
            ar[2] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[2]/div[3]/div[2]/span/span[1]/span")).getText();
            ar[3] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[1]/div[4]/span[2]/span/span/span")).getText();
        } else {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[2]/div[3]/div[2]/span")));
            } catch (TimeoutException e) {
                throw new GoibiboException("Fare elements not found. Timeout", PATH, row);
            }

            ar[0] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(0).getText();
            ar[1] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(1).getText();
            ar[2] = driver.findElements(By.xpath("//span[@class='padR5 font18']")).get(2).getText();
            ar[3] = driver.findElement(By.xpath("//*[@id=\"fareSummary\"]/div[1]/div[4]/div/div[2]/div/span")).getText();
        }

        // Once reached the end, print 'N' in Excel doc and output booking details
        ExcelReader ex = new ExcelReader();
        ex.setData(PATH, "Output", "N", "N/A", row, ar);

        //Screenshot for data validation
        Screenshot.screenCapture(row);
    }
}