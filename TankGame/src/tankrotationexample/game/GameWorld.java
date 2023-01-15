package tankrotationexample.game;


import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;
import tankrotationexample.Resources;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Sound bgMusic;

    private Tank t1;
    private Tank t2;
    private Launcher lf;
    private long tick = 0;
    private List<GameObject> gameObjects = new ArrayList<>(500);
    private List<Animation> anims = new ArrayList<>(20);


    /**
     * 
     * @param lf
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            this.resetGame();
            bgMusic = Resources.getSound("bg");
            bgMusic.setVolume(0.2f);
            bgMusic.setLooping();
            bgMusic.playSound();
            while (true) {
                this.tick++;
                t1.update(this);
                t2.update(this);
                this.checkCollisions();
                this.anims.forEach(a -> a.update());
                this.gameObjects.removeIf(g->g.hasCollided);
                this.anims.removeIf(a -> !a.isRunning);
                this.repaint();   // redraw game
                if(t1.endGame() || t2.endGame()){
                    this.lf.setFrame("end");
                    return;
                }
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */
                Thread.sleep(1000 / 144);

            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    private void checkCollisions() {
        for(int i = 0; i < this.gameObjects.size(); i++){
            GameObject ob1 = this.gameObjects.get(i);
            if(ob1 instanceof Wall || ob1 instanceof PowerUp) continue;
            for(int j = 0; j < this.gameObjects.size(); j++){
                if(i == j) continue;
                GameObject ob2 = this.gameObjects.get(j);
                if(ob1.getHitbox().intersects(ob2.getHitbox())){
                    ob1.collideWith(ob2, this);
                }
            }
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setX(50);
        this.t1.setY(190);
        this.t2.setX(1810);
        this.t2.setY(1300);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.WORLD_WIDTH,
                GameConstants.WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        try(BufferedReader mapReader = new BufferedReader(new InputStreamReader(GameWorld.class.getClassLoader().getResourceAsStream("Maps/map1.csv")))){
            for(int i = 0; mapReader.ready(); i++){
                String[] gameObjects = mapReader.readLine().split(",");
                for(int j = 0; j < gameObjects.length; j++) {
                    String objectType = gameObjects[j];
                    if(Objects.equals("0", objectType)) continue;
                    this.gameObjects.add(GameObject.gameObjectFactory(objectType, j*30, i*30));
                }
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }


        t1 = new Tank(50, 190, 1,  0, Resources.getSprite("tank1"));
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);

        t2 = new Tank(1810, 1300, 2,  0, Resources.getSprite("tank2"));
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_SHIFT);
        this.lf.getJf().addKeyListener(tc2);
        this.gameObjects.add(t1);
        this.gameObjects.add(t2);
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        drawFloor(buffer);

        for(int i = 0; i < this.gameObjects.size(); i++){
            this.gameObjects.get(i).drawImage(buffer);
        }

        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        this.anims.forEach(a->a.drawImage(buffer));
        drawSplitScreens(g2, world);
        drawMiniMap(g2, world);
    }
    void drawFloor(Graphics2D buffer){
        for(int i = 0; i < GameConstants.WORLD_WIDTH; i += 320){
            for(int j = 0; j < GameConstants.WORLD_HEIGHT; j+=240){
                buffer.drawImage(Resources.getSprite("floor"), i, j, null);
            }
        }
    }
    void drawMiniMap(Graphics2D g, BufferedImage world){
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        AffineTransform at = new AffineTransform();
        at.translate(GameConstants.GAME_SCREEN_WIDTH/2f - (GameConstants.WORLD_WIDTH*.2f)/2f,
                GameConstants.GAME_SCREEN_HEIGHT/1.031f - (GameConstants.WORLD_HEIGHT*.2f));
        at.scale(.2,.2);
        g.drawImage(mm, at,null);
    }
    void drawSplitScreens(Graphics2D g, BufferedImage world){
        BufferedImage lh = world.getSubimage(t1.getScreenX(), t1.getScreenY(),
                GameConstants.GAME_SCREEN_WIDTH/2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rh = world.getSubimage(t2.getScreenX(), t2.getScreenY(),
                GameConstants.GAME_SCREEN_WIDTH/2, GameConstants.GAME_SCREEN_HEIGHT);
        g.drawImage(lh, 0, 0, null);
        g.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH/2, 0, null);
    }


    public void addGameObject(Bullet b) {
        this.gameObjects.add(b);
    }
    public void addAnimation(Animation anim) {
        this.anims.add(anim);
    }
}
