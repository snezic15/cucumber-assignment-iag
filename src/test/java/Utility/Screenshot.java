package Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Screenshot {
    public static void screenCapture(int row) throws IOException, AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);

        String date = new SimpleDateFormat("dd-MM-yyyy ha").format(new Date());
        String path = "Screenshots/" + date;

        File dir = new File(path);
        if (!dir.exists()) dir.mkdir();

        File img = new File(path, "Scenario " + (row + 1) + ".jpeg");
        ImageIO.write(capture, "jpeg", img);
    }
}