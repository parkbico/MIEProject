package jyh.test.android.mie_project;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class DrawingActivity extends AppCompatActivity {

    private FrameLayout frame;

    private Button btnUndo, btnRedo, btnColorPickerD, btnColor;

    private DrawView mDrawView;

    private MyColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        frame = findViewById(R.id.frame);

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnColorPickerD = findViewById(R.id.btnColorPickerD);
        btnColor = findViewById(R.id.btnColor);

        mDrawView = new DrawView(this);
        frame.addView(mDrawView);

        colorPicker = new MyColorPicker();
        colorPicker.init(this, mDrawView);

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
