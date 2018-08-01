package jyh.test.android.mie_project;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class DrawingActivity extends AppCompatActivity {

    private DrawView mDrawView;
    private MyColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        FrameLayout frame = findViewById(R.id.frame);

        final Button btnUndo, btnRedo, btnColorPickerD, btnColor;

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnColorPickerD = findViewById(R.id.btnColorPickerD);
        btnColor = findViewById(R.id.btnColor);

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

        onConfigurationChanged(Resources.getSystem().getConfiguration());
    }
}
