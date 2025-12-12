package engine.resource.sound;

import engine.utils.logger.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    private final Clip[] clips;

    public static Sound createSound(InputStream in, int preloadCount) {
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            
            AudioFormat format = audioInputStream.getFormat();
            byte[] audioData = audioInputStream.readAllBytes();
            audioInputStream.close();

            Clip[] clips = new Clip[preloadCount];
            for (int i = 0; i < preloadCount; i++) {
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(format, audioData, 0, audioData.length);

                clips[i] = clip;
            }

            return new Sound(clips);
        } catch (UnsupportedAudioFileException e) {
            Log.error("Sound format not supported: " + e.getMessage());
            return null;
        } catch (IOException | LineUnavailableException e) {
            Log.error("Sound loading failed: " + e.getMessage());
            return null;
        }
    }

    private Sound(Clip[] clips) {
        this.clips = clips;
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
