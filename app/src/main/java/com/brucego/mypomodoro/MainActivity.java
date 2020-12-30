package com.brucego.mypomodoro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brucedream.mypomodoro.R;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private TextView total;
    private TextView timeLeft;
    private Button stopBtn;
    private Button startBtn;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total = (EditText) findViewById(R.id.total);
        timeLeft = (TextView) findViewById(R.id.timeLeft);
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        stopBtn.setEnabled(false);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startBtn(View view) {
        startCountDownTimer();
        total.clearFocus();
        timeLeft.setTextColor(Color.BLACK);
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
    }

    public void stopBtn(View view) {
        total.clearFocus();
        countDownTimer.cancel();
        timeLeft.setText("0");
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    private void startCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        int totalMillisecond = (int) (Float.valueOf(total.getText().toString()) * 60 * 1000);
//        totalMillisecond = 3 * 1000;
        countDownTimer = new CountDownTimer(totalMillisecond, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                timeLeft.setText(value);
            }

            @Override
            public void onFinish() {
                timeLeft.setText("Done");
                timeLeft.setTextColor(Color.GREEN);
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                sendMsgToSelf();
            }
        }.start();
    }

    private void sendMsgToSelf() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String notificationID = "notification";
        String description = "Pomodoro notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(notificationID, description, importance);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, notificationID)
                .setAutoCancel(true)
                .setContentTitle("Pomodoro")
                .setContentText("Hi Bro. Let's Rock!")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置红色
                .setColor(Color.parseColor("#F00606"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "5"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setVibrate(new long[]{0, 300, 500, 700})
                .build();
        notificationManager.notify(1, notification);
    }

}