package fortesp.babysheep;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerWrapper {

    public final static int MAX_VOLUME = 100;
    private MediaPlayer mediaPlayer;

    public MediaPlayerWrapper(Context context, int soundResourceId) {
        mediaPlayer = MediaPlayer.create(context, soundResourceId);
    }

    public void setVolume(int volume) {
        final float v = resolveVolume(volume);
        mediaPlayer.setVolume(v, v);
    }

    public void start() {
        mediaPlayer.start();

    }

    public void stop() {
        mediaPlayer.pause();
    }

    private float resolveVolume(int step) {
        return (float) (1 - (Math.log(MAX_VOLUME - step) / Math.log(MAX_VOLUME)));
    }

}
