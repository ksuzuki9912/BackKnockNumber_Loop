package com.example.backknocknumber;

import android.view.animation.Animation;
import android.view.animation.Transformation;

//認証結果表示用クラス
public class TestAnimation4 extends Animation {
    private int currentPosition = 0;
    private int endPosition = 0;

    private MainActivity3.MyView4 myView4;

    TestAnimation4(MainActivity3.MyView4 myView4, int pos) {
        currentPosition = myView4.getPosition4();
        endPosition = pos;
        this.myView4 = myView4;
    }

    @Override
    protected void applyTransformation(
            float interpolatedTime, Transformation transformation) {
        // interpolatedTime: 0.f -> 1.0f
        int pp = (int)((endPosition-currentPosition)*interpolatedTime);

        // 矩形のY軸位置をセット
        myView4.setPosition4(pp);
        myView4.requestLayout();
    }
}
