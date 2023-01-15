package tankrotationexample.game;

import org.w3c.dom.css.Rect;
import tankrotationexample.GameConstants;
import tankrotationexample.Resources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tank extends GameObject{
    private int id;

    private float screenX;
    private float screenY;
    private float vx;
    private float vy;
    private float rollBackX;
    private float rollBackY;
    private float angle;

    private long coolDown = 100;
    private long timeLastShot = 0;
    private int live = 3;
    private int health = 100;
    private int damage = 25;
    private int breakCount = 0;

    private float R = 1.0f;  // speed
    private float ROTATIONSPEED = 1.0f;

    private boolean end = false;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean shootPressed;
    List<Bullet> ammo = new ArrayList<>(50);


    Tank(float x, float y, int id, float angle, BufferedImage img) {
        super(x,y,img);
        this.id = id;
        this.angle = angle;
    }


    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }
    public void toggleShootPressed() {
        this.shootPressed = true;
    }
    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }
    public void unToggleShootPressed() {
        this.shootPressed = false;
    }
    void update(GameWorld gw) {
        this.hitbox.setLocation((int)x,(int)y);
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }
        if(this.shootPressed && (this.timeLastShot + coolDown) < System.currentTimeMillis()) {
            this.timeLastShot = System.currentTimeMillis();
            System.out.println("Tank shot a bullet");
            Bullet b = new Bullet(setBulletStartX(),setBulletStartY(), this.getId(), angle, Resources.getSprite("bullet"));
            gw.addGameObject(b);
            gw.addAnimation(new Animation(b.x,b.y, Resources.getAnimation("shoot")));
            this.ammo.add(b);
            Resources.getSound("shoot").playSound();
        }

        this.ammo.forEach(Bullet::update);
        this.ammo.removeIf(bullet -> bullet.hasCollided);
        centerScreen();

    }


    private int setBulletStartX(){
        float cx = 41f * (float)Math.cos(Math.toRadians(angle));
        return (int) x + this.img.getWidth() / 2 + (int) cx - 16;
    }
    private int setBulletStartY(){
        float cy = 41f * (float) Math.sin(Math.toRadians(angle));
        return (int) y + this.img.getHeight() / 2 + (int) cy - 16;
    }
    private void setCoolDown(long newCoolDown){
        this.coolDown = newCoolDown;
        if (this.coolDown < 3){
            this.coolDown= 3;
        }
    }


    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx =  Math.round(R * Math.cos(Math.toRadians(angle)));
        vy =  Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        setX(x);
        setY(y);
        checkBorder();
        centerScreen();

    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        setX(x);
        setY(y);
        checkBorder();
        centerScreen();

    }


    private void checkBorder() {
        if (x < 30) {
            setX(30);
        }
        if (x >= GameConstants.WORLD_WIDTH- 88) {
            setX(GameConstants.WORLD_WIDTH - 88);
        }
        if (y < 40) {
            setY(40);
        }
        if (y >= GameConstants.WORLD_HEIGHT - 80) {
            setY(GameConstants.WORLD_HEIGHT- 80);
        }
    }
    private void centerScreen(){
        this.screenX = this.x - GameConstants.GAME_SCREEN_WIDTH / 4f;
        this.screenY = this.y - GameConstants.GAME_SCREEN_HEIGHT / 2f;

        if(this.screenX < 0) screenX = 0;
        if(this.screenY < 0) screenY = 0;
        if(this.screenX > GameConstants.WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f){
            this.screenX = GameConstants.WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f;
        }
        if(this.screenY > GameConstants.WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT){
            this.screenY = GameConstants.WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        }

    }
    public void drawLives(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for(int i = 0; i < this.live; i++){
            g2d.drawOval((int)x + (i * 10), (int)y+65, 10, 10);
            g2d.fillOval((int)x + (i * 10), (int)y+65, 10, 10);
        }
        if(this.live == 0){
            this.end = true;
        }
    }

    public boolean endGame(){
        return this.end;
    }
    @Override
    public void collideWith(GameObject obj, GameWorld gw) {
        if(obj instanceof Bullet && getId() != ((Bullet) obj).getTankId()){
            this.health = this.health - damage;
            if(this.health == 0 && this.live != 0){
                this.health = 100;
                this.live--;
            }
            obj.hasCollided = true;
            Resources.getSound("explode").playSound();
            gw.addAnimation(new Animation(obj.x, obj.y, Resources.getAnimation("collide")));
        }
        else if(obj instanceof Wall){
            Rectangle collide = getHitbox().intersection(obj.getHitbox());
            if(collide.height > collide.width && x > this.getHitbox().x){
                setX(x - collide.width/2f);
            }
            else if(collide.height > collide.width && x < this.getHitbox().x  ){
                setX(x + collide.width/2f);
            }
            else if(collide.height < collide.width && y > this.getHitbox().y){
                setY(y - collide.height/2f);
            }
            else if(collide.height < collide.width && y < this.getHitbox().y){
                setY(y + collide.height/2f);
            }

        }
        else if(obj instanceof PowerUp){
            Resources.getSound("pickup").playSound();
            if(obj instanceof Life){
                obj.hasCollided = true;
                this.live++;
            }
            else if(obj instanceof Speed){
                obj.hasCollided = true;
                this.R += .5;
            }
            else if(obj instanceof Rocket){
                obj.hasCollided = true;
                if(this.damage <= 50) {
                    this.damage += 25;
                }
            }
        }
    }



    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
        this.ammo.forEach(bullet -> bullet.drawImage(g));

        g2d.setColor(Color.GREEN);
        g2d.drawRect((int)x,(int)y - 20,100, 15);
        g2d.fillRect((int)x,(int)y - 20,this.health, 15);
        drawLives(g2d);
    }

    public int getScreenX(){
        return (int)screenX;
    }
    public int getScreenY(){
        return (int)screenY;
    }

    private int getId() {
        return id;
    }
}
