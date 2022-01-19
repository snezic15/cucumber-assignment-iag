package Utility;

import java.io.IOException;

public class GoibiboException extends Exception {

    public GoibiboException(String msg, String path, int row) throws IOException {
        super(msg);
        printErrorDoc(path, row, msg);
    }

    private void printErrorDoc(String path, int row, String msg) throws IOException {
        ExcelReader reader = new ExcelReader();
        reader.setData(path, "Output", "Y", msg, row);
    }
}
