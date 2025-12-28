package engine.resource.sound;

import engine.utils.logger.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    private final ArrayList<Clip> clips = new ArrayList<>();
    private final AudioFormat format;
    private final byte[] audioData;
    private final DataLine.Info info;

    public static Sound createSound(InputStream in) {
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            AudioFormat format;
            byte[] audioData;

            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis)) {
                format = audioInputStream.getFormat();
                audioData = audioInputStream.readAllBytes();
            }
            
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(format, audioData, 0, audioData.length);

            return new Sound(format, audioData, info, clip);
        } catch (UnsupportedAudioFileException e) {
            Log.error("Sound format not supported: %s", e.getMessage());
            return null;
        } catch (IOException | LineUnavailableException e) {
            Log.error("Sound loading failed: %s", e.getMessage());
            return null;
        }
    }

    private Sound(AudioFormat format, byte[] audioData, DataLine.Info info, Clip clip) {
        this.clips.add(clip);
        this.format = format;
        this.audioData = audioData;
        this.info = info;
    }

    public void setLoop(boolean loop) {
        for (Clip clip : this.clips) {
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.loop(0);
            }
        }
    }

    public void setCapacity(int capacity) {
        while (this.clips.size() < capacity) {
            try {
                Clip clip = (Clip) AudioSystem.getLine(this.info);
                clip.open(this.format, this.audioData, 0, this.audioData.length);
                this.clips.add(clip);
            } catch (LineUnavailableException e) {
                Log.error("Sound creation failed: %s", e.getMessage());
                return;
            }
        }
    }

    public void play(float volume) {
        for (Clip clip : this.clips) {
            if (!clip.isActive()) {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float db = (float) (20 * Math.log10(Math.max(volume, 0.0001f)));
                    gain.setValue(db);
                }
                clip.setFramePosition(0);
                clip.start();
                return;
            }
        }
    }

    public void play() {
        this.play(1.f);
    }

    public void stop() {
        for (Clip clip : this.clips) {
            if (clip.isActive()) {
                clip.stop();
            }
        }
    }
}
