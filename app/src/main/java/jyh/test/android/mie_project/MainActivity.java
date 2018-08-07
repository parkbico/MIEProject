package jyh.test.android.mie_project;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class MainActivity extends Activity {

    Button btnNewDrawing , btnNewText , btnCreateNewFile , btnCreateCancel;
    Dialog newDrawingDialog;
    EditText editNewFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(
                this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            setPermission();
        }

        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            setPermission();
        }

        btnNewDrawing = findViewById(R.id.btnNewDrawing);
        btnNewDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDrawingDialog = new Dialog(MainActivity.this);
                newDrawingDialog.setContentView(R.layout.createnew_dialog);

                btnCreateNewFile = newDrawingDialog.findViewById(R.id.btnCreateNewFile);
                btnCreateCancel  = newDrawingDialog.findViewById(R.id.btnCreateCancel);
                editNewFileName  = newDrawingDialog.findViewById(R.id.editNewFileName);

                btnCreateNewFile.setOnClickListener( dialogClick );
                btnCreateCancel.setOnClickListener( dialogClick );

                newDrawingDialog.setTitle("신규생성");
                newDrawingDialog.show();
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
                .setDeniedMessage("앱에서 요구하는 권한이 있습니다. \n 권한 수락을 해주세요.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    View.OnClickListener dialogClick =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch ( view.getId() ){

                case R.id.btnCreateNewFile:
                    String filename         = editNewFileName.getText().toString();

                    if(filename.isEmpty() ){
                        Toast.makeText(getApplicationContext() , "파일명을 입력해주세요" , Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent i                = new Intent(MainActivity.this, DrawingActivity.class);

                    Bundle filenameBundle   = new Bundle();
                    filenameBundle.putString( "fileName" , filename );
                    i.putExtras(filenameBundle);

                    //중복방지
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);

                    //dialog 없애주기
                    newDrawingDialog.dismiss();

                    break;

                case R.id.btnCreateCancel:

                    newDrawingDialog.dismiss();
                    break;

            }
        }
    };

}