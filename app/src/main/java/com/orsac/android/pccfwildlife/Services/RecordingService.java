package com.orsac.android.pccfwildlife.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.GajaBandhuItemActivity;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SQLiteDB.RecordingDbHelper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;

public class RecordingService extends Service {

        private static final String LOG_TAG = "RecordingService";

        private final String mFileName = null;
        private   String mFilePath = null;

        private MediaRecorder mRecorder = null;

        private RecordingDbHelper mDatabase;

        private long mStartingTimeMillis = 0;
        private long mElapsedMillis = 0;
        private int mElapsedSeconds = 0;
        private final OnTimerChangedListener onTimerChangedListener = null;
        private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

        private Timer mTimer = null;
        private TimerTask mIncrementTimerTask = null;
        File f;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        public interface OnTimerChangedListener {
            void onTimerChanged(int seconds);
        }

        @Override
        public void onCreate() {
            super.onCreate();
            mDatabase = new RecordingDbHelper(getApplicationContext());
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            try {
                mFilePath = intent.getStringExtra("filePath");
                startRecording();
            }catch (Exception e){
                e.printStackTrace();
            }

            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            try {
                if (mRecorder != null) {
                    stopRecording();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            super.onDestroy();
        }

        public void startRecording() {
            try {
                setFileNameAndPath();

                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mRecorder.setOutputFile(mFilePath);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mRecorder.setAudioChannels(1);

            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                mRecorder.prepare();
                mRecorder.start();
                mStartingTimeMillis = System.currentTimeMillis();


            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }

        public void setFileNameAndPath(){
            int count = 0;
//            File f;

            do{
//                count++;
//
//                mFileName = "wildlife_bana_sathi_voice"
//                        + "_" + (mDatabase.getCount() + count) + ".mp4";
//                mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                mFilePath += "/WildLife_SoundRecorder/" + mFileName;

                f = new File(mFilePath);
            }while (f.exists() && !f.isDirectory());
        }

        public void stopRecording() {
            try {
                mRecorder.stop();
                mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
                mRecorder.release();
                Toast.makeText(this, "Recording Finish" + " " + mFilePath, Toast.LENGTH_LONG).show();

                //remove notification
                if (mIncrementTimerTask != null) {
                    mIncrementTimerTask.cancel();
                    mIncrementTimerTask = null;
                }

                mRecorder = null;
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

            } catch (Exception e){
                Log.e(LOG_TAG, "exception", e);
            }
        }


        private void startTimer() {
            mTimer = new Timer();
            mIncrementTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mElapsedSeconds++;
                    if (onTimerChangedListener != null)
                        onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                    NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mgr.notify(1, createNotification());
                }
            };
            mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
        }

        //TODO:
        private Notification createNotification() {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.iwlms_logo)
                            .setContentTitle("Wildlife Recording")
                            .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                            .setOngoing(true);

            mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                    new Intent[]{new Intent(getApplicationContext(), GajaBandhuItemActivity.class)}, 0));

            return mBuilder.build();
        }
}
