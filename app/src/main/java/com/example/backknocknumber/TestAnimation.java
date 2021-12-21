package com.example.backknocknumber;

import android.view.animation.Animation;
import android.view.animation.Transformation;

//登録過程用クラス
public class TestAnimation extends Animation {
    private int currentPosition = 0;
    private int endPosition = 0;

    private MainActivity2.MyView myView;

    TestAnimation(MainActivity2.MyView myView, int pos) {
        currentPosition = myView.getPosition();
        endPosition = pos;
        this.myView = myView;
    }

    @Override
    protected void applyTransformation(
            float interpolatedTime, Transformation transformation) {
        // interpolatedTime: 0.f -> 1.0f
        int pp = (int)((endPosition-currentPosition)*interpolatedTime);

        // 矩形のY軸位置をセット
        myView.setPosition(pp);
        myView.requestLayout();
    }
}
