package jyh.test.android.mie_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.io.ByteArrayOutputStream;

public class MakeTextActivity extends Activity {

    FrameLayout txtFrame;

    EditText editText ;
    Button btnPencilTxt , btnPlusTxt ;

    Bundle bundleTxtResult ;
    Intent intentResult ;

    String checkFileName ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_text);

        //find id
        txtFrame     = findViewById(R.id.txtFrame);
        editText     = findViewById(R.id.editText);
        btnPencilTxt = findViewById(R.id.btnPencilTxt);
        btnPlusTxt   = findViewById(R.id.btnPlusTxt);

        //intent, bundle
        Intent intentTxt        = getIntent();
        Bundle bundleTxt        = intentTxt.getExtras();

        if(bundleTxt != null)
            checkFileName    = bundleTxt.getString("fileName");

        //click
        btnPlusTxt.setOnClickListener( plusClick );
        btnPencilTxt.setOnClickListener( plusClick );

        //return result
        bundleTxtResult = new Bundle();
        intentResult     = new Intent();

        //set text color


    }//onCreate

    View.OnClickListener plusClick =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch ( view.getId() ){
                case R.id.btnPlusTxt:

                    editText.setCursorVisible(false);
                    editText.buildDrawingCache();
                    Bitmap bmp = Bitmap.createBitmap(editText.getDrawingCache());

                    //bitmap 을 바로 bundle에 담지 못하기 때문에 ByteArray로 처리
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG , 100 , stream);
                    byte[] byteArray = stream.toByteArray();
                    //bundle에 넣어줌

                    bundleTxtResult.putByteArray("txtImage" , byteArray);

                    //intent에 bundle 넣어줌
                    intentResult.putExtras(bundleTxtResult);

                    //결과 보내기
                    setResult(RESULT_OK , intentResult);

                    //make text 창은 닫아줌
                    finish();

                    break;

                case R.id.btnPencilTxt:


                    break;
            }
        }
    };
}
