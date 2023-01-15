package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Breakable extends Wall{
    public Breakable(float x, float y, BufferedImage img) {
        super(x, y, img);
    }

    public void drawImage(Graphics g) {
        g.drawImage(this.img, (int)x, (int)y, null);
    }

}
