package Steps;

import Pages.GoibiboExcel;
import Pages.GoibiboFlightSelection;
import Pages.GoibiboHomePage;
import Utility.GoibiboException;
import Utility.Screenshot;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.*;
import java.io.IOException;

public class FlightStep {
    // Variables for WebDriver, Goibibo website class, row, and automation type
    private final String PATH = "src/test/java/Excel/FlightData.xlsx";
    public static WebDriver driver = null;
    private GoibiboHomePage h;
    private GoibiboFlightSelection f;
    private int row;
    private int style;

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

        // Instantiate Goibibo pages for page elements, as well as excel data
        GoibiboExcel g = new GoibiboExcel(PATH, row);
        h = new GoibiboHomePage(driver, g);
        f = new GoibiboFlightSelection(driver);
        this.row = row;
    }

    @And("an option from is selected for One-Way, Roundtrip or Multi-City")
    public void anOptionFromIsSelectedForOneWayOrRoundtrip() throws GoibiboException, IOException {
        h.flightOption(PATH, row);
    }

    @And("a starting and final destination are entered")
    public void aStartingAndFinalDestinationAreEntered() throws GoibiboException, IOException {
        h.locations(PATH, row);
    }

    @And("a departure and return date are selected")
    public void aDepartureAndReturnDateAreSelected() throws IOException, GoibiboException {
        h.dates(PATH, row);
    }

    @And("the user selects the number of travelers and travel class")
    public void theUserSelectsTheNumberOfTravelersAndTravelClass() throws IOException, GoibiboException {
        h.usersAndClass(PATH, row);
    }

    @When("the user selects the Search button")
    public void theUserSelectsTheSearchButton() throws IOException, GoibiboException {
        h.search(PATH, row);

        style = h.getStyle();
    }

    @Then("the flight selection page should be displayed")
    public void theFlightSelectionPageShouldBeDisplayed() throws IOException, GoibiboException {
       f.flightSelection(PATH, row, style);
    }

    @Then("the fare details should be stored in the spreadsheet")
    public void theFareDetailsShouldBeStoredInTheSpreadsheet() throws IOException, GoibiboException, AWTException {
        f.fareDetails(PATH, row, style);

        //Screenshot for data validation
        Screenshot.screenCapture(row);
    }
}