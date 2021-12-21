package com.example.backknocknumber;

import android.view.animation.Animation;
import android.view.animation.Transformation;

//登録結果表示用クラス
public class TestAnimation2 extends Animation {
    private int currentPosition = 0;
    private int endPosition = 0;

    private MainActivity2.MyView2 myView2;

    TestAnimation2(MainActivity2.MyView2 myView2, int pos) {
        currentPosition = myView2.getPosition2();
        endPosition = pos;
        this.myView2 = myView2;
    }

    @Override
    protected void applyTransformation(
            float interpolatedTime, Transformation transformation) {
        // interpolatedTime: 0.f -> 1.0f
        int pp = (int)((endPosition-currentPosition)*interpolatedTime);

        // 矩形のY軸位置をセット
        myView2.setPosition2(pp);
        myView2.requestLayout();
    }
}
