package solution.helpers;

import solution.views.IntroView;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by benallen on 18/03/15.
 */
public class SoundHelper {
    private static Clip mSelectClip = null;
    private static boolean mSelectClipRunning = false;

    private static Clip mClickClip = null;
    private static Clip mNoClickClip = null;

    public static void loadClips(){
        // Open an audio input stream.
        URL url = SoundHelper.class.getClassLoader().getResource("sound" + File.separator + "select.wav");
        URL url2 = SoundHelper.class.getClassLoader().getResource("sound" + File.separator + "click.wav");
        URL url3 = SoundHelper.class.getClassLoader().getResource("sound" + File.separator + "noclick.wav");
        try {

            // Save the input streams into the clips
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            mSelectClip = AudioSystem.getClip();
            mSelectClip.open(audioInput);

            audioInput = AudioSystem.getAudioInputStream(url2);
            mClickClip = AudioSystem.getClip();
            mClickClip.open(audioInput);

            audioInput = AudioSystem.getAudioInputStream(url3);
            mNoClickClip = AudioSystem.getClip();
            mNoClickClip.open(audioInput);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public static void itemHover(){
        if (mSelectClipRunning == false) {
            if (mSelectClip.isRunning())
                mSelectClip.stop();   // Stop the player if it is still running
            mSelectClip.setFramePosition(0); // rewind to the beginning
            mSelectClip.start();     // Start playing
            mSelectClipRunning = true;
        }
    }

    public static void itemDeHover() {
        if(mSelectClip != null) {
            mSelectClipRunning = false;
            mSelectClip.stop();
        }
    }
    public static void itemClick(){
        if (mClickClip.isRunning())
            mClickClip.stop();   // Stop the player if it is still running
        mClickClip.setFramePosition(0); // rewind to the beginning
        mClickClip.start();     // Start playing
    }
    public static void itemNoClick(){
        if (mNoClickClip.isRunning())
            mNoClickClip.stop();   // Stop the player if it is still running
        mNoClickClip.setFramePosition(0); // rewind to the beginning
        mNoClickClip.start();     // Start playing
    }
}
