package fortesp.babysheep;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import fortesp.babysheep.preferences.ApplicationPreferences;

public class MainActivity extends AppCompatActivity {

    public final static int SHEEP_INTERVAL_START_MILIS = 2000;
    public final static int SHEEP_INTERVAL_MILIS = 1000 * 30; // Each half a minute a sheep jumps

    private static MediaPlayerWrapper mediaPlayer;
    private static Set<ImageView> sheepSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        SeekBar seekBar = findViewById(R.id.sb_volume);

        Button btStart = findViewById(R.id.bt_start);
        Button btStop = findViewById(R.id.bt_stop);

        RadioGroup rbTime = findViewById(R.id.rb_time);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.main_container);

        setSupportActionBar(toolbar);
        mediaPlayer = new MediaPlayerWrapper(this, R.raw.whitenoise_1);

        Handler timerHandler = new Handler(Looper.getMainLooper());
        Handler sheepHandler = new Handler(Looper.getMainLooper());
        // ---

        int radioId = ApplicationPreferences.getInstance().getInteger("rt_time");
        if (radioId > 0) {
            rbTime.check(radioId);
        }

        rbTime.setOnCheckedChangeListener((group, checkedId) -> {
            ApplicationPreferences.getInstance().setPref("rt_time", checkedId);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.setVolume(progress);
                ApplicationPreferences.getInstance().setPref("volume", progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        int volumeProgress = ApplicationPreferences.getInstance().getInteger("volume");
        if (volumeProgress > 0) {
            seekBar.setProgress(volumeProgress);
        }

        // BOUNCE TASK LOOP
        Runnable sheepTask = new Runnable() {
            @Override
            public void run() {
                bounceSheep(coordinatorLayout);
                sheepHandler.postDelayed((Runnable) this, SHEEP_INTERVAL_MILIS);
            }
        };

        // START
        btStart.setOnClickListener(v -> {
            mediaPlayer.setVolume(seekBar.getProgress());
            mediaPlayer.start();
            timerHandler.postDelayed(() -> {
                try {
                    mediaPlayer.stop();
                } finally {
                    toggleLayoutContainerVisibility();
                }
            }, getSelectedDurationInMilis());

            toggleLayoutContainerVisibility();

            sheepHandler.postDelayed(sheepTask, SHEEP_INTERVAL_START_MILIS);
        });

        // STOP
        btStop.setOnClickListener(v -> {
            mediaPlayer.stop();
            timerHandler.removeCallbacksAndMessages(null);
            sheepHandler.removeCallbacksAndMessages(null);
            toggleLayoutContainerVisibility();
            removeAllSheep(coordinatorLayout);
        });

    }

    private void toggleLayoutContainerVisibility() {
        LinearLayout controlsContainerPrimary = findViewById(R.id.controls_container_1);
        LinearLayout controlsContainerSecondary = findViewById(R.id.controls_container_2);

        if (controlsContainerPrimary.getVisibility() == View.VISIBLE) {
            controlsContainerPrimary.setVisibility(View.GONE);
            controlsContainerSecondary.setVisibility(View.VISIBLE);
        } else {
            controlsContainerPrimary.setVisibility(View.VISIBLE);
            controlsContainerSecondary.setVisibility(View.GONE);
        }
    }

    private int getSelectedDurationInMilis() {
        int duration = 0;
        RadioGroup radioGroup = findViewById(R.id.rb_time);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_5:
                duration = 5;
                break;
            case R.id.rb_15:
                duration = 15;
                break;
            case R.id.rb_30:
                duration = 30;
                break;
        }
        return duration * 1000 * 1000;
    }

    private void bounceSheep(ViewGroup viewGroup) {

        ImageView sheep = createSheep();
        viewGroup.addView(sheep);

        int containerLayoutWidth = viewGroup.getWidth();
        int containerLayoutHeight = viewGroup.getHeight();

        int originX = ThreadLocalRandom.current().nextInt(0, containerLayoutWidth - sheep.getLayoutParams().width);
        int toYDelta = ThreadLocalRandom.current().nextInt((int) (containerLayoutHeight / 1.5), containerLayoutHeight - sheep.getLayoutParams().height);

        TranslateAnimation transAnim = new TranslateAnimation(originX, originX, 0, toYDelta);
        transAnim.setStartOffset(500);
        transAnim.setDuration(3000);
        transAnim.setFillAfter(true);
        transAnim.setInterpolator(new BounceInterpolator());
        transAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final int left = sheep.getLeft();
                final int top = sheep.getTop();
                final int right = sheep.getRight();
                final int bottom = sheep.getBottom();
                sheep.layout(left, top, right, bottom);
            }
        });
        sheep.startAnimation(transAnim);
    }

    private ImageView createSheep() {
        ImageView sheep = new ImageView(this);
        sheepSet.add(sheep);
        sheep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_sheep_160040));
        int sheepDivider = ThreadLocalRandom.current().nextInt(1, 3);
        sheep.setLayoutParams(new LinearLayout.LayoutParams(200 / sheepDivider, 200 / sheepDivider));
        return sheep;
    }

    private void removeAllSheep(ViewGroup viewGroup) {
        sheepSet.stream().forEach(sheep -> viewGroup.removeView(sheep));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {

            LayoutInflater li = LayoutInflater.from(this);
            View aboutLayout = li.inflate(R.layout.about, null);

            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle("About");
            dialog.setView(aboutLayout);
            dialog.setIcon(R.drawable.ic_about);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dialog));
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}