package jyh.test.android.mie_project;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;

public class MyColorPicker {
    private ColorPicker colorPicker;
    private SeekBar seekBar;

    private View cp_view;
    private PopupWindow cp_window;

    private DrawView drawView;

    public void init(Activity parent, DrawView drawView){
        this.drawView = drawView;

        cp_view = parent.getLayoutInflater().inflate(R.layout.color_picker, null);

        cp_window = new PopupWindow(cp_view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cp_window.setOutsideTouchable(true);
        cp_window.setBackgroundDrawable(new BitmapDrawable());

        colorPicker = (ColorPicker) cp_view.findViewById(R.id.cp_picker);
        seekBar = cp_view.findViewById(R.id.seekBar);

        colorPicker.setOnColorSelectedListener(onSetColor);
        seekBar.setOnSeekBarChangeListener(onSetValue);
    }

    public void showPicker(View parent){
        cp_window.showAtLocation(parent, Gravity.TOP|Gravity.RIGHT, 0, 0);

        drawView.setPopupShown(true);
    }

    private ColorPicker.OnColorSelectedListener onSetColor = new ColorPicker.OnColorSelectedListener() {
        @Override
        public void onColorSelected(int color) {
            drawView.setCurrentColor(color);
        }
    };

    private SeekBar.OnSeekBarChangeListener onSetValue = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            drawView.setCurrentValue(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
