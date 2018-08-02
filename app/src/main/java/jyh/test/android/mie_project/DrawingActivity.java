package jyh.test.android.mie_project;

import android.app.Dialog;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class DrawingActivity extends AppCompatActivity {
    final int CROP_FROM_CAMERA = 0;
    final int TAKE_CAMERA = 1;

    private DrawView mDrawView;
    private MyColorPicker colorPicker;
    private FrameLayout frame;

    private Uri mImageCaptureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        frame = findViewById(R.id.frame);

        Button btnUndo, btnRedo, btnColorPickerD, btnPencilD, btnEraserD, btnPlus, btnCapture;

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnColorPickerD = findViewById(R.id.btnColorPickerD);
        btnCapture = findViewById(R.id.btnCapture);
        btnPencilD = findViewById(R.id.btnPencilD);
        btnEraserD = findViewById(R.id.btnEraserD);
        btnPlus = findViewById(R.id.btnPlus);

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

        btnCapture.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Toast.makeText(getApplicationContext(), "저장하였습니다", Toast.LENGTH_SHORT).show();
                  //View rootView = getWindow().getDecorView();//activity의 view정보 구하기

                  File screenShot = ScreenShot(frame);
                  if (screenShot != null) {
                      //저장
                      sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                      //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

                  }
              }
          });

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

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "캡쳐완료", Toast.LENGTH_SHORT).show();
                View rootView = getWindow().getDecorView();//activity의 view정보 구하기

                File screenShot = ScreenShot(frame);
                if (screenShot != null) {
                    //저장
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

                }
            }
        });

        onConfigurationChanged(Resources.getSystem().getConfiguration());
    }

    PopupMenu.OnMenuItemClickListener onClickPlus = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch(menuItem.getItemId()){
                case R.id.camera:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String url = "tmp" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(),
                            "jyh.test.android.mie_project",
                            new File(Environment.getExternalStorageDirectory(), url));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

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
                case CROP_FROM_CAMERA:
                    final Bundle extras = data.getExtras();

                    if(extras != null){
                        Matrix matrix = new Matrix();

                        Bitmap origin = extras.getParcelable("data");

                        if(origin.getWidth() > origin.getHeight())
                            matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(origin, frame.getMeasuredHeight(), frame.getMeasuredWidth(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        mDrawView.setCameraPicture(rotatedBitmap);

                        File f = new File(mImageCaptureUri.getPath());
                        if(f.exists())
                            f.delete();
                    }

                    break;

                case TAKE_CAMERA:
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(mImageCaptureUri, "image/*");

                    intent.putExtra("outputX", 90);
                    intent.putExtra("outputY", 90);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);

                    startActivityForResult(intent, CROP_FROM_CAMERA);

                    /*if(data != null) {
                        Matrix matrix = new Matrix();

                        Bitmap origin = (Bitmap) data.getExtras().get("data");

                        if(origin.getWidth() > origin.getHeight())
                            matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(origin, frame.getMeasuredHeight(), frame.getMeasuredWidth(), true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        mDrawView.setCameraPicture(rotatedBitmap);
                    }*/

                    break;
            }
        }
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
