package com.cris.edu.camera1;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用Camera API实现的相机预览类
 * Created by Cris on 2017/7/18.
 */

public class CameraPreview  extends TextureView implements  TextureView.SurfaceTextureListener{
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private static int mCameraId=0;
    private int rotation;//纠正预览方向需要用到的角度
    private int result;

    public CameraPreview(Context context){
        super(context);
        //这句话不能遗漏
        setSurfaceTextureListener(this);
    }

    public CameraPreview(Context context,int rotation){
        super(context);
        this.rotation=rotation;
        //这句话不能遗漏
        setSurfaceTextureListener(this);
    }


    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera();
        //校正预览方向
        setCameraDisplayOrientation(rotation,mCameraId,mCamera);
        setProperPreviewSize(width,height);
        adjustDisplayRatio(result);
        startPreview();
    }

    /**
     * 打开相机
     */
    private void openCamera(){
        if (mCamera==null) {
            try {
                mCamera = Camera.open(mCameraId);
                mParameters=mCamera.getParameters();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 切换前后摄像头
     */
    public static void switchCamera(){
        mCameraId=1-mCameraId;
    }
    /**
     * 开启预览
     */
    private void startPreview(){
        if (mCamera!=null){
            SurfaceTexture surfaceTexture=getSurfaceTexture();
            try {
                mCamera.setPreviewTexture(surfaceTexture);
                mCamera.startPreview();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**校正预览方向
     * @param rotation
     * @param cameraId
     * @param camera
     */
    public void setCameraDisplayOrientation(int rotation,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    /**
     * 关闭相机
     */
    private void closeCamera(){
        mCamera.stopPreview();
        mCamera.release();
    }


    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        closeCamera();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    /**
     * 设置合适的预览分辨率
     * @param width
     * @param height
     */
    private void setProperPreviewSize(int width,int height){
        List<Camera.Size> choices=mParameters.getSupportedPreviewSizes();
        //直接选择最大的但比height，width比小的的预览大小
        Camera.Size mPreviewSize=chooseMaxSize(choices,height,width);
        mParameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
        mCamera.setParameters(mParameters);
    }

    /**
     * 选择最大的预览分辨率 但不能比手机屏幕宽高比大
     * @param choices
     * @return
     */
    private static Camera.Size chooseMaxSize( List<Camera.Size> choices,int height,int width) {
        ArrayList<Camera.Size> sizeList=new ArrayList<Camera.Size>();
        double ratio=Math.max(1.0*height/width,1.0*width/height);
        for(Camera.Size option : choices) {
            double sizeRatio=Math.max(1.0*option.height/option.width,1.0*option.width/option.height);
            if (sizeRatio<ratio){
                sizeList.add(option);
            }
        }
        Camera.Size maxSize=sizeList.get(0);
        for (Camera.Size option:sizeList){
            if (option.height*option.width>maxSize.height*maxSize.width){
                maxSize=option;
            }
        }
        return maxSize;
    }

    /**
     * 根据预览分辨率设置画面大小
     * result与手机是否旋转有关，这里写死，写成portrait
     * @param result
     */
    private void adjustDisplayRatio(int result) {
        ViewGroup parent = ((ViewGroup) getParent());
        Rect rect = new Rect();
        parent.getLocalVisibleRect(rect);
        int width = rect.width();
        int height = rect.height();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        int previewWidth;
        int previewHeight;
        if (result == 90 || result == 270) {
            previewWidth = previewSize.height;
            previewHeight = previewSize.width;
        } else {
            previewWidth = previewSize.width;
            previewHeight = previewSize.height;
        }
        //在这里设置预览画面大小
        if (width * previewHeight > height * previewWidth) {
            final int scaledChildWidth = previewWidth * height / previewHeight;

            layout((width - scaledChildWidth) / 2, 0,
                    (width + scaledChildWidth) / 2, height);
        } else {
            final int scaledChildHeight = previewHeight * width / previewWidth;
            layout(0, (height - scaledChildHeight) / 2,
                    width, (height + scaledChildHeight) / 2);
        }
    }

}
