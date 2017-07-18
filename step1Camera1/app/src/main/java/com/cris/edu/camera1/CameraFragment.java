package com.cris.edu.camera1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * step1：实现了前后摄像头的方向正确，预览无变形的预览
 * Created by Cris on 2017/7/18.
 */

public class CameraFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.camera_preview,container,false);

        //添加预览界面
        int rotation =getActivity().getWindowManager().getDefaultDisplay()
                .getRotation();
        final CameraPreview mPreview = new CameraPreview(getActivity(),rotation);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button switchCamera=(Button)rootView.findViewById(R.id.btn_switch_camera);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPreview.switchCamera();
                getFragmentManager().beginTransaction().
                        replace(R.id.container, new CameraFragment()).commit();
            }
        });
        return rootView;
    }
}
