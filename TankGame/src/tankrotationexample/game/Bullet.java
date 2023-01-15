package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.Resources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject{
    private int tankId;
    float R = 6;
    float angle = 0;
    private int hitCount = 0;

    public Bullet(float x, float y, int tankId, float angle, BufferedImage img){
        super(x,y,img);
        this.tankId = tankId;
        this.angle = angle;

    }

    void update(){
        x += Math.round(R * Math.cos(Math.toRadians(angle)));
        y += Math.round(R * Math.sin(Math.toRadians(angle)));
        setX(x);
        setY(y);
        this.hitbox.setLocation((int)x, (int)y);
        checkBorder();

    }
    private void checkBorder() {
        if (x < 30) {
            setX(30);
            this.hasCollided = true;
        }
        if (x >= GameConstants.WORLD_WIDTH- 88) {
            setX(GameConstants.WORLD_WIDTH - 88);
            this.hasCollided = true;
        }
        if (y < 40) {
            setY(40);
            this.hasCollided = true;
        }
        if (y >= GameConstants.WORLD_HEIGHT - 80) {
            setY(GameConstants.WORLD_HEIGHT- 80);
            this.hasCollided = true;
        }
    }
    public int getTankId() {
        return this.tankId;
    }

    @Override
    public void collideWith(GameObject obj, GameWorld gw) {
        if(obj instanceof Wall){
            if(obj instanceof Breakable){
                obj.hasCollided = true;
            }
            this.hasCollided = true;
            Resources.getSound("explode").playSound();
            gw.addAnimation(new Animation(obj.x, obj.y, Resources.getAnimation("collide")));
        }

    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
    }
}
