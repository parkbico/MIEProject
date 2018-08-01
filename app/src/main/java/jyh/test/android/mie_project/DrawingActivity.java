package jyh.test.android.mie_project;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class DrawingActivity extends AppCompatActivity {

    private DrawView mDrawView;
    private MyColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        FrameLayout frame = findViewById(R.id.frame);

        Button btnUndo, btnRedo, btnColorPickerD, btnColor, btnCapture;

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnColorPickerD = findViewById(R.id.btnColorPickerD);
        btnColor = findViewById(R.id.btnColor);
        btnCapture = findViewById(R.id.btnCapture);

        mDrawView = new DrawView(this);
        frame.addView(mDrawView);

        colorPicker = new MyColorPicker(this, mDrawView);

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.onClickUndo();
            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.onClickRedo();
            }
        });

        btnColorPickerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawView.setPicking(true);
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPicker.showPicker(view);
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"캡쳐완료", Toast.LENGTH_SHORT).show();
                View rootView = getWindow().getDecorView();//activity의 view정보 구하기

                File screenShot = ScreenShot(rootView);
                if (screenShot != null) {
                    //저장
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

                }
            }
        });

        onConfigurationChanged(Resources.getSystem().getConfiguration());
    }

    //화면 캡쳐하기
    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename =  dateName(System.currentTimeMillis());
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);//Pictures폴더 filename 파일
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);  //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }

    private String dateName(long dateTaken){
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
        return dateFormat.format(date)+".png";
    }

}
