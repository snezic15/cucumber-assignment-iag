import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/java/Features"},
        plugin = ("json:target/cucumber-reports/CucumberTestReport.json"), monochrome = true
)

public class Runner {
}