package com.example.muztalk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.muztalk.camera.CameraSourcePreview;
import com.example.muztalk.camera.FaceGraphic;
import com.example.muztalk.camera.GraphicOverlay;
import com.example.muztalk.camera.TextGraphic;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.view.View.GONE;

public class FacecameraActivity extends AppCompatActivity {

    TextGraphic mTextGraphic;

    private final Thread observer = new Thread("observer")
    {
        {
            setDaemon(true);
        }

        public void run()
        {
            while(!isInterrupted())
            {
                /*
                TextGraphic mTextGraphic = new TextGraphic(mGraphicOverlay);
                mGraphicOverlay.add(mTextGraphic);*/
                //mTextGraphic.updateText(2);
            }
        }
    };

    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private int typeFace = 0;
    private int typeFlash = 0;
    private boolean flashmode = false;
    private Camera camera;
    CameraSource.PictureCallback jpegCallback;

    private static final int[] MASK = {
            R.id.no_filter,
            R.id.snap,
            R.id.dog,
            R.id.glasses2,
            R.id.glasses3,
            R.id.glasses4,
            R.id.glasses5,
            R.id.mask,
            R.id.mask2,
            R.id.mask3,
            R.id.mask4,
            R.id.cat2,
            R.id.op
    };

    ImageButton FACE , NO_FILTER, MASK4, OP, SNAP, GLASSES2, GLASSES3, GLASSES4, GLASSES5, MASK1, MASK2, MASK3, DOG, CAT2;
    ImageButton FLIP_CAMERA,FLASH,CAPTURE;
    TextView EXIT_CAMERA;
    SharedPreff sharedPreff;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if (sharedPreff.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facecamera);
        init_fun();
        Objects.requireNonNull(getSupportActionBar()).hide();
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        //mTextGraphic = new TextGraphic(mGraphicOverlay);
        //mGraphicOverlay.add(mTextGraphic);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);




        FACE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.scrollView).getVisibility() == GONE){
                    findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.selected_face_icon);
                }else{
                    findViewById(R.id.scrollView).setVisibility(GONE);
                    ((ImageButton) findViewById(R.id.face)).setImageResource(R.drawable.face);
                }
            }
        });


        ///////////////////////FACE FILTERS
        NO_FILTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=0;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        SNAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=1;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        DOG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=2;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        GLASSES2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=3;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        GLASSES3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=4;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        GLASSES4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=5;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        GLASSES5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=6;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        MASK1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=7;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        MASK2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=8;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        MASK3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=9;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        MASK4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=10;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        CAT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=11;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });

        OP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background);
                typeFace=12;
                findViewById(MASK[typeFace]).setBackgroundResource(R.drawable.round_background_select);
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        FLIP_CAMERA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        FLASH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FLASH.setImageResource(R.drawable.flash_selected);

              /*  int blue;
                if(flashmode==false){
                    flashmode = true;
                    blue = 0;
                }else{
                    flashmode = false;
                    blue = 255;
                }
                FLASH.setColorFilter(Color.argb(255, 255, 255, blue));*/
            }
        });

        CAPTURE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takeImage();
                onPause();

                CAPTURE.setVisibility(GONE);
                FLIP_CAMERA.setVisibility(GONE);
                FACE.setVisibility(GONE);
                FLASH.setVisibility(GONE);


            }
        });

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        }
        else {
            requestCameraPermission();
        }

        EXIT_CAMERA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacecameraActivity.this, TotalchatsActivity.class);
                startActivity(intent);
            }
        });

    }
    private void init_fun() {
        FACE = findViewById(R.id.face);
        NO_FILTER = findViewById(R.id.no_filter);
        MASK4 = findViewById(R.id.mask4);
        OP = findViewById(R.id.op);
        SNAP = findViewById(R.id.snap);
        GLASSES2 = findViewById(R.id.glasses2);
        GLASSES3 = findViewById(R.id.glasses3);
        GLASSES4 = findViewById(R.id.glasses4);
        GLASSES5 = findViewById(R.id.glasses5);
        MASK1 = findViewById(R.id.mask);
        MASK2 = findViewById(R.id.mask2);
        MASK3 = findViewById(R.id.mask3);
        DOG = findViewById(R.id.dog);
        CAT2 = findViewById(R.id.cat2);
        FLIP_CAMERA = findViewById(R.id.flip_camera);
        FLASH = findViewById(R.id.flash);
        CAPTURE = findViewById(R.id.capture_image);
        EXIT_CAMERA = findViewById(R.id.exit_camera);
    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        //new MultiProcessor.Builder<>(new GraphicTextTrackerFactory()).build();

        if (!detector.isOperational()) {

            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                //.setRequestedPreviewSize(640, 480)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(20.0f)
                .build();
        //observer.start();
        /*
        TextGraphic mTextGraphic = new TextGraphic(mGraphicOverlay);
        mGraphicOverlay.add(mTextGraphic);
        mTextGraphic.updateText(2);*/
    }

    private void createCameraSource_front() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        //new MultiProcessor.Builder<>(new GraphicTextTrackerFactory()).build();

        if (!detector.isOperational()) {

            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                //.setRequestedPreviewSize(640, 480)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(20.0f)
                .build();
        //observer.start();
        /*
        TextGraphic mTextGraphic = new TextGraphic(mGraphicOverlay);
        mGraphicOverlay.add(mTextGraphic);
        mTextGraphic.updateText(2);*/
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
    private void takeImage() {
        try {
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //releaseCameraSource();
            //releaseCamera();
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //setUpCamera(camera);
            //Thread.sleep(1000);

           /* mCameraSource.takePicture(null, null, new CameraSource.PictureCallback() {
                //@SuppressLint({"WrongConstant", "SdCardPath", "DefaultLocale"})
                @Override
                public void onPictureTaken(byte[] data) {
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(String.format("/sdcard/Muztalk%d.jpg", System.currentTimeMillis()));
                        outStream.write(data);
                        outStream.close();
                        Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                    Toast.makeText(getApplicationContext(), "Picture Saved", 2000).show();
                    refreshCamera();
                }

            });*/


            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                private File imageFile;
                @Override
                public void onPictureTaken(byte[] data) {
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(String.format("/sdcard/Muztalk%d.jpg", System.currentTimeMillis()));
                        outStream.write(data);
                        outStream.close();
                        Toast.makeText(FacecameraActivity.this, "Picture Saved", Toast.LENGTH_LONG).show();
                        Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                        // convert byte array into bitmap
                        /*Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);

                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(getWindowManager().getDefaultDisplay().getRotation());
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                        String state = Environment.getExternalStorageState();
                        File folder = null;
                        if (state.contains(Environment.MEDIA_MOUNTED)) {
                            folder = new File(Environment
                                    .getExternalStorageDirectory()+"/faceFilter");
                        } else {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/faceFilter");
                        }

                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            java.util.Date date = new java.util.Date();
                            imageFile = new File(folder.getAbsolutePath()
                                    + File.separator
                                    //+ new Timestamp(date.getTime()).toString()
                                    + "Image.jpg");

                            imageFile.createNewFile();
                        } else {
                            Toast.makeText(getBaseContext(), "Image Not saved",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                        // save image into gallery
                        rotatedBitmap = resize(rotatedBitmap, 800, 600);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                        FileOutputStream fout = new FileOutputStream(imageFile);
                        fout.write(ostream.toByteArray());
                        fout.close();
                        ContentValues values = new ContentValues();

                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.MediaColumns.DATA,
                                imageFile.getAbsolutePath());

                        setResult(Activity.RESULT_OK); //add this
                        finish();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        String fail_msg = "Failed to save Image";
                        Log.d("",fail_msg);
                    }
                }
            });

        }catch (Exception ex){
            String fail_msg = "Failed to save Image";
            Log.d("",fail_msg);
        }

    }
    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, "Access to the camera needs permission for the detection.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }
    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }
    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }
    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("Permission is not granted")
                .setPositiveButton("OK", listener)
                .show();
    }

    /////////////////////--------------------TEXT TRACKER------------------///////////////////////
    private class GraphicTextTrackerFactory implements MultiProcessor.Factory<String> {
        @Override
        public Tracker<String> create(String face) {
            return new GraphicTextTracker(mGraphicOverlay);
        }
    }
    private class GraphicTextTracker extends Tracker<String> {
        private GraphicOverlay mOverlay;
        private TextGraphic mTextGraphic;

        GraphicTextTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mTextGraphic = new TextGraphic(overlay);
        }

        public void onUpdate() {
            mOverlay.add(mTextGraphic);
            mTextGraphic.updateText(3);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mTextGraphic);
        }
    }

    /////////////////////--------------------FACE TRACKER------------------///////////////////////
    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay,typeFace);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            Log.d("typface: ", String.valueOf(typeFace));
            Log.d("typface: ", String.valueOf(face));
            mFaceGraphic.updateFace(face,typeFace);

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

}