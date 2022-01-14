package Steps;//package Steps;
//
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//
//public class FlightStepDepreciated {
//    private WebDriver driver = null;
//
//    @Given("the user navigates to website homepage")
//    public void theUserNavigatesToWebsiteHomepage() {
//        System.setProperty("webdriver.chrome.driver", "src/test/java/Drivers/chromedriver.exe");
//        driver = new ChromeDriver();
//
////        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
////        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
//        driver.manage().window().maximize();
//
//        driver.navigate().to("https://www.goibibo.com/");
//    }
//
//    @And("an option from is selected for One-Way or Roundtrip")
//    public void anOptionFromIsSelectedForOneWayOrRoundtrip() throws InterruptedException {
//        driver.findElement(By.id("roundTrip")).click();
//        Thread.sleep(2500);
//    }
//
//    @And("a starting and final destination are entered")
//    public void aStartingAndFinalDestinationAreEntered() throws Exception {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//
//        driver.findElement(By.id("gosuggest_inputSrc")).sendKeys("M");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
//        driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();
//
//        driver.findElement(By.id("gosuggest_inputDest")).sendKeys("Adelaide");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-autosuggest-1-suggestion--0")));
//        driver.findElement(By.id("react-autosuggest-1-suggestion--0")).click();
//
//        if (!driver.findElement(By.id("gosuggest_inputSrc")).getAttribute("value").contains("Melbourne"))
//            throw new Exception("Departure Location is incorrect");
//
//        if (driver.findElement(By.id("gosuggest_inputDest")).getAttribute("value").isEmpty())
//            throw new Exception("Arrival Location is blank");
//    }
//
//    @And("a departure and return date are selected")
//    public void aDepartureAndReturnDateAreSelected() throws InterruptedException {
//        String dep = "20220123";
//        String arr = "20220125";
//
//        driver.findElement(By.id("departureCalendar")).click();
//        driver.findElement(By.id("fare_" + dep)).click();
//
//        driver.findElement(By.id("returnCalendar")).click();
//        driver.findElement(By.id("fare_" + arr)).click();
//
//        Thread.sleep(2500);
//    }
//
//    @And("the user selects the number of travelers and travel class")
//    public void theUserSelectsTheNumberOfTravelersAndTravelClass() throws InterruptedException {
//        driver.findElement(By.id("pax_label")).click();
//
//        driver.findElement(By.id("adultPaxBox")).clear();
//        driver.findElement(By.id("adultPaxBox")).sendKeys("3");
//
//        Select s = new Select(driver.findElement(By.id("gi_class")));
//        s.selectByVisibleText("Business");
//
//        Thread.sleep(2500);
//    }
//
//    @And("the user selects the Search button")
//    public void theUserSelectsTheSearchButton() throws InterruptedException {
//        driver.findElement(By.id("gi_search_btn")).click();
//
//        driver.findElements(By.className("srp-card-uistyles__BookButton-sc-3flq99-21")).get(0).click();
//
//        Thread.sleep(2500);
//    }
//
//    @Then("the flight selection page should be displayed")
//    public void theFlightSelectionPageShouldBeDisplayed() {
//        driver.close();
//        driver.quit();
//    }
//}
