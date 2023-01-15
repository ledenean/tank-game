package tankrotationexample.game;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Clip soundClip;
    public Sound(Clip c){
        this.soundClip = c;
    }
    public void playSound(){
        this.soundClip.setFramePosition(0);
        this.soundClip.start();
    }
    public void stopSound(){
        if(this.soundClip.isRunning()){
            this.stopSound();
        }
    }
    public void setLooping(){
        this.soundClip.loop(Clip.LOOP_CONTINUOUSLY);

    }
    public void setVolume(float level){
        FloatControl volume = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(20f * (float)Math.log10(level));
    }


}
