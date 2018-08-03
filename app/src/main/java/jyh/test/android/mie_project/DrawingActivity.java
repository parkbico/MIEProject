package jyh.test.android.mie_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
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

    //gallery
    private static final int GALLERY_CODE = 2;
    private String selectedImagePath;
    private Uri mImageCaptureUri;

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
                case R.id.maketext:
                    Toast.makeText(DrawingActivity.this , "텍스트 추가하기" , Toast.LENGTH_LONG).show();
                    break;
                case R.id.gallery:

                    selectGallery();

                    break;
                case R.id.camera:
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String url = "tmp" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                    mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(),
                            "jyh.test.android.mie_project",
                            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), url));

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
            Matrix matrix = new Matrix();
            Bitmap bitmap = null;
            Bitmap scaledBitmap;
            Bitmap rotatedBitmap;

            if(requestCode == TAKE_CAMERA) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap.getWidth() > bitmap.getHeight())
                    matrix.postRotate(90);

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, frame.getMeasuredHeight(), frame.getMeasuredWidth(), true);
                rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), rotatedBitmap, "Title", null);

                CropImage.activity(Uri.parse(path))
                        .setAspectRatio(frame.getMeasuredWidth(), frame.getMeasuredHeight())
                        .start(this);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                scaledBitmap = Bitmap.createScaledBitmap(bitmap, frame.getMeasuredWidth(), frame.getMeasuredHeight(), true);
                rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                mDrawView.setCameraPicture(rotatedBitmap);

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists())
                    f.delete();

            } else if (requestCode == GALLERY_CODE) {
                if (data.getData() != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);

                    CropImage.activity(Uri.parse(path))
                            .setAspectRatio(frame.getMeasuredWidth(), frame.getMeasuredHeight())
                            .start(this);

                    //mDrawView.setCameraPicture(bitmapG);
                }
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

    //갤러리 이동
    public void selectGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent , GALLERY_CODE);

    }


}
