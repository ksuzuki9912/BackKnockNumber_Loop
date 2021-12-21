package com.example.backknocknumber;

import android.app.Application;

public class Common extends Application {  //グローバル変数共有クラス

    public int yv1;
    public int yv2;
    public int count1; //繰り返しカウント用関数
    public int count2;

    public int An0;
    public int An1;
    public int An2;
    public int An3;
    public int Af0;
    public int Af1;
    public int Af2;
    public int Af3;
    public int Bn0;
    public int Bn1;
    public int Bn2;
    public int Bn3;
    public int Bf0;
    public int Bf1;
    public int Bf2;
    public int Bf3;

    public void initA(){  //登録に(MainActivity2で)使用する値を初期化
        yv1 = 0;
        count1 = 0;
        An0 = 0;
        An1 = 0;
        An2 = 0;
        An3 = 0;
        Af0 = 0;
        Af1 = 0;
        Af2 = 0;
        Af3 = 0;
    }
    public void initB(){  //認証に(MainActivity3で)使用する値を初期化
        yv2 = 0;
        count2 = 0;
        Bn0 = 0;
        Bn1 = 0;
        Bn2 = 0;
        Bn3 = 0;
        Bf0 = 0;
        Bf1 = 0;
        Bf2 = 0;
        Bf3 = 0;
    }

}
