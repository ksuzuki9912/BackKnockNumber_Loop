package com.example.backknocknumber;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;


import android.content.pm.ActivityInfo;//縦画面に固定する為のimport
import android.graphics.Color;
import android.os.Bundle;

import android.hardware.Sensor;//Sensorクラスのハードウェアを利用するためのimport
import android.hardware.SensorEvent;//SensorEventクラスのハードウェアを利用するためのimport,onSensorChangedメソッドの引数に持たせる
import android.hardware.SensorEventListener;//implements SensorEventListenerと継承させるためのimport
import android.hardware.SensorManager;//SensorManagerクラスのハードウェア（センサ類）を利用するためのimport
import android.os.Handler;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;//TextViewクラスのウィジェットを利用する為のimport
import android.view.View;//イベントの発生源をViewオブジェクトに渡すためのimport
import android.widget.Toast;//Toast表示を使えるようにする


//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.charts.LineChart;//LineChartを利用できるようにする
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//ファイル操作用
import android.content.Context;

import java.util.Arrays;//ソート用
import java.io.BufferedReader;
import java.io.File;//ファイルの存在を確認、削除するため
import java.io.FileInputStream;//ファイルを開く為の変数の宣言に必要
import java.io.FileOutputStream;//ファイルを作る為の変数の宣言に必要

import java.io.InputStreamReader;

//メイン画面のクラス
public class MainActivity extends AppCompatActivity {

    private Button buttonA,buttonB;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonA = findViewById(R.id.buttonA);


        // lambda式
        buttonA.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), MainActivity2.class);
            startActivity(intent);
        });

        buttonB = findViewById(R.id.buttonB);


        // lambda式
        buttonB.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), MainActivity3.class);
            startActivity(intent);
        });
    }
}




/*
エミュレーターのサイズ
1080*1918pxl
xc=540
yc=959
xc2=270
xc3=810
yc2=479.5
yc3=1438.5
yc4=239.75
 */
