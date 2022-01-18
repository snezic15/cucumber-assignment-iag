package Pages;

import Utility.ExcelReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GoibiboDepreciated {
    //Page elements
    private final String flightType;
    private final String multiExtra;
    private final String departureLocation;
    private final String arrivalLocation;
    private final String departureDate;
    private final String returnDate;
    private final String adults;
    private final String children;
    private final String infants;
    private final String classType;
    private final String arrivalLocation2;
    private final String departureDate2;
    private final String arrivalLocation3;
    private final String departureDate3;
    private final String arrivalLocation4;
    private final String departureDate4;

    public GoibiboDepreciated(String path, int row) throws IOException {
        ExcelReader reader = new ExcelReader();
        List<Map<String, String>> workbook = reader.getData(path, "Input");

        //Test print
//        System.out.println("***Begin test dump");
//        for (Map<String, String> map : workbook) {
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                System.out.println(entry.getKey() + " - " + entry.getValue());
//            }
//        }
//        System.out.println("End test dump***");

        //Set variables from excel doc
        flightType = workbook.get(row).get("Flight Type");
        multiExtra = workbook.get(row).get("Extra Paths");
        departureLocation = workbook.get(row).get("From");
        arrivalLocation = workbook.get(row).get("Destination");
        departureDate = workbook.get(row).get("Departure Date");
        returnDate = workbook.get(row).get("Return Date");
        adults = workbook.get(row).get("Adults");
        children = workbook.get(row).get("Children");
        infants = workbook.get(row).get("Infants");
        classType = workbook.get(row).get("Class");
        arrivalLocation2 = workbook.get(row).get("Multi Destination");
        departureDate2 = workbook.get(row).get("Multi Destination Date");
        arrivalLocation3 = workbook.get(row).get("Multi Destination 2");
        departureDate3 = workbook.get(row).get("Multi Destination Date 2");
        arrivalLocation4 = workbook.get(row).get("Multi Destination 3");
        departureDate4 = workbook.get(row).get("Multi Destination Date 3");
    }

    //Getters
    public String getFlightType() {
        return flightType;
    }

    public String getMultiExtra() {
        return multiExtra;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation(int x) {
        switch (x) {
            case 1: return arrivalLocation2;
            case 2: return arrivalLocation3;
            case 3: return arrivalLocation4;
            default: return arrivalLocation;
        }
    }

    public String getDepatureDate(int x) {
        switch (x) {
            case 1: return departureDate2;
            case 2: return departureDate3;
            case 3: return departureDate4;
            default: return departureDate;
        }
    }

    public String getReturnDate() {
        return returnDate;
    }

    public String getAdults() {
        return adults;
    }

    public String getChildren() {
        return children;
    }

    public String getInfants() {
        return infants;
    }

    public String getClassType() {
        return classType;
    }
}
