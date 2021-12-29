package com.orsac.android.pccfwildlife.CameraClass;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.VideoMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WhatsappCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private final Handler customHandler = new Handler();
    int flag = 0;
    private File tempFile = null;
    private Camera.PictureCallback jpegCallback;
    public int MAX_VIDEO_SIZE_UPLOAD = 25; //MB
    Runnable runnable;
    int seconds;
    String videopath="";

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (myOrientationEventListener != null)
                myOrientationEventListener.enable();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private File folder = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_activity_camera);

        try {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


            runTimePermission = new RunTimePermission(this);
            runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, new RunTimePermission.RunTimePermissionListener() {

                @Override
                public void permissionGranted() {
                    // First we need to check availability of play services
                    initControls();

                    identifyOrientationEvents();

                    //create a folder to get image
                    try {

                        if(Build.VERSION.SDK_INT >= 29) {

                            //only api 29 above
                            File path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "gajavideo" + timeStamp + "_";
                            tempFile=new File(path+"/GajaBandhuCamera");

                            if (!tempFile.exists()){
                                tempFile.mkdir();
                            }

                            // Create an image file name
//                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                            String imageFileName = "gajavideo" + timeStamp + "_";
//                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                            File file = File.createTempFile(
//                                    imageFileName,  /* prefix */
//                                    ".mp3",         /* suffix */
//                                    storageDir      /* directory */
//                            );

                            // Save a file: path for use with ACTION_VIEW intents
                            videopath = tempFile.getAbsolutePath();



                        }else{
                            folder = new File(Environment.getExternalStorageDirectory() + "/GajaBandhuCamera");
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
//                    folder = new File(Environment.getExternalStorageDirectory() + "/GajaBandhuCamera");
//                    if (!folder.exists()) {
//                        folder.mkdirs();
//                    }
                    //capture image on callback
                    captureImageCallback();
                    //
                    if (camera != null) {
                        Camera.CameraInfo info = new Camera.CameraInfo();
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            imgFlashOnOff.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void permissionDenied() {
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void cancelSavePicTaskIfNeed() {
        try {

            if (savePicTask != null && savePicTask.getStatus() == AsyncTask.Status.RUNNING) {
                savePicTask.cancel(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cancelSaveVideoTaskIfNeed() {
        try {

            if (saveVideoTask != null && saveVideoTask.getStatus() == AsyncTask.Status.RUNNING) {
                saveVideoTask.cancel(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private SavePicTask savePicTask;

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private final byte[] data;
        private int rotation = 0;

        public SavePicTask(byte[] data, int rotation) {
            this.data = data;
            this.rotation = rotation;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                return saveToSDCard(data, rotation);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            activeCameraCapture();

            tempFile = new File(result);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Build.VERSION.SDK_INT >= 29) {
                        VideoMessageGajaBandhuActivity.videoPath = tempFile.toString();
                        finish();
                    }else {
                        VideoMessageGajaBandhuActivity.videoPath = folder.toString();
                        finish();
                    }

                }
            }, 100);


        }
    }

    public String saveToSDCard(byte[] data, int rotation) throws IOException {
        String imagePath = "";
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int reqHeight = metrics.heightPixels;
            int reqWidth = metrics.widthPixels;

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (rotation != 0) {
                Matrix mat = new Matrix();
                mat.postRotate(rotation);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                if (bitmap != bitmap1) {
                    bitmap.recycle();
                }
                imagePath = getSavePhotoLocal(bitmap1);
                if (bitmap1 != null) {
                    bitmap1.recycle();
                }
            } else {
                imagePath = getSavePhotoLocal(bitmap);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        try {
            final int height = options.outHeight;
            final int width = options.outWidth;


            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return inSampleSize;
    }

    private String getSavePhotoLocal(Bitmap bitmap) {
        String path = "";
        try {
            OutputStream output;
            File file = new File(folder.getAbsolutePath(), "wc" + System.currentTimeMillis() + ".jpg");
            try {
                output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
                output.flush();
                output.close();
                path = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void captureImageCallback() {

        try {

            surfaceHolder = imgSurface.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            jpegCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {

                    refreshCamera();

                    cancelSavePicTaskIfNeed();
                    savePicTask = new SavePicTask(data, getPhotoRotation());
                    savePicTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class SaveVideoTask extends AsyncTask<Void, Void, Void> {

        File thumbFilename;

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            try {

                progressDialog = new ProgressDialog(WhatsappCameraActivity.this);
                progressDialog.setMessage("Processing captured video...");
                progressDialog.show();
                super.onPreExecute();
                imgCapture.setOnTouchListener(null);
                textCounter.setVisibility(View.GONE);
                imgSwipeCamera.setVisibility(View.VISIBLE);
                imgFlashOnOff.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    myOrientationEventListener.enable();

                    customHandler.removeCallbacksAndMessages(null);

                    mediaRecorder.stop();
                    releaseMediaRecorder();
                    if(Build.VERSION.SDK_INT >= 29) {
                        tempFile = new File(tempFile.getAbsolutePath() + "/" + mediaFileName + ".mp4");
                        thumbFilename = new File(tempFile.getAbsolutePath(), "t_" + mediaFileName + ".jpeg");

                        generateVideoThmb(tempFile.getPath(), thumbFilename);
                    }else {
                        tempFile = new File(folder.getAbsolutePath() + "/" + mediaFileName + ".mp4");
                        thumbFilename = new File(folder.getAbsolutePath(), "t_" + mediaFileName + ".jpeg");

                        generateVideoThmb(folder.getPath(), thumbFilename);
                    }
//                    generateVideoThmb(tempFile.getPath(), thumbFilename);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                if (tempFile != null && thumbFilename != null)
                    onVideoSendDialog(tempFile.getAbsolutePath(), thumbFilename.getAbsolutePath());

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private int mPhotoAngle = 90;

    private void identifyOrientationEvents() {

        try {

            myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int iAngle) {

                    final int[] iLookup = {0, 0, 0, 90, 90, 90, 90, 90, 90, 180, 180, 180, 180, 180, 180, 270, 270, 270, 270, 270, 270, 0, 0, 0}; // 15-degree increments
                    if (iAngle != ORIENTATION_UNKNOWN) {

                        int iNewOrientation = iLookup[iAngle / 15];
                        if (iOrientation != iNewOrientation) {
                            iOrientation = iNewOrientation;
                            if (iOrientation == 0) {
                                mOrientation = 90;
                            } else if (iOrientation == 270) {
                                mOrientation = 0;
                            } else if (iOrientation == 90) {
                                mOrientation = 180;
                            }

                        }
                        mPhotoAngle = normalize(iAngle);
                    }
                }
            };

            if (myOrientationEventListener.canDetectOrientation()) {
                myOrientationEventListener.enable();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private MediaRecorder mediaRecorder;
    private SurfaceView imgSurface;
    private ImageView imgCapture;
    private ImageView imgFlashOnOff;
    private ImageView imgSwipeCamera;
    private RunTimePermission runTimePermission;
    private TextView textCounter;
    private TextView hintTextView;

    private void initControls() {

        try {

            mediaRecorder = new MediaRecorder();

            imgSurface = findViewById(R.id.imgSurface);
            textCounter = findViewById(R.id.textCounter);
            imgCapture = findViewById(R.id.imgCapture);
            imgFlashOnOff = findViewById(R.id.imgFlashOnOff);
            imgSwipeCamera = findViewById(R.id.imgChangeCamera);
            textCounter.setVisibility(View.GONE);

            hintTextView = findViewById(R.id.hintTextView);

            imgSwipeCamera.setOnClickListener(this);
            activeCameraCapture();//call the camera

            imgFlashOnOff.setOnClickListener(this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        cancelSavePicTaskIfNeed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlashOnOff:
                flashToggle();
                break;
            case R.id.imgChangeCamera:
                camera.stopPreview();
                camera.release();
                if (flag == 0) {
                    imgFlashOnOff.setVisibility(View.GONE);
                    flag = 1;
                } else {
                    imgFlashOnOff.setVisibility(View.VISIBLE);
                    flag = 0;
                }
                surfaceCreated(surfaceHolder);
                break;
            default:
                break;
        }
    }

    private void flashToggle() {

        if (flashType == 1) {

            flashType = 2;
        } else if (flashType == 2) {

            flashType = 3;
        } else if (flashType == 3) {

            flashType = 1;
        }
        refreshCamera();
    }

    private void captureImage() {
        camera.takePicture(null, null, jpegCallback);
        inActiveCameraCapture();
    }

    private void releaseMediaRecorder() {
        try {

            if (mediaRecorder != null) {
                mediaRecorder.reset();   // clear recorder configuration
                mediaRecorder.release(); // release the recorder object
                mediaRecorder = new MediaRecorder();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void refreshCamera() {

        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
            Camera.Parameters param = camera.getParameters();

            if (flag == 0) {
                if (flashType == 1) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);
                } else if (flashType == 2) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    Camera.Parameters params = null;
                    if (camera != null) {
                        params = camera.getParameters();

                        if (params != null) {
                            List<String> supportedFlashModes = params.getSupportedFlashModes();

                            if (supportedFlashModes != null) {
                                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                                }
                            }
                        }
                    }
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);
                } else if (flashType == 3) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
                }
            }


            refrechCameraPriview(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refrechCameraPriview(Camera.Parameters param) {
        try {
            camera.setParameters(param);
            setCameraDisplayOrientation(0);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCameraDisplayOrientation(int cameraId) {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();

            if (Build.MODEL.equalsIgnoreCase("Nexus 6") && flag == 1) {
                rotation = Surface.ROTATION_180;
            }
            int degrees = 0;
            switch (rotation) {

                case Surface.ROTATION_0:

                    degrees = 0;
                    break;

                case Surface.ROTATION_90:

                    degrees = 90;
                    break;

                case Surface.ROTATION_180:

                    degrees = 180;
                    break;

                case Surface.ROTATION_270:

                    degrees = 270;
                    break;

            }

            int result;

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360; // compensate the mirror

            } else {
                result = (info.orientation - degrees + 360) % 360;

            }

            camera.setDisplayOrientation(result);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //------------------SURFACE CREATED FIRST TIME--------------------//

    int flashType = 1;

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            if (flag == 0) {
                camera = Camera.open(0);
            } else {
                camera = Camera.open(1);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        try {
            Camera.Parameters param;
            param = camera.getParameters();
            List<Camera.Size> sizes = param.getSupportedPreviewSizes();
            //get diff to get perfact preview sizes
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            long diff = (height * 1000 / width);
            long cdistance = Integer.MAX_VALUE;
            int idx = 0;
            for (int i = 0; i < sizes.size(); i++) {
                long value = (long) (sizes.get(i).width * 1000) / sizes.get(i).height;
                if (value > diff && value < cdistance) {
                    idx = i;
                    cdistance = value;
                }
                Log.e("WHHATSAPP", "width=" + sizes.get(i).width + " height=" + sizes.get(i).height);
            }
            Log.e("WHHATSAPP", "INDEX:  " + idx);
            Camera.Size cs = sizes.get(idx);
            param.setPreviewSize(cs.width, cs.height);
            param.setPictureSize(cs.width, cs.height);
            camera.setParameters(param);
            setCameraDisplayOrientation(0);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            if (flashType == 1) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);

            } else if (flashType == 2) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                Camera.Parameters params = null;
                if (camera != null) {
                    params = camera.getParameters();

                    if (params != null) {
                        List<String> supportedFlashModes = params.getSupportedFlashModes();

                        if (supportedFlashModes != null) {
                            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                            }
                        }
                    }
                }
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);

            } else if (flashType == 3) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        try {
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera();
    }

    //------------------SURFACE OVERRIDE METHIDS END--------------------//

    private final long timeInMilliseconds = 0L;
    private long startTime = SystemClock.uptimeMillis();
    private final long updatedTime = 0L;
    private final long timeSwapBuff = 0L;
    private final Runnable updateTimerThread = new Runnable() {

        public void run() {

//            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
//            updatedTime = timeSwapBuff + timeInMilliseconds;
//
//            int secs = (int) (updatedTime / 1000);
//            int mins = secs / 60;
//            int hrs = mins / 60;
//
//            secs = secs % 60;
//
//            textCounter.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
//            customHandler.postDelayed(this, 0);


            seconds=10;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            String time
                    = String
                    .format(Locale.getDefault(),
                            "%02d:%02d",
                            minutes, secs);
            textCounter.setText(time);
            seconds--;
            customHandler.postDelayed(updateTimerThread,1000);
            if (seconds==0){
                saveVideoTask = new SaveVideoTask();
                saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                customHandler.removeCallbacks(updateTimerThread);

            }

//            customHandler.postDelayed(runnable=new Runnable() {
//                @Override
//                public void run() {
//
//                    seconds=10;
//                    customHandler.postDelayed(runnable,1000);
//                    int hours = seconds / 3600;
//                    int minutes = (seconds % 3600) / 60;
//                    int secs = seconds % 60;
//                        String time
//                                = String
//                                .format(Locale.getDefault(),
//                                        "%02d:%02d",
//                                        minutes, secs);
//                        textCounter.setText(time);
//                        seconds--;
//                        if (seconds==0){
//                            customHandler.removeCallbacks(runnable);
//                            mediaRecorder.stop();
//                        }
//                }
//            },100);

        }

    };



    private void scaleUpAnimation() {
        try{
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 2f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 2f);
            scaleDownX.setDuration(100);
            scaleDownY.setDuration(100);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    View p = (View) imgCapture.getParent();
                    p.invalidate();
                }
            });
            scaleDown.start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void scaleDownAnimation() {
        try {

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 1f);
            scaleDownX.setDuration(100);
            scaleDownY.setDuration(100);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleDownX).with(scaleDownY);

            scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    View p = (View) imgCapture.getParent();
                    p.invalidate();
                }
            });
            scaleDown.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {

            if (customHandler != null)
                customHandler.removeCallbacksAndMessages(null);
            customHandler.removeCallbacks(runnable);///for remove handler

            releaseMediaRecorder();       // if you are using MediaRecorder, release it first

            if (myOrientationEventListener != null)
                myOrientationEventListener.enable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SaveVideoTask saveVideoTask = null;

    private void activeCameraCapture() {
        try {
            if (imgCapture != null) {
                imgCapture.setAlpha(1.0f);
                imgCapture.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        hintTextView.setVisibility(View.INVISIBLE);
                        try {
                            if (prepareMediaRecorder()) {
                                myOrientationEventListener.disable();
                                mediaRecorder.start();
                                startTime = SystemClock.uptimeMillis();

                                seconds=10;
                                customHandler.postDelayed(runnable=new Runnable() {
                                    @Override
                                    public void run() {
                                        customHandler.postDelayed(runnable,1000);//It run after 1 sec delay
                                        int hours = seconds / 3600;
                                        int minutes = (seconds % 3600) / 60;
                                        int secs = seconds % 60;

                                        String time
                                                = String
                                                .format(Locale.getDefault(),
                                                        "%02d:%02d",
                                                        minutes, secs);
                                        textCounter.setText(time);
                                        seconds--;


                                        if (seconds==0){
                                            customHandler.removeCallbacks(runnable);

                                            saveVideoTask = new SaveVideoTask();
                                            saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                        }

                                    }
                                },100);//run with this delay when on click

                            } else {
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        textCounter.setVisibility(View.VISIBLE);
                        imgSwipeCamera.setVisibility(View.GONE);
                        imgFlashOnOff.setVisibility(View.GONE);
                        scaleUpAnimation();
                        imgCapture.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                                    return true;
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {

                                    scaleDownAnimation();
                                    hintTextView.setVisibility(View.VISIBLE);

                                    cancelSaveVideoTaskIfNeed();
                                    saveVideoTask = new SaveVideoTask();
                                    saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                    return true;
                                }
                                return true;

                            }
                        });
                        return true;
                    }

                });
                imgCapture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //for video  ----
//                    seconds=10;
                        hintTextView.setVisibility(View.INVISIBLE);
                        try {
                            if (prepareMediaRecorder()) {
                                myOrientationEventListener.disable();
                                mediaRecorder.start();
                                startTime = SystemClock.uptimeMillis();

//                            customHandler.postDelayed(updateTimerThread, 100);
                                seconds=10;
                                customHandler.postDelayed(runnable=new Runnable() {
                                    @Override
                                    public void run() {
                                        customHandler.postDelayed(runnable,1000);//It run after 1 sec delay
                                        int hours = seconds / 3600;
                                        int minutes = (seconds % 3600) / 60;
                                        int secs = seconds % 60;

                                        String time
                                                = String
                                                .format(Locale.getDefault(),
                                                        "%02d:%02d",
                                                        minutes, secs);
                                        textCounter.setText(time);
                                        seconds--;


                                        if (seconds==0){
                                            customHandler.removeCallbacks(runnable);

                                            saveVideoTask = new SaveVideoTask();
                                            saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                        }

                                    }
                                },100);//run with this delay when on click

                            } else {
//                            return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        textCounter.setVisibility(View.VISIBLE);
                        imgSwipeCamera.setVisibility(View.INVISIBLE);//Gone
                        imgFlashOnOff.setVisibility(View.INVISIBLE);//Gone
//                    scaleUpAnimation();//animate the capture icon
                        imgCapture.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                                    return true;
                                }
                                if (event.getAction() == MotionEvent.ACTION_UP) {

//                                scaleDownAnimation();//animate the capture icon
                                    hintTextView.setVisibility(View.VISIBLE);

                                    cancelSaveVideoTaskIfNeed();
                                    saveVideoTask = new SaveVideoTask();
                                    saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                    return true;
                                }
                                return true;

                            }
                        });
//                    return true;

                        //For camera ---------------
//                    if (isSpaceAvailable()) {
//                        captureImage();
//                    } else {
//                        Toast.makeText(WhatsappCameraActivity.this, "Memory is not available", Toast.LENGTH_SHORT).show();
//                    }
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onVideoSendDialog(final String videopath, final String thumbPath) {

        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (videopath != null) {
                        File fileVideo = new File(videopath);
                        long fileSizeInBytes = fileVideo.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;
                        if (fileSizeInMB > MAX_VIDEO_SIZE_UPLOAD) {
                            new AlertDialog.Builder(WhatsappCameraActivity.this)
                                    .setMessage(getString(R.string.file_limit_size_upload_format, ""+MAX_VIDEO_SIZE_UPLOAD))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {

                            VideoMessageGajaBandhuActivity.videoPath= videopath;
                            finish();

//                        Intent mIntent = new Intent(WhatsappCameraActivity.this, PhotoVideoRedirectActivity.class);
//                        mIntent.putExtra("PATH", videopath.toString());
//                        mIntent.putExtra("THUMB", thumbPath.toString());
//                        mIntent.putExtra("WHO", "Video");
//                        startActivity(mIntent);

                            //SendVideoDialog sendVideoDialog = SendVideoDialog.newInstance(videopath, thumbPath, name, phoneNuber);
                            // sendVideoDialog.show(getSupportFragmentManager(), "SendVideoDialog");
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inActiveCameraCapture() {
        try {
            if (imgCapture != null) {
                imgCapture.setAlpha(1.0f);
                imgCapture.setOnClickListener(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //--------------------------CHECK FOR MEMORY -----------------------------//

    public int getFreeSpacePercantage() {
        int percantage = (int) (freeMemory() * 100 / totalMemory());
        int modValue = percantage % 5;
        return percantage - 1;
//        return percantage - modValue;
    }

    public double totalMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getBlockCount() * (double) stat.getBlockSize();
        return sdAvailSize / 1073741824;
    }

    public double freeMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        return sdAvailSize / 1073741824;
    }

    public boolean isSpaceAvailable() {
        return getFreeSpacePercantage() >= 1;
    }
    //-------------------END METHODS OF CHECK MEMORY--------------------------//


    private String mediaFileName = null;

    @SuppressLint("SimpleDateFormat")
    protected boolean prepareMediaRecorder() throws IOException {
        try {

            mediaRecorder = new MediaRecorder(); // Works well
            camera.stopPreview();
            camera.unlock();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (flag == 1) {
                mediaRecorder.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH));
            } else {
                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            }
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

            mediaRecorder.setOrientationHint(mOrientation);

            if (Build.MODEL.equalsIgnoreCase("Nexus 6") && flag == 1) {

                if (mOrientation == 90) {
                    mediaRecorder.setOrientationHint(mOrientation);
                } else if (mOrientation == 180) {
                    mediaRecorder.setOrientationHint(0);
                } else {
                    mediaRecorder.setOrientationHint(180);
                }

            } else if (mOrientation == 90 && flag == 1) {
                mediaRecorder.setOrientationHint(270);
            } else if (flag == 1) {
                mediaRecorder.setOrientationHint(mOrientation);
            }
            mediaFileName = "wc_vid_" + System.currentTimeMillis();
            if(Build.VERSION.SDK_INT >= 29) {
                mediaRecorder.setOutputFile(tempFile + "/" + mediaFileName + ".mp4"); // Environment.getExternalStorageDirectory()

            }else {
                mediaRecorder.setOutputFile(folder + "/" + mediaFileName + ".mp4"); // Environment.getExternalStorageDirectory()

            }
//            mediaRecorder.setOutputFile(folder.getAbsolutePath() + "/" + mediaFileName + ".mp4"); // Environment.getExternalStorageDirectory()

            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

                public void onInfo(MediaRecorder mr, int what, int extra) {
                    // TODO Auto-generated method stub

                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {

                        long downTime = 0;
                        long eventTime = 0;
                        float x = 0.0f;
                        float y = 0.0f;
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(
                                downTime,
                                eventTime,
                                MotionEvent.ACTION_UP,
                                0,
                                0,
                                metaState
                        );

                        imgCapture.dispatchTouchEvent(motionEvent);

                        Toast.makeText(WhatsappCameraActivity.this, "You reached to Maximum(25MB) video size.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mediaRecorder.setMaxFileSize(1000 * 25 * 1000);

            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                releaseMediaRecorder();
                e.printStackTrace();
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    OrientationEventListener myOrientationEventListener;
    int iOrientation = 0;
    int mOrientation = 90;

    public void generateVideoThmb(String srcFilePath, File destFile) {
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(srcFilePath, 120);
            FileOutputStream out = new FileOutputStream(destFile);
            ThumbnailUtils.extractThumbnail(bitmap, 200, 200).compress(Bitmap.CompressFormat.JPEG, 30, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int normalize(int degrees) {
        try {

            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException("Error....");

    }

    private int getPhotoRotation() {
        int rotation;
        int orientation = mPhotoAngle;

        Camera.CameraInfo info = new Camera.CameraInfo();
        if (flag == 0) {
            Camera.getCameraInfo(0, info);
        } else {
            Camera.getCameraInfo(1, info);
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {
            rotation = (info.orientation + orientation) % 360;
        }
        return rotation;
    }


}
