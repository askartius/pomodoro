package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

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
    private int timerTime = 0;
    private boolean timerRunning = false;
    private CountDownTimer timer;

    CircularProgressIndicator timeCircle;

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

        timeCircle = findViewById(R.id.time_circle);

        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                )
                .build();
        int ding = soundPool.load(this, R.raw.ding, 1);

        if (savedInstanceState != null) {
            timerTime = savedInstanceState.getInt(TIMER_TIME);
            timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
            timeCircle.setProgress(savedInstanceState.getInt(PROGRESS));
            timeCircle.setMax(timerTime);
            Log.d("TEST", "T" + timerTime);


            if (timerTime == 1500) { // If timerTime == focus time
                timeCircle.setIndicatorColor(getColor(R.color.red));
                timeCircle.setTrackColor(getColor(R.color.light_red));
            } else {
                timeCircle.setIndicatorColor(getColor(R.color.green));
                timeCircle.setTrackColor(getColor(R.color.light_green));
            }

            if (timerRunning) {
                timer = new CountDownTimer((timerTime - savedInstanceState.getInt(PROGRESS)) * 1000L, 1000L) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeCircle.setProgress((int) (timerTime - millisUntilFinished / 1000), true);
                    }

                    @Override
                    public void onFinish() {
                        timerRunning = false;
                        soundPool.play(ding, 1, 1, 1, 2, 1);
                        switchMode();
                    }
                }.start();
            }
        } else {
            switchMode();
        }

        // Switch mode on click
        timeCircle.setOnClickListener(v -> switchMode());

        // Start timer on long click
        timeCircle.setOnLongClickListener(v -> {
            if (timerRunning) {
                timerRunning = false;
                timer.cancel();
                switchMode();
            } else {
                timerRunning = true;
                timer = new CountDownTimer(timerTime * 1000L, 1000L) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeCircle.setProgress((int) (timerTime - millisUntilFinished / 1000), true);
                    }

                    @Override
                    public void onFinish() {
                        timerRunning = false;
                        soundPool.play(ding, 1, 1, 1, 2, 1);
                        switchMode();
                    }
                }.start();
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
        outState.putInt(PROGRESS, timeCircle.getProgress());
        outState.putInt(TIMER_TIME, timerTime);
        outState.putBoolean(TIMER_RUNNING, timerRunning);

        super.onSaveInstanceState(outState);
    }

    private void switchMode() {
        if (!timerRunning) {
            if (timerTime == 1500) { // If timerTime == focus time
                timerTime = 300;
                timeCircle.setIndicatorColor(getColor(R.color.green));
                timeCircle.setTrackColor(getColor(R.color.light_green));
            } else {
                timerTime = 1500;
                timeCircle.setIndicatorColor(getColor(R.color.red));
                timeCircle.setTrackColor(getColor(R.color.light_red));
            }

            timeCircle.setProgress(0, true);
            timeCircle.setMax(timerTime);
        }
    }
}