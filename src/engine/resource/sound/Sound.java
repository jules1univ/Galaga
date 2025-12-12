package engine.resource.sound;

import engine.utils.logger.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    private final Clip clip;

    public static Sound createSound(InputStream in) {
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            return new Sound(clip);
        } catch (UnsupportedAudioFileException e) {
            Log.error("Sound format not supported: " + e.getMessage());
            return null;
        } catch (IOException | LineUnavailableException e) {
            Log.error("Sound loading failed: " + e.getMessage());
            return null;
        }
    }

    public void play() {
        if (this.clip.isRunning()) {
            this.clip.stop();
        }
        this.clip.setFramePosition(0);
        this.clip.start();
    }

    public void stop() {
        if (this.clip.isRunning()) {
            this.clip.stop();
        }
    }

    public Sound(Clip clip) {
        this.clip = clip;
    }
}
