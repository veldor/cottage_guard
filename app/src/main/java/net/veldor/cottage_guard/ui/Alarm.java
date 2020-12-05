package net.veldor.cottage_guard.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.veldor.cottage_guard.App;
import net.veldor.cottage_guard.R;
import net.veldor.cottage_guard.selections.DefenceAlert;

public class Alarm extends AppCompatActivity {

    private Vibrator vibrator;
    private Ringtone r;
    private TextView mCottageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            km.requestDismissKeyguard(this, null);
        } else {
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setContentView(R.layout.activity_alarm);
        setupUI();

        // вибрирую
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] mVibratePattern = new long[]{0, 400, 800, 600, 800, 800, 800, 1000};
            int[] mAmplitudes = new int[]{0, 255, 0, 255, 0, 255, 0, 255};
            vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, 1));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    private void setupUI() {
        mCottageNumber = findViewById(R.id.alert_text);
        if (mCottageNumber != null) {
            DefenceAlert info = App.getInstance().mLiveCurrentAlert.getValue();
            if (info != null) {
                mCottageNumber.setText(info.cottageNumber);
            }
        }
        Button mAlertAcceptedButtonView = findViewById(R.id.alerts_accepted);
        mAlertAcceptedButtonView.setOnClickListener(v -> {
            Log.d("surprise", "Alarm onClick 67: alert accepted");
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
        r.stop();
    }
}