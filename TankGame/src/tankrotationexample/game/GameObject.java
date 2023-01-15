package tankrotationexample.game;

import tankrotationexample.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;


public abstract class GameObject {
    protected float x,y;
    protected BufferedImage img;
    protected boolean hasCollided;
    protected Rectangle hitbox;

    public GameObject(float x, float y, BufferedImage img){
        this.x = x;
        this.y = y;
        this.img = img;
        this.hitbox = new Rectangle((int)x,(int)y,this.img.getWidth(), this.img.getHeight());
        this.hasCollided = false;
    }
    public Rectangle getHitbox(){
        return this.hitbox.getBounds();
    }
    void setX(float x){
        this.x = x;

    }
    void setY(float y){
        this.y = y;
    }
    public static GameObject gameObjectFactory(String type, float x, float y) {
        switch (type) {
            case "2": {
                return new Breakable(x, y, Resources.getSprite("break"));
            }
            case "3": {
                return new Life(x,y, Resources.getSprite("heart"));
            }
            case "4": {
                return new Speed(x, y, Resources.getSprite("speed"));
            }
            case "5": {
                return new Rocket(x, y, Resources.getSprite("rocket"));
            }
            case "9": {
                return new Wall(x, y, Resources.getSprite("unbreak"));
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + type);
            }

        }
    }
    public void drawImage(Graphics g)
    {
        g.drawImage(this.img, (int)x, (int)y, null);

    }

    public void collideWith(GameObject obj, GameWorld gw) {
    }
}

