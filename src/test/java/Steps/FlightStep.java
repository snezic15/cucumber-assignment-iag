package Steps;


import Pages.Goibibo;
import Utility.GoibiboException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FlightStep {
    // Variables for WebDriver, Goibibo website class, and automation type
    private static final String PATH = "/Users/Samuel Nezic/Desktop/FlightData.xlsx";
    public static WebDriver driver = null;
    private Goibibo g;
    private int style;

    @Given("the user navigates to website homepage using data from spreadsheet row {int}")
    public void theUserNavigatesToWebsiteHomepage(int row) throws IOException {
        // Instantiate driver and nagivate to website
        System.setProperty("webdriver.chrome.driver", "src/test/java/Drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("https://www.goibibo.com/");

        // Website has two variants that completely change the layout/element ID's. Seems to be a coinflip which
        // version loads by default so keep restarting scenario until the variant chosen to automate loads
        if (!driver.getPageSource().contains("FlightHomeOldWidget")) {
            driver.quit();
            theUserNavigatesToWebsiteHomepage(row);
        }

        // Instantiate Goibibo website class for page elements
        g = new Goibibo(PATH, row);
    }

    @And("an option from is selected for One-Way, Roundtrip or Multi-City")
    public void anOptionFromIsSelectedForOneWayOrRoundtrip() {
        // Check flight type, set accordingly as it effects steps required
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
        driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();

        if (!driver.findElement(By.id("gosuggest_inputSrc")).getAttribute("value").contains(g.getDepartureLocation()))
            throw new GoibiboException("Incorrect Departure Location Selected", PATH, 1);

        if (style == 3 && Integer.parseInt(g.getMultiExtra()) > 0) y = Integer.parseInt(g.getMultiExtra());

        for (int i = 0; i <= y; i++) {
            if (i > 1) driver.findElement(By.className("padL5")).click();

            driver.findElements(By.id("gosuggest_inputDest")).get(i).sendKeys(g.getArrivalLocation(i));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
            driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();

            if (!driver.findElements(By.id("gosuggest_inputDest")).get(i).getAttribute("value").contains(g.getArrivalLocation(i)))
                throw new GoibiboException("Incorrect Departure Location Selected", PATH, 1);
        }
    }

    @And("a departure and return date are selected")
    public void aDepartureAndReturnDateAreSelected() {
        int diff, y = 0;
        String day, month, year;
        DateTimeFormatter mm = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yy = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter MMMM = DateTimeFormatter.ofPattern("MMMM");
        LocalDateTime now = LocalDateTime.now();
        LocalDate d;

        if (style == 3 && Integer.parseInt(g.getMultiExtra()) > 0) y = Integer.parseInt(g.getMultiExtra());

        for (int i = 0; i <= y; i++) {
            driver.findElements(By.id("departureCalendar")).get(i).click();

            day = g.getDepatureDate(i).substring(0, 2);
            month = g.getDepatureDate(i).substring(2, 4);
            year = g.getDepatureDate(i).substring(4);
            String depFare =
                    g.getDepatureDate(i).substring(4) + g.getDepatureDate(i).substring(2, 4) + g.getDepatureDate(i).substring(0, 2);

            if (i > 0) {
                diff = Integer.parseInt(month) - Integer.parseInt(g.getDepatureDate(i-1).substring(2, 4));
                diff = Integer.parseInt(year) > Integer.parseInt(g.getDepatureDate(i-1).substring(4)) ?
                        diff + (12 * (Integer.parseInt(year) - Integer.parseInt(g.getDepatureDate(i-1).substring(4)))) : diff;
            }

            else {
                diff = Integer.parseInt(month) - Integer.parseInt(mm.format(now));
                diff = Integer.parseInt(year) > Integer.parseInt(yy.format(now)) ? diff + (12 * (Integer.parseInt(year) - Integer.parseInt(yy.format(now)))) : diff;
            }

            if (diff != 0) {
                for (int x = 0; x < diff; x++) {
                    driver.findElement(By.cssSelector("[aria-label='Next Month']")).click();
                }
            }

            driver.findElement(By.id("fare_" + depFare)).click();

            d = LocalDate.parse(year + "-" + month + "-" + day);
            assertTrue(driver.findElements(By.id("departureCalendar")).get(i).getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)));
        }

        if (style != 2) return;

        day = g.getReturnDate().substring(0, 2);
        month = g.getReturnDate().substring(2, 4);
        year = g.getReturnDate().substring(4);
        String retFare = g.getReturnDate().substring(4) + g.getReturnDate().substring(2, 4) + g.getReturnDate().substring(0, 2);

        diff = Integer.parseInt(month) - Integer.parseInt(mm.format(now));
        diff = Integer.parseInt(year) > Integer.parseInt(yy.format(now)) ? diff + (12 * (Integer.parseInt(year) - Integer.parseInt(yy.format(now)))) : diff;

        if (diff != 0) {
            for (int i = 0; i < diff; i++) {
                driver.findElement(By.cssSelector("[aria-label='Next Month']")).click();
            }
        }

        driver.findElement(By.id("fare_" + retFare)).click();

        MMMM = DateTimeFormatter.ofPattern("MMMM");
        d = LocalDate.parse(year + "-" + month + "-" + day);
        assertTrue(driver.findElement(By.id("returnCalendar")).getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)));
    }

    @And("the user selects the number of travelers and travel class")
    public void theUserSelectsTheNumberOfTravelersAndTravelClass() {
        // Select label
        driver.findElement(By.id("pax_label")).click();

        // Set traveller details
        driver.findElement(By.id("adultPaxBox")).clear();
        driver.findElement(By.id("adultPaxBox")).sendKeys(g.getAdults());
        assertTrue(driver.findElement(By.id("adultPaxBox")).getAttribute("value").contains(g.getAdults()));

        driver.findElement(By.id("childPaxBox")).clear();
        driver.findElement(By.id("childPaxBox")).sendKeys(g.getChildren());
        assertTrue(driver.findElement(By.id("childPaxBox")).getAttribute("value").contains(g.getChildren()));

        driver.findElement(By.id("infantPaxBox")).clear();
        driver.findElement(By.id("infantPaxBox")).sendKeys(g.getInfants());
        assertTrue(driver.findElement(By.id("infantPaxBox")).getAttribute("value").contains(g.getInfants()));

        // Select option from dropdown that matches data and assert correct option has been selected
        Select s = new Select(driver.findElement(By.id("gi_class")));
        int i = 0;
        for (WebElement option : s.getOptions()) {
            if (option.getText().equalsIgnoreCase(g.getClassType()))
                break;
            i++;
        }

        s.selectByIndex(i);
        assertTrue(s.getFirstSelectedOption().getText().equalsIgnoreCase(g.getClassType()));
    }

    @And("the user selects the Search button")
    public void theUserSelectsTheSearchButton() {
        // Press search
        driver.findElement(By.id("gi_search_btn")).click();
    }

    @Then("the flight selection page should be displayed")
    public void theFlightSelectionPageShouldBeDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        if (style == 3) {
            try {
                assertFalse(driver.getPageSource().contains("Sorry, we could not find any flights for this route"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("orange")));
                driver.findElement(By.className("orange")).click();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {
            try {
                assertFalse(driver.getPageSource().contains("Sorry, we could not find any flights for this route"));
                driver.findElements(By.className("srp-card-uistyles__BookButton-sc-3flq99-21")).get(0).click();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
