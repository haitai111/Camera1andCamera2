package com.cris.edu.camera1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSIONS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //一、运行时权限声明
        //step1:检查权限
        if (((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) != PackageManager.PERMISSION_GRANTED))
        {
            //step2:申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS);
        }
        getFragmentManager().beginTransaction().
                add(R.id.container,new CameraFragment()).commit();

    }

    //step3:处理权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSIONS){
            if (grantResults.length>0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ){
            }else {
                Toast.makeText(this,"你拒绝了相机权限，无法使用该软件", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
