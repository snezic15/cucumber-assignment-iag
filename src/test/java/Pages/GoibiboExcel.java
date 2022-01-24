package Pages;

import Utility.ExcelReader;
import Utility.GoibiboException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class GoibiboExcel {
    //Page elements
    private final List<Map<String, String>> workbook;
    private final int row;
    private final String path;

    public GoibiboExcel(String path, int row) throws IOException, GoibiboException {
        ExcelReader reader = new ExcelReader();
        workbook = reader.getData(path, "Input");
        this.row = row;
        this.path = path;

        //Test print when needed
//        System.out.println("***Begin test dump");
//        for (Map<String, String> map : workbook) {
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                System.out.println(entry.getKey() + " - " + entry.getValue());
//            }
//        }
//        System.out.println("End test dump***");

        //Bootleg way to check if row is blank
        try {
            if (Objects.equals(workbook.get(row).get("Flight Type"), ""))
                throw new GoibiboException("Row " + this.row + " is empty", path, this.row);
        } catch (IndexOutOfBoundsException e) {
            throw new GoibiboException("Row " + this.row + " does not exist", path, this.row);
        }
    }

    //Getters
    public String getFlightType() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("Flight Type"));
    }

    public String getMultiExtra() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Extra Paths"));
        if (Integer.parseInt(temp) > 0 && Integer.parseInt(temp) < 4) return temp;
        else throw new GoibiboException("Invalid number of paths (should be between 1-3)", path, row);
    }

    public String getDepartureLocation() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("From"));
    }

    public String getArrivalLocation(int x) throws GoibiboException, IOException {
        switch (x) {
            case 1:
                return checkLetter(workbook.get(row).get("Multi Destination"));
            case 2:
                return checkLetter(workbook.get(row).get("Multi Destination 2"));
            case 3:
                return checkLetter(workbook.get(row).get("Multi Destination 3"));
            default:
                return checkLetter(workbook.get(row).get("Destination"));
        }
    }

    public String getDepartureDate(int x) throws GoibiboException, IOException {
        String temp;

        switch (x) {
            case 1:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (2nd entry)", path, row);

            case 2:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date 2")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (3rd entry)", path, row);

            case 3:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date 3")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (4th entry)", path, row);

            default:
                temp = checkDate(checkNum(workbook.get(row).get("Departure Date")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (1st entry)", path, row);
        }
    }

    public String getReturnDate() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Return Date"));
        if (temp.length() == 8) return temp;
        else throw new GoibiboException("Invalid return date", path, row);
    }

    public String getAdults() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Adults"));
        if (Integer.parseInt(temp) > 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of adults", path, row);
    }

    public String getChildren() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Children"));
        if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of children", path, row);
    }

    public String getInfants() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Infants"));
        if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of infants", path, row);
    }

    public String getClassType() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("Class"));
    }

    private String checkNum(String s) throws IOException, GoibiboException {
        if (Pattern.matches("^[0-9]*$", s)) return s;
        else throw new GoibiboException("Input contains non-numerical characters", path, row);
    }

    private String checkLetter(String s) throws IOException, GoibiboException {
        if (Pattern.matches("^[a-zA-Z ]*$", s)) return s;
        else throw new GoibiboException("Input contains non-alphabetical characters", path, row);
    }

    private String checkDate(String s) throws IOException, GoibiboException {
        try {
            DateFormat d = new SimpleDateFormat("ddMMyyyy");
            d.setLenient(false);
            d.parse(s);
            return s;
        } catch (ParseException e) {
            throw new GoibiboException("Date is not a valid format", path, row);
        }
    }
}