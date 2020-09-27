package com.example.muztalk;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback{

    final  int CAMERA_REQUEST_CODE = 1;
    Camera camera;
    SurfaceHolder mSurfaceHolder;
    SurfaceView mSurfaceView;
    ImageView CANCEL_CAMERA;

    public CameraFragment() {

    }

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_camera, container, false);
        mSurfaceView = view.findViewById(R.id.surfaceView_camera);
        mSurfaceHolder=mSurfaceView.getHolder();

        if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(),new String[] {android.Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
        }
        else
        {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        CANCEL_CAMERA = view.findViewById(R.id.cancel_camera);
        CANCEL_CAMERA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backtoroom();
            }
        });

        return view;
    }

    private void backtoroom() {
        Intent intent = new Intent(getContext(),RoomActivity.class);
        startActivity(intent);
        return;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters parameters;
        parameters= camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                else
                {
                    Toast.makeText(getContext(),"Please check app permissions",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}