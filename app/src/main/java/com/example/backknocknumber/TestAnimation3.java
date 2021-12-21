package com.example.backknocknumber;

import android.view.animation.Animation;
import android.view.animation.Transformation;

//認証過程用クラス
public class TestAnimation3 extends Animation {
    private int currentPosition = 0;
    private int endPosition = 0;

    private MainActivity3.MyView3 myView3;

    TestAnimation3(MainActivity3.MyView3 myView3, int pos) {
        currentPosition = myView3.getPosition3();
        endPosition = pos;
        this.myView3 = myView3;
    }

    @Override
    protected void applyTransformation(
            float interpolatedTime, Transformation transformation) {
        // interpolatedTime: 0.f -> 1.0f
        int pp = (int)((endPosition-currentPosition)*interpolatedTime);

        // 矩形のY軸位置をセット
        myView3.setPosition3(pp);
        myView3.requestLayout();
    }
}