package com.example.idrecognizer.views.recognizer_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.StringRes;

import com.example.idrecognizer.common.helpers.Utils;
import com.example.idrecognizer.views.IdRectangleOverlay;
import com.example.idrecognizer.views.feature_detection_overlay.FeatureGraphicOverlay;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.view.CameraView;
import io.fotoapparat.view.FocusView;

public class RecognizerView extends FrameLayout {

    private CameraView cameraView;
    private FocusView focusView;
    private Fotoapparat fotoapparat;
    private IdRectangleOverlay rectangleOverlay;
    private Rect cropRectangle;
    private TextView hintText;
    private ProgressBar progressBar;
    private LinearLayout hintContainer;

    public RecognizerView(Context context) {
        super(context);
        init(context);
    }

    public RecognizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecognizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        cameraView = new CameraView(context);
        focusView = new FocusView(context);
        hintText = makeHintText();
        progressBar = makeProgressBar();

        cameraView.addView(focusView);
        addView(cameraView);
    }

    public void initFotoapparat(FrameProcessor frameProcessor){
        fotoapparat = Fotoapparat.with(getContext())
                .into(cameraView)
                .focusView(focusView)
                .previewScaleType(ScaleType.CenterCrop)
                .lensPosition(LensPositionSelectorsKt.back())
                .frameProcessor(frameProcessor)
                .build();
    }

    public void addCropRectangleOverlay(Rect rect){
        if (rectangleOverlay != null) { removeView(rectangleOverlay); }
        cropRectangle = rect;
        rectangleOverlay = new IdRectangleOverlay(getContext());
        rectangleOverlay.setCropRect(rect);
        addView(rectangleOverlay);
        setupHintContainer();
    }

    public void addFeatureDetectionOverlay(FeatureGraphicOverlay graphicOverlay){
        addView(graphicOverlay);
    }

    public void displayScanSuccess(boolean showFlipView){
        rectangleOverlay.toggle(showFlipView);
    }

    public void start(){
        fotoapparat.start();
    }

    public void stop(){
        fotoapparat.stop();
    }

    private void setupHintContainer(){
        //Vendosni anen e perparme te kartes se indetitetit ..
        if (hintContainer == null){
            hintContainer = new LinearLayout(getContext());
            hintContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            hintContainer.setOrientation(LinearLayout.VERTICAL);
            int paddingTop = Utils.dpToPx(getContext(),8);
            int paddingHorizontal = Utils.dpToPx(getContext(),16);
            hintContainer.setPadding(paddingHorizontal,paddingTop,paddingHorizontal,0);
            hintContainer.addView(progressBar);
            hintContainer.addView(hintText);
            addView(hintContainer);
        }
        hintContainer.setY(cropRectangle.bottom);
    }

    private TextView makeHintText(){
        TextView hintText = new TextView(getContext());
        hintText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        hintText.setTextColor(Color.WHITE);
        hintText.setGravity(Gravity.CENTER);
        hintText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        return hintText;
    }

    private ProgressBar makeProgressBar(){
        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        progressBar.setMax(100);
        progressBar.setIndeterminate(false);
        progressBar.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        return progressBar;
    }

    public void setProgress(@FloatRange(from = 0, to = 1) float progress){
        int progressScaled = Math.round(100 * progress);
        if (progressScaled > progressBar.getProgress()) {
            progressBar.setProgress(progressScaled, true);
        }
    }

    public void reset(){
        hintText.setText("");
        progressBar.setProgress(0);
    }

    public void setHintText(@StringRes int textResId){
        hintText.setText(textResId);
    }
}
