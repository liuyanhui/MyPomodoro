package com.brucego.mypomodoro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.brucedream.mypomodoro.R;

public class PomodoroActivity extends AppCompatActivity {
    private PomodoroStatusEnum status;
    private TextView mottoTextView;
    private TextView remainTextView;
    private TextView reservedTextView;
    private TextView sumTextView;
    private LinearLayout statisticsLayout;
    private CountDownTimer countDownTimer;
    private NotificationManager notificationManager;
    private Button btn;

    //默认的倒计时秒数
    private int totalSeconds = 25 * 60;
    private int restSeconds = 5 * 60;
    private int wordedTimes = 0;

    private static final String PROJECT_KEY = "mypomodoro";
    private static final String MOTTO_KEY = "motto";
    private static final String RESERVED_TIME_KEY = "RESERVED_TIME";
    private static final String REST_TIME_KEY = "REST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro2_layout);

        mottoTextView = (TextView) findViewById(R.id.mottoTextView);
        remainTextView = (TextView) findViewById(R.id.remainTextView);
        reservedTextView = (TextView) findViewById(R.id.reservedTextView);
        sumTextView = (TextView) findViewById(R.id.sumTextView);
        btn = (Button) findViewById(R.id.pomodoroBtn);
        statisticsLayout = (LinearLayout) findViewById(R.id.statisticsLayout);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initial();
    }

    public void clickBtn(View view) {
        if (status == PomodoroStatusEnum.INITAIL) {
            status = PomodoroStatusEnum.WORK;
            work();
        } else {
            initial();
        }
    }

    private void initial() {
        mottoTextView.setText(this.getSharedPreferences(PROJECT_KEY, MODE_PRIVATE).getString(MOTTO_KEY, "Get Out Of The Asshole."));
        totalSeconds = this.getSharedPreferences(PROJECT_KEY, MODE_PRIVATE).getInt(RESERVED_TIME_KEY, 25 * 60);
        restSeconds = this.getSharedPreferences(PROJECT_KEY, MODE_PRIVATE).getInt(REST_TIME_KEY, 5 * 60);
        reservedTextView.setText(String.valueOf(totalSeconds));
        remainTextView.setText(String.valueOf(totalSeconds));
        remainTextView.setText("");
        status = PomodoroStatusEnum.INITAIL;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        this.btn.setBackgroundColor(Color.BLUE);
        wordedTimes = 0;
        sumTextView.setText("");
        statisticsLayout.setVisibility(View.INVISIBLE);
    }

    private void work() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        countDownTimer = new CountDownTimer(totalSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                remainTextView.setText(value);
            }

            @Override
            public void onFinish() {
                sendMsgToSelf("Hey Bro. Take A Break!");
                rest();
                statisticsLayout.setVisibility(View.VISIBLE);
                sumTextView.setText(String.valueOf(++wordedTimes));
            }
        }.start();
        reservedTextView.setText(String.valueOf(totalSeconds));
        this.btn.setBackgroundColor(Color.LTGRAY);
    }

    private void rest() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(restSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) (millisUntilFinished / 1000));
                remainTextView.setText(value);
            }

            @Override
            public void onFinish() {
                sendMsgToSelf("Hey Bro. Let's Rock!");
                work();
            }
        }.start();
        reservedTextView.setText(String.valueOf(restSeconds));
        this.btn.setBackgroundColor(Color.GREEN);
    }

    private void sendMsgToSelf(String msg) {
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
                .setContentText(msg)
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
