package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Wall extends GameObject{

    public Wall(float x, float y, BufferedImage img){
        super(x,y,img);
    }



    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.drawImage(this.img, (int)x, (int)y, null);

    }

}
