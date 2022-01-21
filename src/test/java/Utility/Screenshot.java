package Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {
    public static void screenCapture(int scenario) throws IOException, AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        File imageFile = new File("Screenshots", "Scenario " + (scenario + 1) + ".jpeg");
        ImageIO.write(capture, "jpeg", imageFile);
    }
}
