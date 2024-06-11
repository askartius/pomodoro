package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {
    private final int focusTime = 25 * 60;
    private final int relaxTime = 5 * 60;
    private int time = relaxTime; // Focus time in seconds
    private boolean timerRunning = false;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CircularProgressIndicator timeCircle = findViewById(R.id.time_circle);
        timeCircle.setMax(time); // Update time circle's maximum time

        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                )
                .build();
        int ding = soundPool.load(this, R.raw.ding, 1);

        // Update time on click
        timeCircle.setOnClickListener(v -> {
            if (!timerRunning) {
                if (time == focusTime) {
                    time = relaxTime;
                    Toast.makeText(MainActivity.this, "Relax (5 min)", Toast.LENGTH_SHORT).show();
                } else {
                    time = focusTime;
                    Toast.makeText(MainActivity.this, "Focus (25 min)", Toast.LENGTH_SHORT).show();
                }
                timeCircle.setMax(time); // Update time circle's maximum time
                Log.d("TEST", String.valueOf(time));
            }
        });

        // Start timer on long click
        timeCircle.setOnLongClickListener(v -> {
            if (timerRunning) {
                timer.cancel();
                timeCircle.setProgress(0);
            } else {
                timer = new CountDownTimer(time * 1000L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeCircle.setProgress((int) (time - millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        timerRunning = false;
                        Toast.makeText(MainActivity.this, "Focus finished, time to relax!", Toast.LENGTH_LONG).show();
                        soundPool.play(ding, 1, 1, 1, 3, 1);
                    }
                }.start();
            }

            timerRunning = !timerRunning;

            return true;
        });
    }
}