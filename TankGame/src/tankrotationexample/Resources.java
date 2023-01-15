package tankrotationexample;

import tankrotationexample.game.GameWorld;
import tankrotationexample.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Resources {
    private static Map<String, BufferedImage> sprites = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();
    private static Map<String, List<BufferedImage>> animations = new HashMap<>();
    private static BufferedImage loadSprite(String path) throws IOException{
        String message = "Could not find " + path;
        return ImageIO.read(Objects.requireNonNull(GameWorld.class.getClassLoader().getResource(path), message));
    }
    private static Sound loadSound(String path) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        AudioInputStream as = AudioSystem.getAudioInputStream(Objects.requireNonNull(Resources.class.getClassLoader().getResource(path)));
        Clip c = null;
        c = AudioSystem.getClip();
        c.open(as);
        Sound s = new Sound(c);
        s.setVolume(2f);
        return s;
    }
    private static void initSprites() {
        try {
            Resources.sprites.put("tank1", loadSprite("Tanks/tank1.png"));
            Resources.sprites.put("tank2", loadSprite("Tanks/tank2.png"));
            Resources.sprites.put("bullet", loadSprite("Bullet/bullet.png"));
            Resources.sprites.put("rocket", loadSprite("PowerUps/Rocket.gif"));
            Resources.sprites.put("break", loadSprite("Walls/break.png"));
            Resources.sprites.put("unbreak", loadSprite("Walls/unbreak.png"));
            Resources.sprites.put("floor",loadSprite("Floor/grass03.png"));
            Resources.sprites.put("speed", loadSprite("PowerUps/speed.png"));
            Resources.sprites.put("heart", loadSprite("PowerUps/heart1.png"));
            Resources.sprites.put("menu", ImageIO.read(
                    Objects.requireNonNull(Resources.class.getClassLoader().getResource("Title/title.png"))));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }
    }
    private static void initSounds(){
        try{
            Resources.sounds.put("bg", loadSound("Sounds/Music.mid"));
            Resources.sounds.put("pickup", loadSound("Sounds/pickup.wav"));
            Resources.sounds.put("shoot", loadSound("Sounds/shoot.wav"));
            Resources.sounds.put("explode", loadSound("Sounds/shotexplosion.wav"));
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }
    private static void initAnimations(){
        try{
            String base = "Animations/bullet/expl_08_%04d.png";
            List<BufferedImage> temp = new ArrayList<>();
            for(int i = 0; i < 32; i++){
                String fName = String.format(base, i);
                temp.add(loadSprite(fName));
            }
            Resources.animations.put("shoot", temp);

            base = "Animations/nuke/expl_01_%04d.png";
            temp.clear();
            for(int i = 0; i < 24; i++){
                String fName = String.format(base, i);
                temp.add(loadSprite(fName));
            }
            Resources.animations.put("collide", temp);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println(e);
            System.exit(-3);
        }
    }
    public static void loadResources(){
        initSprites();
        initSounds();
        initAnimations();
    }
    public static BufferedImage getSprite(String key){
        if (!Resources.sprites.containsKey(key)) {
            System.out.println(key + " sprite not found");
            System.exit(-2);
        }
        return Resources.sprites.get(key);
    }
    public static Sound getSound(String key){
        if (!Resources.sounds.containsKey(key)) {
            System.out.println(key + " sound not found");
            System.exit(-2);
        }
        return Resources.sounds.get(key);
    }
    public static void main(String[] args){
        Resources.loadResources();
        System.out.println();
    }

    public static List<BufferedImage> getAnimation(String key) {
        if(!Resources.animations.containsKey(key)){
            System.out.println(key + " animation resource not found");
            System.exit(-2);
        }
        return Resources.animations.get(key);
    }
}
