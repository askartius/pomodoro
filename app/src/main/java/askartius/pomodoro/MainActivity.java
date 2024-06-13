package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {
    private final String PROGRESS = "progress";
    private final String TIMER_TIME = "timerTime";
    private final String TIMER_RUNNING = "timerRunning";
    private final int FOCUS_TIME = 1500;
    private final int RELAX_TIME = 300;
    private int timerTime = 0;
    private boolean timerRunning = false;
    private int dingSound;
    private CountDownTimer timer;
    private CircularProgressIndicator timerCircle;
    private SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timerCircle = findViewById(R.id.timer_circle);
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                .build();
        dingSound = soundPool.load(this, R.raw.ding, 1);

        switchMode();

        // Switch mode on click
        timerCircle.setOnClickListener(v -> switchMode());

        // Start timer on long click
        timerCircle.setOnLongClickListener(v -> {
            if (timerRunning) {
                timerRunning = false;
                timer.cancel();
                switchMode();
            } else {
                timerRunning = true;
                startTimer(timerTime);
            }

            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(PROGRESS, timerCircle.getProgress());
        outState.putInt(TIMER_TIME, timerTime);
        outState.putBoolean(TIMER_RUNNING, timerRunning);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        timerTime = savedInstanceState.getInt(TIMER_TIME);
        timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
        timerCircle.setProgress(savedInstanceState.getInt(PROGRESS));
        timerCircle.setMax(timerTime);
        updateUI();

        if (timerRunning) {
            startTimer(timerTime - timerCircle.getProgress());
        }
    }

    private void startTimer(int time) {
        timer = new CountDownTimer(time * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerCircle.setProgress((int) (timerTime - millisUntilFinished / 1000), true);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                soundPool.play(dingSound, 1, 1, 1, 2, 1);
                switchMode();
            }
        }.start();
    }

    private void updateUI() {
        if (timerTime == FOCUS_TIME) {
            timerCircle.setIndicatorColor(getColor(R.color.red));
            timerCircle.setTrackColor(getColor(R.color.light_red));
        } else {
            timerCircle.setIndicatorColor(getColor(R.color.green));
            timerCircle.setTrackColor(getColor(R.color.light_green));
        }
    }

    private void switchMode() {
        if (!timerRunning) {
            if (timerTime == FOCUS_TIME) {
                timerTime = RELAX_TIME;
            } else {
                timerTime = FOCUS_TIME;
            }

            timerCircle.setProgress(0, true);
            timerCircle.setMax(timerTime);
            updateUI();
        }
    }
}