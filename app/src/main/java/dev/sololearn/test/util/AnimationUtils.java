package dev.sololearn.test.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AnimationUtils {

    public static void showAnimateViewHeight(View view, int toHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float newHeight = toHeight * (float) valueAnimator1.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) newHeight;
            view.setLayoutParams(params);

        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    public static void showAnimateViewWidth(View view, int toWidth) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float newWidth = toWidth * (float) valueAnimator1.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) newWidth;
            view.setLayoutParams(params);

        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    public static void hideAnimateViewHeight(View view, int currentHeight) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0f);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float newHeight = currentHeight * (float) valueAnimator1.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) newHeight;
            view.setLayoutParams(params);

        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    public static void hideAnimateViewWidth(View view, int currentWidth) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0f);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            float newWidth = currentWidth * (float) valueAnimator1.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) newWidth;
            view.setLayoutParams(params);

        });
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    public static void showViewWithAlphaAnimation(View view) {
        view.animate().alpha(1f).start();
    }

    public static void hideViewWithAlphaAnimation(View view) {
        view.animate().alpha(0f).start();
    }
}
