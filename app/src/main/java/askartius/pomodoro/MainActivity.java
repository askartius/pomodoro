package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {
    private int time = 5 * 60; // Focus time in seconds

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
        timeCircle.setMax(time * 60);

        MaterialTextView timeDisplay = findViewById(R.id.time_display);
        timeDisplay.setText(String.valueOf(time / 60));
        timeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time == 5) {
                    time = 25;
                } else {
                    time = 5;
                }

                timeDisplay.setText(String.valueOf(time));
            }
        });
        

        SoundPool soundPool = new SoundPool.Builder()
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                )
                .build();
        int ding = soundPool.load(this, R.raw.ding, 1);

        new CountDownTimer(time * 60000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCircle.setProgress((int) (time * 60 - millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Focus finished, time to relax!", Toast.LENGTH_LONG).show();
                soundPool.play(ding, 1, 1, 1, 3, 1);
            }
        }.start();
    }
}