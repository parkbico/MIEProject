package jyh.test.android.mie_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class DrawingActivity extends Activity {
    final int TAKE_CAMERA = 1;

    private DrawView mDrawView;
    private MyColorPicker colorPicker;
    private FrameLayout frame;

    //top bar , menu
    Button btnMenu;
    TextView txtFileName;
    String fileName;
    AlertDialog.Builder finishDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        frame = findViewById(R.id.frame);

        Button btnUndo, btnRedo, btnColorPickerD, btnPencilD, btnEraserD, btnPlus ; // btnCapture;

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnColorPickerD = findViewById(R.id.btnColorPickerD);
        //btnCapture = findViewById(R.id.btnCapture);
        btnPencilD = findViewById(R.id.btnPencilD);
        btnEraserD = findViewById(R.id.btnEraserD);
        btnPlus = findViewById(R.id.btnPlus);

        //top bar , menu
        btnMenu       = findViewById(R.id.btnMenu);
        txtFileName  = findViewById(R.id.txtFileName);

        btnMenu.setOnClickListener( menuClick );

        Intent intent = getIntent();
        Bundle fileNameBundle = intent.getExtras();
        fileName = fileNameBundle.getString("fileName"); //사용자가 입력한 파일명
        txtFileName.setText(fileName);

        //top bar , menu - end


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

        btnPencilD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPicker.showPicker(view);
            }
        });

//        btnCapture.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View view) {
//                  Toast.makeText(getApplicationContext(), "저장하였습니다", Toast.LENGTH_SHORT).show();
//                  //View rootView = getWindow().getDecorView();//activity의 view정보 구하기
//
//                  File screenShot = ScreenShot(frame);
//                  if (screenShot != null) {
//                      //저장
//                      sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
//                      //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//
//                  }
//              }
//          });

        btnEraserD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.erase();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(DrawingActivity.this, view);
                getMenuInflater().inflate(R.menu.plus, popup.getMenu());
                popup.setOnMenuItemClickListener(onClickPlus);

                popup.show();
            }
        });

//        btnCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "캡쳐완료", Toast.LENGTH_SHORT).show();
//                View rootView = getWindow().getDecorView();//activity의 view정보 구하기
//
//                File screenShot = ScreenShot(frame);
//                if (screenShot != null) {
//                    //저장
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
//                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//
//                }
//            }
//        });

        onConfigurationChanged(Resources.getSystem().getConfiguration());
    }

    PopupMenu.OnMenuItemClickListener onClickPlus = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch(menuItem.getItemId()){
                case R.id.camera:
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_CAMERA);

                    break;
            }

            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            switch(requestCode){
                case TAKE_CAMERA:
                    if(data != null) {
                        Matrix matrix = new Matrix();

                        Bitmap origin = (Bitmap) data.getExtras().get("data");

                        if(origin.getWidth() > origin.getHeight())
                            matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(origin, frame.getMeasuredHeight(), frame.getMeasuredWidth(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        mDrawView.setCameraPicture(rotatedBitmap);
                    }

                    break;
            }
        }
    }

    //화면 캡쳐하기
    public File ScreenShot(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename =  dateName(System.currentTimeMillis());
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", fileName+"_"+filename);//Pictures폴더 filename 파일
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

    View.OnClickListener menuClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            PopupMenu popup = new PopupMenu(
                    DrawingActivity.this, //화면제어권자
                    btnMenu ); // anchor : 팝업메뉴를 띄울 기준 view

            //팝업메뉴가 참조할 리소스 파일
            getMenuInflater().inflate( R.menu.topmenu, popup.getMenu() );

            //팝업메뉴 이벤트 감지자
            popup.setOnMenuItemClickListener( click );

            //팝업메뉴 보이기
            popup.show();

        }
    };

    PopupMenu.OnMenuItemClickListener click = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch ( item.getItemId() ){

                case R.id.menu1:

                    Toast.makeText(getApplicationContext(), "캡쳐완료", Toast.LENGTH_SHORT).show();
                    View rootView = getWindow().getDecorView();//activity의 view정보 구하기

                    File screenShot = ScreenShot(frame);
                    if (screenShot != null) {
                        //저장
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

                    }

                    break;

                case R.id.menu2:

                    finishDialog = new AlertDialog.Builder(DrawingActivity.this);
                    finishDialog.setNegativeButton("취소" , null );
                    finishDialog.setPositiveButton("종료" , finishClick );

                    finishDialog.setTitle("종료하시겠습니까?");
                    finishDialog.setMessage("작업 내용은 저장되지 않습니다.");
                    finishDialog.show();

                    break;

            }//switch
            return true;
        }
    };

    DialogInterface.OnClickListener finishClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            switch( i ){
                case DialogInterface.BUTTON_POSITIVE :

                    finish();
                    break;

            }
        }
    };
}
