package com.example.xyzreader.ui;

import android.animation.ValueAnimator;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

public class PhotographicPrintAnimator {

  final SoftReference<ImageView> mView;
  final ColorMatrix mColorMatrix;
  final float[] mBrightness = new float[]
      {
          1, 0, 0, 0, 0,
          0, 1, 0, 0, 0,
          0, 0, 1, 0, 0,
          0, 0, 0, 1, 0
      };

  final ColorMatrix mBrightnessColorMatrix;
  final Interpolator mInterpolator;

  public PhotographicPrintAnimator(ImageView v) {
    mView = new SoftReference<ImageView>(v);
    mColorMatrix = new ColorMatrix();
    mBrightnessColorMatrix = new ColorMatrix();
    mInterpolator = new AccelerateDecelerateInterpolator();
  }

  public void start(int duration) {


    final ValueAnimator v = ValueAnimator.ofFloat(0f, 1f);
    final ValueAnimator v2 = ValueAnimator.ofFloat(0f, 1f);

    v.setInterpolator(mInterpolator);
    v.setDuration(duration);
    v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mColorMatrix.setSaturation(animation.getAnimatedFraction());

        if (mView.get() != null) {
          mView.get().setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        } else {
          v.cancel();
          v2.cancel();
        }
      }
    });

    v2.setInterpolator(mInterpolator);
    v2.setDuration((int) (duration * 0.75));
    v2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {

        float brightness = (1f - animation.getAnimatedFraction()) * 255;
        mBrightness[4] = brightness;
        mBrightness[9] = brightness;
        mBrightness[14] = brightness;
        mBrightnessColorMatrix.set(mBrightness);
        mColorMatrix.postConcat(mBrightnessColorMatrix);

        if (mView.get() != null) {
          mView.get().setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        } else {
          v.cancel();
          v2.cancel();
        }
      }
    });

    if (mView.get() != null) {
      mView.get().setAlpha(0f);
      mView.get().animate().alpha(1.0f).setDuration(duration / 2);
      v.start();
      v2.start();
    }
  }
}
