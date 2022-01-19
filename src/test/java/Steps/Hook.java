package Steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hook {
    @Before
    public void InitializeTest() {
        System.out.println("Begin Scenario");
    }

    @After
    public void TearDownTest() {
        //Quit driver after each scenario/example
        FlightStep.driver.quit();
        System.out.println("End Scenario");
    }
}
