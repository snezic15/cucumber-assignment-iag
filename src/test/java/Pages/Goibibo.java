package Pages;

import Utility.ExcelReader;
import Utility.GoibiboException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Goibibo {
    //Page elements
    private final List<Map<String, String>> workbook;
    private final int row;
    private final int excelRow;
    private final String path;

    public Goibibo(String path, int row) throws IOException {
        ExcelReader reader = new ExcelReader();
        workbook = reader.getData(path, "Input");
        this.row = row;
        this.path = path;
        excelRow = row + 1;

        //Test print when needed
//        System.out.println("***Begin test dump");
//        for (Map<String, String> map : workbook) {
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                System.out.println(entry.getKey() + " - " + entry.getValue());
//            }
//        }
//        System.out.println("End test dump***");
    }

    //Getters
    public String getFlightType() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("Flight Type"));
    }

    public String getMultiExtra() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Extra Paths"));
        if (Integer.parseInt(temp) > 0 && Integer.parseInt(temp) < 4) return temp;
        else throw new GoibiboException("Invalid number of paths (should be between 1-3)", path, excelRow);
    }

    public String getDepartureLocation() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("From"));
    }

    public String getArrivalLocation(int x) throws GoibiboException, IOException {
        switch (x) {
            case 1: return checkLetter(workbook.get(row).get("Multi Destination"));
            case 2: return checkLetter(workbook.get(row).get("Multi Destination 2"));
            case 3: return checkLetter(workbook.get(row).get("Multi Destination 3"));
            default: return checkLetter(workbook.get(row).get("Destination"));
        }
    }

    public String getDepartureDate(int x) throws GoibiboException, IOException {
        String temp;

        switch (x) {
            case 1:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (2nd entry)", path, excelRow);

            case 2:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date 2")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (3rd entry)", path, excelRow);

            case 3:
                temp = checkDate(checkNum(workbook.get(row).get("Multi Destination Date 3")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (4th entry)", path, excelRow);

            default:
                temp = checkDate(checkNum(workbook.get(row).get("Departure Date")));
                if (temp.length() == 8) return temp;
                else throw new GoibiboException("Invalid departure date (1st entry)", path, excelRow);
        }
    }

    public String getReturnDate() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Return Date"));
        if (temp.length() == 8) return temp;
        else throw new GoibiboException("Invalid return date", path, excelRow);
    }

    public String getAdults() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Adults"));
        if (Integer.parseInt(temp) > 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of adults", path, excelRow);
    }

    public String getChildren() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Children"));
        if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of children", path, excelRow);
    }

    public String getInfants() throws GoibiboException, IOException {
        String temp = checkNum(workbook.get(row).get("Infants"));
        if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < 10) return temp;
        else throw new GoibiboException("Invalid number of infants", path, excelRow);
    }

    public String getClassType() throws GoibiboException, IOException {
        return checkLetter(workbook.get(row).get("Class"));
    }

    private String checkNum(String s) throws IOException, GoibiboException {
        if (Pattern.matches("^[0-9]*$", s)) return s;
        else throw new GoibiboException("Input contains non-numerical characters", path, excelRow);
    }

    private String checkLetter(String s) throws IOException, GoibiboException {
        if (Pattern.matches("^[a-zA-Z ]*$", s)) return s;
        else throw new GoibiboException("Input contains non-alphabetical characters", path, excelRow);
    }

    private String checkDate(String s) throws IOException, GoibiboException {
        try {
            DateFormat d = new SimpleDateFormat("ddMMyyyy");
            d.setLenient(false);
            d.parse(s);
            return s;
        }
        catch (ParseException e) {
            throw new GoibiboException("Date is not a valid format", path, excelRow);
        }
    }
}
