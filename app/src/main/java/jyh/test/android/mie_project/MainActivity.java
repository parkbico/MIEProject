package jyh.test.android.mie_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnNewDrawing , btnNewText;
    //test..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission( this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            setPermission();
        }

        btnNewDrawing = findViewById(R.id.btnNewDrawing);
        btnNewDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DrawingActivity.class);
                startActivity(i);
            }
        });
    }


    //앱 권한설정 감지자
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //모든 권한이 허락 완료된 경우
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            //한 가지라도 허용되지 않은 권한이 있을 경우
            finish();
        }
    };


    //앱 권한설정 TedPermission 설정
    private void setPermission(){
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("갤러리 접근 권한이 필요합니다. \n 권한 수락을 해주세요.")
                .setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).check();
    }

}