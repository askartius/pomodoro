package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {
    private int timerTime = 0;
    private int timePassed = 0;
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

        switchMode();

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
                        timeCircle.setProgress(timePassed);
                        timePassed++;
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

    private void switchMode() {
        if (!timerRunning) {
            timePassed = 0;
            timeCircle.setProgress(timePassed);

            if (timerTime == 1500) { // If timerTime == focus time
                timerTime = 300;
                timeCircle.setIndicatorColor(getColor(R.color.green));
                timeCircle.setTrackColor(getColor(R.color.light_green));
            } else {
                timerTime = 1500;
                timeCircle.setIndicatorColor(getColor(R.color.red));
                timeCircle.setTrackColor(getColor(R.color.light_red));
            }

            timeCircle.setMax(timerTime); // Update time circle's maximum time
        }
    }
}