package Pages;

import Utility.ElementUtil;
import Utility.GoibiboException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GoibiboHomePage {
    private int style;
    private final GoibiboExcel g;
    private final WebDriver driver;

    //Option
    @FindBy(id = "oneway")
    private WebElement oneway;

    @FindBy(id = "roundTrip")
    private WebElement returnTrip;

    @FindBy(id = "multiCity")
    private WebElement multi;

    //Location
    @FindBy(id = "gosuggest_inputSrc")
    private WebElement start;

    @FindBy(id = "react-autosuggest-1-suggestion--0")
    private WebElement auto;

    @FindBy(className = "padL5")
    private WebElement nextLoc;

    @FindBy(id = "gosuggest_inputDest")
    private List<WebElement> dest;

    //Dates
    @FindBy(id = "departureCalendar")
    private List<WebElement> depCal;

    @FindBy(id = "returnCalendar")
    private WebElement retCal;

    @FindBy(css = "[aria-label='Next Month']")
    private WebElement nextMonth;

    //People/Class
    @FindBy(id = "pax_label")
    private WebElement tab;

    @FindBy(id = "adultPaxBox")
    private WebElement adult;

    @FindBy(id = "childPaxBox")
    private WebElement child;

    @FindBy(id = "infantPaxBox")
    private WebElement infant;

    @FindBy(id = "gi_class")
    private WebElement fliClass;

    //Submit
    @FindBy(id = "gi_search_btn")
    private WebElement search;

    public GoibiboHomePage(WebDriver driver, GoibiboExcel g) {
        this.driver = driver;
        this.g = g;
        PageFactory.initElements(driver, this);
    }

    public void flightOption(String path, int row) throws GoibiboException, IOException {
        // Different cases for flight type
        switch (g.getFlightType()) {
            case "Oneway":
                ElementUtil.click(oneway);
                style = 1;
                return;
            case "Return":
                ElementUtil.click(returnTrip);
                style = 2;
                return;
            case "Multi":
                ElementUtil.click(multi);
                style = 3;
                return;
            default:
                throw new GoibiboException("Flight type not found", path, row);
        }
    }

    public void locations(String path, int row) throws GoibiboException, IOException {
        int y = 0;

        // Find search element, fill with Excel data, select first result. Finally, check if selection contains
        // original input to ensure correct option selection
        ElementUtil.sendKeys(start, g.getDepartureLocation());

        try {
            ElementUtil.wait(driver, auto);
            ElementUtil.click(auto);
        } catch (TimeoutException e) {
            throw new GoibiboException("Autosuggest element for departure location not found. Timeout", path, row);
        }

        if (!start.getAttribute("value").contains(g.getDepartureLocation()))
            throw new GoibiboException("Departure location does not match dataset", path, row);

        if (style == 3 && Integer.parseInt(g.getMultiExtra()) > 0) y = Integer.parseInt(g.getMultiExtra());

        for (int i = 0; i <= y; i++) {
            if (i > 1) ElementUtil.click(nextLoc);

            ElementUtil.sendKeys(dest.get(i), g.getArrivalLocation(i));

            try {
                ElementUtil.wait(driver, auto);
                ElementUtil.click(auto);
            } catch (TimeoutException e) {
                throw new GoibiboException("Autosuggest element for arrival location" + (i + 1) + " not found. Timeout", path, row);
            }

            if (!dest.get(i).getAttribute("value").contains(g.getArrivalLocation(i)))
                throw new GoibiboException("Arrival location " + (i + 1) + " does not match dataset", path, row);
        }
    }

    public void dates(String path, int row) throws GoibiboException, IOException {
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
            ElementUtil.click(depCal.get(i));

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
                    ElementUtil.click(nextMonth);
                }
            }

            //Click requested date
            ElementUtil.click(ElementUtil.element(driver, By.id("fare_" + depFare)));

            //Validate input
            d = LocalDate.parse(year + "-" + month + "-" + day);
            if (!depCal.get(i).getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)))
                throw new GoibiboException("Departure date " + (i + 1) + " does not match dataset", path, row);
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
                ElementUtil.click(nextMonth);
            }
        }

        ElementUtil.click(ElementUtil.element(driver, By.id("fare_" + retFare)));

        //Validate input
        d = LocalDate.parse(year + "-" + month + "-" + day);
        if (!retCal.getAttribute("value").contains(day + " " + MMMM.format(d).substring(0, 3)))
            throw new GoibiboException("Return date does not match dataset", path, row);
    }

    public void usersAndClass(String path, int row) throws GoibiboException, IOException {
        ElementUtil.click(tab);

        // Set traveller details
        ElementUtil.clear(adult);
        ElementUtil.sendKeys(adult, g.getAdults());
        if (!adult.getAttribute("value").contains(g.getAdults()))
            throw new GoibiboException("Number of adults does not match dataset", path, row);

        ElementUtil.clear(child);
        ElementUtil.sendKeys(child, g.getChildren());
        if (!child.getAttribute("value").contains(g.getChildren()))
            throw new GoibiboException("Number of children does not match dataset", path, row);

        ElementUtil.clear(infant);
        ElementUtil.sendKeys(infant, g.getInfants());
        if (!infant.getAttribute("value").contains(g.getInfants()))
            throw new GoibiboException("Number of infants does not match dataset", path, row);

        // Select option from dropdown that matches data and assert correct option has been selected
        Select s = new Select(fliClass);
        int i = 0;
        for (WebElement option : s.getOptions()) {
            if (option.getText().equalsIgnoreCase(g.getClassType())) break;
            i++;
        }

        s.selectByIndex(i);
        if (!s.getFirstSelectedOption().getText().equalsIgnoreCase(g.getClassType()))
            throw new GoibiboException("Flight class does not match dataset", path, row);
    }

    public void search(String path, int row) throws GoibiboException, IOException {
        // Search
        ElementUtil.click(search);

        if (ElementUtil.contains(driver, "Please enter a valid"))
            throw new GoibiboException("All inputs are not filled adequately", path, row);
    }

    public int getStyle() {
        return style;
    }
}
