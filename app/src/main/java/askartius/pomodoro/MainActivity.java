package askartius.pomodoro;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity {
    private final int focusTime = 10; // Focus time in seconds

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
        timeCircle.setMax(focusTime);

        SoundPool soundPool = new SoundPool.Builder().build();
        int dingId = soundPool.load(this, R.raw.ding, 1);

        new CountDownTimer(focusTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeCircle.setProgress((int) (focusTime - millisUntilFinished / 1000), true);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Focus finished, time to relax!", Toast.LENGTH_SHORT).show();


                //soundPool.play(dingId, 1, 1, 1, 3, 1);
                MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.ding);
                for (int i = 0; i < 3; i++) {
                    player.start();
                }
            }
        }.start();
    }
}