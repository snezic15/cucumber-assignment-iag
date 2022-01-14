package test.java.Utility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screenshot {
    public static void screenCapture(int scenario) throws IOException, AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = new Robot().createScreenCapture(screenRect);
        File imageFile = new File("scenario" + scenario + ".bmp");
        ImageIO.write(capture, "bmp", imageFile);
    }
}
