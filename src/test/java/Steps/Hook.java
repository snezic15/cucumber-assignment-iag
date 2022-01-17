package Steps;

import Pages.Goibibo;
import Utility.ExcelReader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.an.E;

public class Hook {
    @Before
    public void InitializeTest() {
        System.out.println("Begin Scenario");
    }

    @After
    public void TearDownTest(Scenario scenario) {
        //Quit driver after each scenario/example
        FlightStep.driver.quit();
        System.out.println("End Scenario");
    }
}
