package com.example.backknocknumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.Button;
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


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.charts.LineChart;//LineChartを利用できるようにする
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//ファイル操作用
import android.content.Context;

import java.util.Arrays;//ソート用
import java.io.BufferedReader;
import java.io.File;//ファイルの存在を確認、削除するため
import java.io.FileInputStream;//ファイルを開く為の変数の宣言に必要
import java.io.FileOutputStream;//ファイルを作る為の変数の宣言に必要

import java.io.InputStreamReader;


//認証画面のクラス
public class MainActivity3 extends AppCompatActivity implements SensorEventListener {
    private Common common;
    private SensorManager sensorManager;//SensorManager型のsensorManagerを宣言（フィールド変数）
    private TextView textView, rateView, knockView, judge, frequency;//TextView型のtextView、rateView, knockView, judge, frequencyを宣言（フィールド変数）
    private EditText input_knock_number, input_slipPenalty, input_discordPenalty;//EditText型の宣言するノック数、ズレ減点、不一致減点を宣言
    private Button startShake, stopShake, reportShake, reportShake2, cert, startResultSCV, stopResultSCV, parentCertification;//Button型startShakeとstopShakeを宣言（フィールド変数）加速度の計測開始、停止、データa、データb、認証、認証結果と各値の連続記録,停止ボタン,学習完了ボタン
    private LineChart mChart;
    private Switch filterSwitch, dpDataType;
    private int data_num = 1;//取得順
    private int button_flag = 0;//0停止、1測定中
    private int recording = 0;//１a登録中,２a登録終了、３ｂ登録中、４ｂ登録終了、0例外　//実挙動用
    private int rflag = 0;//１a登録中,２a登録終了、３ｂ登録中、４ｂ登録終了、0例外　//判別用
    private int record_result = -1;//-1待機中、0初回ファイル作成と記録、1記録、2ファイルを閉じて初期化

    private String[] labels = new String[]{"Z軸", "矩形波"};//グラフのラベルを指定しておく
    private int[] colors = new int[]{Color.BLUE, Color.RED};//グラフの線の色を指定しておく
    private float[] now_data = new float[2];//ローパス、通常のどちらも使えるようにする代入用
    private boolean lineardata = true;//フィルタがかかっているか否か
    private boolean dpType = false;//DPマッチングにノック波形が使用されているか否か //false=生データ,true=ノック波形


    //ファイル記述関連
    private String filename;//作成するファイル名
    private int nameCount = 0;//一度だけ名前を付ける分岐に入るための変数
    private String output = "";//ファイルの先頭に必ず出力させる文字
    private String strTmp;

    private FileInputStream fi = null;
    private InputStreamReader is = null;
    private BufferedReader br = null;

    //認証結果用ファイル記述
    private String filename_r;//作成するファイル名
    private String output_r = "No.,DP使用波形,ローパスフィルタ,認証許容値,急制動閾値,ズレペナルティ,不一致ペナルティ,Aノック数,Bノック数,一致度,認証結果\n";//ファイルの先頭に必ず出力させる文字
    private int data_num_r = 1;//取得順

    //200要素までをMAXとする約10秒が記録上限
    private int[] an = new int[1000];//代入用のA-nデータ
    private double[] az = new double[1000];//代入用のA-zデータ

    //200要素までをMAXとする約10秒が記録上限
    private int[] bn = new int[1000];//代入用のA-nデータ
    private double[] bz = new double[1000];//代入用のA-zデータ

    //ノックカウント
    private int knockcountA = 0;//結果出力用のAノックカウント
    private int knockcountB = 0;//結果出力用のBノックカウント

    //mChart矩形波生成用
    private float[] last4 = new float[]{0, 0, 0, 0};//（チャート用）最新の値を4つ前まで保持する（前を[0],現在を[1],次を[2], その次を[3]）実際は現在値は3だが、前後比較の為1を現在とする（2つ遅れの波形）
    private float swz = 0;//（チャート用）Z軸リアルタイム矩形波（2つ遅れ）
    private float[] slope = new float[]{0, 0, 0};//（チャート用）最新の傾斜情報を3つまで保持する

    private float sensorZ;//加速度センサ-9.8用
    private float gravity = 0;//重力加速度を含むローパス用
    private float linear_acceleration;//重力加速度を含むローパス用
    final float alpha = 0.6f;//重力加速度を含むローパス用

    private boolean knockResult = false;
    private boolean dpResult = false;

    //ノック後の誤認防止用のフリーズ期間
    private int freezePeriod = 0;
    private int freezePeriodA = 0;
    private int freezePeriodB = 0;

    //矩形波に関する部分//変位点検出（ノック回数検出）からのローカル変数を移植
    private int saz[] = new int[1000];//Aの矩形波
    private int sbz[] = new int[1000];//Bの矩形波

    //DPマッチングの結果（不一致率）
    private double resultdistance = 0;//結果出力用の2つの数列の不一致度

    private double aDatasAve = 0;//aveの平均
    private int[][] aDatasWaveN = new int[5][1000];//A1,2,3,4,5の5つの波形番号保存場所
    private double[][] aDatasWave = new double[5][1000];//A1,2,3,4,5の5つの波形保存場所
    private double[] val = new double[4];//A12,A13,A14,A15の4つの認証許容値保管場所
    private int aDatasNow = 0;//Aデータ5つの内、今いくつ存在しているか（0－4）
    private int dpout = 0;//学習完了ボタンからDPマッチングにとんだ時にDP側から結果を出力しないためのフラグ(1で出力しない)

    //ノックに関する部分値の初期値（アプリ側から変更可）
    private double baseline = 0.8;//どのくらいの強さをノックの急制動と認識するかの闘値 998,1002,1006に設定調整あり
    //DPマッチングで使う各値の初期値（アプリ側から変更可）
    private double knock = 0;//宣言ノック数
    private int slipPenalty = 1;//1つずれた事へのペナルティ
    private int discordPenalty = 5;////不一致へのペナルティ
    private double matchingLine = 400;//許容する不一致度
    //固定
    private double diff = 0.25;//DPで不一致ペナルティの数値一致扱いにする誤差(+-0.5)

    //正規分布関連
    private int coefficient = 2;//μ＋σのσの係数（1=68％ 2=95％ 3=95％） 999,1003,1007に設定調整あり

    public int mountCountz = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        common = (Common) getApplication();
        common.initB();

        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//sensorManagerインスタンスを取得
        setContentView(R.layout.activity_main3);
        //加速度グラフ
        mChart = findViewById(R.id.chart);
        // インスタンス生成
        mChart.setData(new LineData());//グラフデータの用意
        mChart.getDescription().setEnabled(false);//description labelを消す
        mChart.setDrawGridBackground(true);// グラフの背景色
        mChart.getAxisRight().setEnabled(false);// 右側の目盛り

        MyView3 myView3 = new MyView3(this);
        MyView4 myView4 = new MyView4(this);
        setContentView(myView3);

        int endPosition = 6870; //画面の最終到達位置
        TestAnimation3 testAnimation3 = new TestAnimation3(myView3, endPosition);
        testAnimation3.setDuration(10000);
        testAnimation3.setRepeatCount(3);
        myView3.startAnimation(testAnimation3);
        testAnimation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                button_flag = 1;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button_flag = 0;
                System.out.println("アニメーション3が終了しました");
                setContentView(myView4);
                TestAnimation4 testAnimation4 = new TestAnimation4(myView4, 0);
                testAnimation4.setDuration(10000);
                myView4.startAnimation(testAnimation4);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public class MyView3 extends View {
        Paint paint;
        Path path;

        float dp;

        public MyView3(Context context) {
            super(context);
            paint = new Paint();
            path = new Path();

            // スクリーンサイズからdipのようなものを作る
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            dp = getResources().getDisplayMetrics().density;
            Log.d("debug", "fdp=" + dp);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // 背景
            canvas.drawColor(Color.argb(255, 255, 255, 255));


            float x = getWidth();
            float y = getHeight();
            //System.out.println(x);
            //System.out.println(y);
            float xc = x / 2;
            float yc = y / 2;
            float xc2 = xc / 2;
            float xc3 = xc + xc2;
            float yc2 = yc / 2;
            float yc3 = yc + yc2;
            float yc4 = 241;
            float yr = yc3 + 25;

            // 線
            paint.setStrokeWidth(10);
            paint.setColor(Color.argb(255, 0, 0, 0));
            // (x1,y1,x2,y2,paint) 始点の座標(x1,y1), 終点の座標(x2,y2)
            canvas.drawLine(xc2 + 0 * dp, yc - (yc - 1) * dp, xc2 - 0 * dp, yc + (yc - 1) * dp, paint);
            canvas.drawLine(xc3 + 0 * dp, yc - (yc - 1) * dp, xc3 + 0 * dp, yc + (yc - 1) * dp, paint);
            canvas.drawLine(xc - (xc - 1) * dp, yc + yc4 + 0 * dp, xc + (xc - 1) * dp, yc + yc4 + 0 * dp, paint);
            canvas.drawLine(xc - (xc - 1) * dp, yc + yc4 + xc + 0 * dp, xc + (xc - 1) * dp, yc + yc4 + xc + 0 * dp, paint);


            paint.setColor(Color.argb(255, 0, 0, 0));
            paint.setStrokeWidth(10);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            // (x1,y1,r,paint) 中心x1座標, 中心y1座標, r半径
            // canvas.drawCircle(xc - 0*dp, yr+0*dp, xc / 2, paint);
            //canvas.drawCircle(xc - 0*dp, yr-xc+0*dp, xc / 2, paint);
            //canvas.drawCircle(xc - 0*dp, yr-(2*xc)+0*dp, xc / 2, paint);
            //canvas.drawCircle(xc - 0*dp, yr-(3*xc)+0*dp, xc / 2, paint);
            for (int num = 0; num < 10; num++) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawCircle(xc - 0 * dp, common.yv2 - (num * xc) + 0 * dp, xc / 2, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawCircle(xc - 0 * dp, common.yv2 - (num * xc) + 0 * dp, xc / 2, paint);


            }
            for (int num = 0; num < 10; num++) {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(5);
                paint.setTextSize(200);
                paint.setColor(Color.argb(255, 0, 0, 0));

                Integer i = Integer.valueOf(num);
                String str = i.toString();
                canvas.drawText(str, xc - 20 * dp, common.yv2 - (num * xc) + 20 * dp, paint);
            }
            float a = (common.yv2 - 970) / xc;
            int ia = (int) a;
            if (ia >= 0 && ia < 10) {
                //System.out.println(ia);
            }
            //System.out.println((yv));
            if (common.yv2 == 6870) {
                //System.out.println(count);
                common.count2++;
            }
        }

//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    //System.out.println(count);
//                    //System.out.println(yv);
//                    int xc = 540;
//                    int num = (common.yv2 - 970) / xc;
//                    switch (common.count2) {
//                        case 0:
//                            if (common.Bf0 == 0) {
//                                common.Bn0 = num;
//                                System.out.println(common.Bn0);
//                                common.Bf0 = 1;
//                            } else {
//                                System.out.println("既に入力されています。");
//                            }
//                            break;
//                        case 1:
//                            if (common.Bf1 == 0) {
//                                common.Bn1 = num;
//                                System.out.println(common.Bn1);
//                                common.Bf1 = 1;
//                            } else {
//                                System.out.println("既に入力されています。");
//                            }
//                            break;
//                        case 2:
//                            if (common.Bf2 == 0) {
//                                common.Bn2 = num;
//                                System.out.println(common.Bn2);
//                                common.Bf2 = 1;
//                            } else {
//                                System.out.println("既に入力されています。");
//                            }
//                            break;
//                        case 3:
//                            if (common.Bf3 == 0) {
//                                common.Bn3 = num;
//                                System.out.println(common.Bn3);
//                                common.Bf3 = 1;
//                            } else {
//                                System.out.println("既に入力されています。");
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//
//
//                    Integer i = Integer.valueOf(num);
//                    String str = i.toString();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//            }
//            //System.out.println("1ケタ目=" + n0);
//            //System.out.println("2ケタ目=" + n1);
//            //System.out.println("3ケタ目=" + n2);
//            //System.out.println("4ケタ目=" + n3);
//            invalidate();
//            return true;
//        }

        public int getPosition3() {
            return common.yv2;
        }

        public void setPosition3(int pos) {
            common.yv2 = pos;
        }
    }

    public class MyView4 extends View {
        Paint paint;
        Path path;

        float StrokeWidth1 = 20f;
        float StrokeWidth2 = 40f;
        float dp;

        public MyView4(Context context) {
            super(context);
            paint = new Paint();
            path = new Path();

            // スクリーンサイズからdipのようなものを作る
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            dp = getResources().getDisplayMetrics().density;
            Log.d("debug", "fdp=" + dp);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float x = 0;
            float x1 = getWidth();
            float x2 = x1 / 2;
            float y3 = getHeight();
            float y0 = y3 / 4;
            float y1 = y3 / 2;
            float y2 = y0 + y1;
            canvas.drawColor(Color.argb(255, 255, 255, 255));

            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(5);
            paint.setTextSize(100);
            paint.setColor(Color.argb(255, 0, 0, 0));

            Integer i0 = Integer.valueOf(common.An0);
            String str0 = i0.toString();
            canvas.drawText("An1=" + str0, x - 0 * dp, y0 - 150 * dp, paint);
            Integer i1 = Integer.valueOf(common.An1);
            String str1 = i1.toString();
            canvas.drawText("An2=" + str1, x - 0 * dp, y1 - 150 * dp, paint);
            Integer i2 = Integer.valueOf(common.An2);
            String str2 = i2.toString();
            canvas.drawText("An3=" + str2, x - 0 * dp, y2 - 150 * dp, paint);
            Integer i3 = Integer.valueOf(common.An3);
            String str3 = i3.toString();
            canvas.drawText("An4=" + str3, x - 0 * dp, y3 - 150 * dp, paint);

            Integer j0 = Integer.valueOf(common.Bn0);
            String str4 = j0.toString();
            canvas.drawText("Bn1=" + str4, x2 - 0 * dp, y0 - 150 * dp, paint);
            Integer j1 = Integer.valueOf(common.Bn1);
            String str5 = j1.toString();
            canvas.drawText("Bn2=" + str5, x2 - 0 * dp, y1 - 150 * dp, paint);
            Integer j2 = Integer.valueOf(common.Bn2);
            String str6 = j2.toString();
            canvas.drawText("Bn3=" + str6, x2 - 0 * dp, y2 - 150 * dp, paint);
            Integer j3 = Integer.valueOf(common.Bn3);
            String str7 = j3.toString();
            canvas.drawText("Bn4=" + str7, x2 - 0 * dp, y3 - 150 * dp, paint);

            if ((common.An0 == common.Bn0) && (common.An1 == common.Bn1) && (common.An2 == common.Bn2) && (common.An3 == common.Bn3)) {
                paint.setColor(Color.argb(255, 0, 0, 255));
                canvas.drawText("Accept!!", x - 0 * dp, y3 - 0 * dp, paint);
            } else {
                paint.setColor(Color.argb(255, 255, 0, 0));
                canvas.drawText("Reject!!", x - 0 * dp, y3 - 0 * dp, paint);
            }
        }

        public int getPosition4() {
            return common.yv2;
        }

        public void setPosition4(int pos) {
            common.yv2 = pos;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //valueRead();//センサーの値処理が始まる前に各値を設定

        //加速度センサ-9.8用
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {//加速度センサの値をgetした場合

            sensorZ = event.values[2];
            //low-passフィルタのよくわからん式
            gravity = alpha * gravity + (1 - alpha) * sensorZ;
            linear_acceleration = sensorZ - gravity;


            if (button_flag == 1) {//計測開始、初期状態ではフィルタなし

                if (!lineardata) {//フィルタリングオフ//lineardata==false
                    strTmp = "加速度\t\t Z: " + String.format("%.5f", sensorZ);
                } else {//フィルタリングオン
                    strTmp = "加速度(low-pass) Z: " + String.format("%.5f", gravity);
                }
                //textView.setText(strTmp);//strTmpに代入された情報をtextViewで表示//通常通りの検出ならこっち


                chartSquareWave();//mChartグラフの矩形波生成

                LineData data = mChart.getLineData();
                if (data != null) {
                    //z軸を描写する
                    for (int i = 0; i < 2; i++) {//z軸と矩形波

                        ILineDataSet set3 = data.getDataSetByIndex(i);
                        if (set3 == null) {//チャートの各種設定
                            LineDataSet set = new LineDataSet(null, labels[i]);//z軸
                            set.setLineWidth(2.0f);//線の太さ
                            set.setColor(colors[i]);//色指定
                            set.setDrawCircles(false);//各点に点を打たない
                            set.setDrawValues(false);//チャートに各点の値を表示しない
                            set3 = set;
                            data.addDataSet(set3);
                        }

                        //データアップデート　
                        if (i == 0) {//Z軸アップデート
                            now_data[i] = (!lineardata) ? sensorZ : linear_acceleration;//low-passがoffなら生データで更新、onなら重力加速度を引いた値を出力(// これはローパスになってなさそう
                        } else {//矩形波アップデート
                            now_data[i] = swz;
                        }

                        data.addEntry(new Entry(set3.getEntryCount(), now_data[i]), i);
                        data.notifyDataChanged();//データを追加したら必ず呼ぶ

                    }

                    //fileSave();//通常通り検出する場合

                    mChart.notifyDataSetChanged(); // 表示の更新のために変更を通知する
                    mChart.setVisibleXRangeMaximum(50); // 表示の幅Xを決定する
                    mChart.moveViewToX(data.getEntryCount()); // 最新のデータまで表示を移動させる

                }
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Listenerの登録
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION/*TYPE_ACCELEROMETER*/);//加速度センサ-9.8用

        ////加速度センサ-9.8用

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);//最速通知頻度
    }


    //解除するコード
    @Override
    protected void onPause() {
        super.onPause();
        //Listenerを解除
        sensorManager.unregisterListener(this);
    }

    public void chartSquareWave() {//なんかおかしい　認識しないところを認識し、認識すべきところを素通り


        //最新の4つを更新
        last4[0] = last4[1];
        last4[1] = last4[2];
        last4[2] = last4[3];
        last4[3] = (lineardata) ? sensorZ : linear_acceleration;//ローパスフィルタon = 生データ/off = フィルタリングデータ

        //最新の傾斜（落差）3つを更新
        slope[0] = last4[1] - last4[0];
        slope[1] = last4[2] - last4[1];
        slope[2] = last4[3] - last4[2];


        //矩形波リアルタイム生成(実際には1つ遅れ)
        if ((last4[0] < last4[1]) && (last4[1] > last4[2])) {//↗i↘になるときに、
            if ((last4[1] - last4[0] > baseline) && (last4[1] - last4[2] > baseline)) {
                if (freezePeriod == 0) {
                    swz = 1;
                    mountCountz++;
                    freezePeriod = 10;//10ms*10回の間ノックを認識しない
                    inputNumber();
                }
            }
        } else if (slope[0] > baseline && slope[2] < -baseline) {//上下の落差は基準を満たすが台形の場合
            if (freezePeriod == 0) {
                swz = 1;
                mountCountz++;
                freezePeriod = 10;//10ms*10回の間ノックを認識しない
                inputNumber();
            }
        } else if (slope[0] > baseline) {//登りだけ基準を満たしている場合
            if (slope[1] + slope[2] < -baseline) {
                if (freezePeriod == 0) {
                    swz = 1;
                    mountCountz++;
                    freezePeriod = 10;//10ms*10回の間ノックを認識しない
                    inputNumber();
                }
            }
        } else if (slope[2] < -baseline) {//下りだけ基準を満たしている場合
            if (slope[0] + slope[1] > baseline) {
                if (freezePeriod == 0) {
                    swz = 1;
                    mountCountz++;
                    freezePeriod = 10;//10ms*10回の間ノックを認識しない
                    inputNumber();
                }
            }
        } else {//それ以外0
            swz = 0;
        }

        String strTmp2 = "\t　ノック回数：" + mountCountz + "回";
        //frequency.setText(strTmp2);//strTmpに代入された情報をrateViewで表示

        freezePeriod = (freezePeriod == 0) ? 0 : freezePeriod - 1;
    }

    public void inputNumber() {
        int xc = 540;
        int num = (common.yv2 - 970) / xc;
        switch (common.count2) {
            case 0:
                if (common.Bf0 == 0) {
                    common.Bn0 = num;
                    System.out.println(common.Bn0);
                    common.Bf0 = 1;
                } else {
                    System.out.println("既に入力されています。");
                }
                break;
            case 1:
                if (common.Bf1 == 0) {
                    common.Bn1 = num;
                    System.out.println(common.Bn1);
                    common.Bf1 = 1;
                } else {
                    System.out.println("既に入力されています。");
                }
                break;
            case 2:
                if (common.Bf2 == 0) {
                    common.Bn2 = num;
                    System.out.println(common.Bn2);
                    common.Bf2 = 1;
                } else {
                    System.out.println("既に入力されています。");
                }
                break;
            case 3:
                if (common.Bf3 == 0) {
                    common.Bn3 = num;
                    System.out.println(common.Bn3);
                    common.Bf3 = 1;
                } else {
                    System.out.println("既に入力されています。");
                }
                break;
            default:
                break;
        }

    }
}