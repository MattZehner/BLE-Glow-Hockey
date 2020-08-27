package com.example.bleclient;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;
//import java.io.PrintStream;
//import java.util.Random;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.ref.WeakReference;

public class GameActivity extends Activity {

    /********************************** Variables ***************************************/

    // constants
    final private static String TAG = "Game Page";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    final private static int MSG_WHAT = 170411;
    final private static int TIMER_PERIOD = 16;     //ms
    public static String UUID_S_GH_COMMAND        = "00030700-0000-1000-8000-00805f9b0131";
    public static String UUID_S_GH_POSITION       = "00030710-0000-1000-8000-00805f9b0131";
    public static String UUID_C_COMMAND_N         = "00030701-0000-1000-8000-00805f9b0131";
    public static String UUID_C_COMMAND_W         = "00030702-0000-1000-8000-00805f9b0131";
    public static String UUID_C_PLAYER_POSITION_N = "00030711-0000-1000-8000-00805f9b0131";
    public static String UUID_C_OPPOSITE_POSITION = "00030713-0000-1000-8000-00805f9b0131";
    public static String UUID_C_BALL_POSITION     = "00030713-0000-1000-8000-00805f9b0131";
    public static String UUID_C_PLAYER_POSITION_W = "00030714-0000-1000-8000-00805f9b0131";


    // custom components
    private Timer timer = new Timer();
    private TimerTask timerTask = getNewTimerTask();
    private myHandler handler = null;
    private GameView gameView = null;

    // BLE components
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattService mGHPositionService;
    private BluetoothGattService mGHCommandService;
    private BluetoothGattCharacteristic mGH_COMMAND_N_Characteristic;
    private BluetoothGattCharacteristic mGH_COMMAND_W_Characteristic;
    private BluetoothGattCharacteristic mGH_Player_Position_N_Characteristic;
    private BluetoothGattCharacteristic mGH_Opposite_Position_Characteristic;
    private BluetoothGattCharacteristic mGH_Ball_Position_Characteristic;
    private BluetoothGattCharacteristic mGH_Player_Position_W_Characteristic;
    private boolean mConnected = false;

    // game states
    private boolean isOver = false;
    private boolean isStart = false;
    private int scoreA = 0;
    private int scoreB = 0;

    // player & Ball parameters
    private int playerCircleAPositionX;
    private int playerCircleAPositionY;
    private int ballPositionX;
    private int ballPositionY;

    // canvas const parameters
    final private static int STD_SCREEN_WIDTH = 1080;
    final private static int STD_SCREEN_HEIGHT = 1920;
    final private static double PADDING_LEFT_RATE = 0.04;
    final private static double PADDING_RIGHT_RATE = 0.04;
    final private static double PADDING_TOP_RATE = 0.04;
    final private static double PADDING_BOTTOM_RATE = 0.04;
    final private static double BALL_SIZE_RATIO = 0.05;
    final private static double BALL_INITIAL_RATE_X = 0.5;
    final private static double BALL_INITIAL_RATE_Y = 0.5;
    final private static double DOOR_LEFT_RATE = 0.35;
    final private static double DOOR_RIGHT_RATE = 0.65;
    final private static double playerCircleSize_RATE = 0.1;
    final private static double playerInnerCircleSize_RATE = 0.072;
    final private static double PLAYER_A_INITIAL_RATE_X = 0.5;
    final private static double PLAYER_A_INITIAL_RATE_Y = 0.8;


    // canvas parameters
    private int tableWidth;
    private int tableHeight;
    private int gamePaddingLeft;
    private int gamePaddingRight;
    private int gamePaddingTop;
    private int gamePaddingBottom;
    private int doorSideLeft;
    private int doorSideRight;
    private int playerCircleSize;
    private int playerInnerCircleSize;
    private int ballSize;
    private Rect fullZone;
    private Rect gameZone;      // 游戏区域
    private Rect doorZoneB;     //对方门
    private Rect doorZoneA;     //己方门


    /*  Unused parameters
    private int playerCircleAPositionLastX;
    private int playerCircleAPositionLastY;
    private int playerCircleASpeedX;
    private int playerCircleASpeedY;
    private int ballSpeedY;
    private int ballSpeedX;

    // bounce effect const parameters
    final private static int BALL_MASS = 20;
    final private static int PLAYER_CIRCLE_MASS = 20;
    final private static double BALL_SPEED_MAX_RATE = 1.3;
    final private static double BALL_SPEED_MIN_RATE = 0.25;
    final private static double BOUNCE_DECAY_RATE = 0.8;
    final private static double BOUNCE_ACCELERATE_RATE = 0.4;
    */

    /********************************** OnCreate ***********************************/


    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "GamePage Create");

        // ???
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //本地组件设置
        gameView = new GameView(this);          //游戏视图实例化
        setContentView(gameView);                       //设置Activity的视图为游戏视图
        handler = new myHandler(this);                 //handler实例化
        gameView.setOnTouchListener(listener);          //设置触摸事件监听器

        //蓝牙服务设置

        // 获取设备名称、地址
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // 绑定蓝牙服务
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);// Get Services



        // UI 初始化
        this.UIInitialize();

        //定时任务开始
        timer.schedule(timerTask , 0, TIMER_PERIOD);

    }



    /****************************** BLE Components *****************************/


    /**
     * service生命周期管理
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            //get Bluetooth Le service instance
            Log.d(TAG, "Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            // Automatically connects to the device
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service Disconnected");
            mBluetoothLeService = null;
        }
    };


    @Override
    protected void onResume(){
        Log.d(TAG, "GamePage Resume");
        super.onResume();
        // 注册广播接收器
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    /**
     * 用于GATT广播接收的intent过滤器
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    /**
     * GATT广播接收器
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // GATT connected
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "GATT_CONNECTED");
                mConnected = true;
            }

            // GATT disconnect
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "GATT_DISCONNECTED");
                mConnected = false;
                invalidateOptionsMenu();
            }

            // GATT service discovered
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "GATT_SERVICES_DISCOVERED");

                // get GATT services
                List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
                for (BluetoothGattService gattService : gattServices) {
                    String uuid = gattService.getUuid().toString();
                    Log.d(TAG, "Service found: "+uuid);
                    if(uuid.equals(UUID_S_GH_POSITION)){
                        mGHPositionService = gattService;
                        Log.d(TAG, "GH Position Service found.");
                    }
                    if(uuid.equals(UUID_S_GH_COMMAND)){
                        mGHCommandService = gattService;
                        Log.d(TAG, "GH Command Service found.");
                    }
                }

                // Get Characteristics
                if(mGHCommandService != null){
                    List<BluetoothGattCharacteristic> GHCommandCharacteristics =
                            mGHCommandService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : GHCommandCharacteristics) {
                        if(gattCharacteristic.getUuid().toString().equals(UUID_C_COMMAND_N)){
                            mGH_COMMAND_N_Characteristic = gattCharacteristic;
                        }
                        else if(gattCharacteristic.getUuid().toString().equals(UUID_C_COMMAND_W)){
                            mGH_COMMAND_W_Characteristic = gattCharacteristic;
                        }
                    }
                }
                else {
                    Log.d(TAG, "GH Command Service is null!");
                }

                if(mGHPositionService != null){
                    List<BluetoothGattCharacteristic> GHPositionCharacteristics =
                            mGHPositionService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : GHPositionCharacteristics) {
                        if(gattCharacteristic.getUuid().toString().equals(UUID_C_PLAYER_POSITION_N)){
                            mGH_Player_Position_N_Characteristic = gattCharacteristic;
                            // set notification enable True
                            mBluetoothLeService.setCharacteristicNotification(
                                    mGH_Player_Position_N_Characteristic, true);
                        }
                        else if(gattCharacteristic.getUuid().toString().equals(UUID_C_OPPOSITE_POSITION)){
                            mGH_Opposite_Position_Characteristic = gattCharacteristic;
                        }
                        else if(gattCharacteristic.getUuid().toString().equals(UUID_C_BALL_POSITION)){
                            mGH_Ball_Position_Characteristic = gattCharacteristic;
                        }
                        else if(gattCharacteristic.getUuid().toString().equals(UUID_C_PLAYER_POSITION_W)){
                            mGH_Player_Position_W_Characteristic = gattCharacteristic;
                        }
                    }
                }
                else {
                    Log.d(TAG, "GH Position Service is null!");
                }
            }

            // data available
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                int position = Integer.valueOf(data);
                int x = position & 0x0fff;
                int y = (position >> 12) & 0x0fff;
                Log.d(TAG, String.format("DATA_AVAILABLE : (%d, %d)", x, y));

            }
        }
    };


    /******************************* UI Managements ******************************/


    /**
     * UI初始化
     */
    private void UIInitialize(){
        //获取屏幕参数
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        Log.d(TAG, String.format("Width: %d, Height: %d", metrics.widthPixels, metrics.heightPixels));

        tableWidth = metrics.widthPixels;
        tableHeight = (int) (metrics.heightPixels * 0.9);
        gamePaddingLeft = (int) (tableWidth * PADDING_LEFT_RATE);
        gamePaddingRight = (int) (tableWidth * PADDING_RIGHT_RATE);
        gamePaddingTop = (int) (tableWidth * PADDING_TOP_RATE);
        gamePaddingBottom = (int) (tableWidth * PADDING_BOTTOM_RATE);
        doorSideLeft = (int) (tableWidth * DOOR_LEFT_RATE);
        doorSideRight = (int) (tableWidth * DOOR_RIGHT_RATE);

        ballSize = (int) (tableWidth * BALL_SIZE_RATIO);
        ballPositionX = (int) (tableWidth * BALL_INITIAL_RATE_X);
        ballPositionY = (int) (tableHeight * BALL_INITIAL_RATE_Y);
        //ballSpeedX = 0;
        //ballSpeedY = 0;

        playerCircleSize = (int) (tableWidth * playerCircleSize_RATE);
        playerInnerCircleSize = (int) (tableWidth * playerInnerCircleSize_RATE);
        playerCircleAPositionX = (int) (tableWidth * PLAYER_A_INITIAL_RATE_X);
        playerCircleAPositionY = (int) (tableHeight * PLAYER_A_INITIAL_RATE_Y);
        //playerCircleASpeedX = 0;
        //playerCircleASpeedY = 0;

        scoreA = scoreB = 0;

        fullZone = new Rect(0,0, tableWidth,tableHeight);

        gameZone = new Rect(
                gamePaddingLeft,
                gamePaddingTop,
                tableWidth - gamePaddingRight,
                tableHeight - gamePaddingBottom
        );

        doorZoneB = new Rect(
                doorSideLeft,
                0,
                doorSideRight,
                gamePaddingTop
        );

        doorZoneA = new Rect(
                doorSideLeft,
                tableHeight - gamePaddingBottom,
                doorSideRight,
                tableHeight
        );

    }



    /**
     * 触控事件监听
     */
    final private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(!isOver) {
                float x = event.getX();
                float y = event.getY();
                /**
                 * player A position
                 */
                if (x - gamePaddingLeft < playerCircleSize) {
                    playerCircleAPositionX = gamePaddingLeft + playerCircleSize;
                } else if (x + gamePaddingRight > tableWidth - playerCircleSize) {
                    playerCircleAPositionX = tableWidth - playerCircleSize - gamePaddingRight;
                } else {
                    playerCircleAPositionX = (int) x;
                }

                if (y < tableHeight * 0.5 + playerCircleSize) {
                    playerCircleAPositionY = (int) (tableHeight * 0.5 + playerCircleSize);
                } else if (y + gamePaddingBottom > tableHeight - playerCircleSize) {
                    playerCircleAPositionY = tableHeight - playerCircleSize - gamePaddingBottom;
                } else {
                    playerCircleAPositionY = (int) y;
                }

                /**
                 * send to server
                 */
                if(mGH_Player_Position_W_Characteristic != null){
                    Calendar calendar = Calendar.getInstance();
                    int sendx = (int)(playerCircleAPositionX * STD_SCREEN_WIDTH / tableWidth);
                    int sendy = (int)(playerCircleAPositionY * STD_SCREEN_HEIGHT / tableHeight);
                    int position = 0;
                    position |= sendx;
                    position |= (sendy << 12);

                    //Log.d(TAG, String.format("sent value:(%d, %d)", sendx, sendy));
                    mGH_Player_Position_W_Characteristic.setValue(position,
                            BluetoothGattCharacteristic.FORMAT_UINT32,0);
                    mBluetoothLeService.writeCharacteristic(mGH_Player_Position_W_Characteristic);
                }
                return true;
            }
            return true;
        }
    };

    /**
     * 视图更新 内置类
     */
    class GameView extends View {
        Paint paint = new Paint();

        //构造函数
        public GameView(Context context) {
            super(context);
            setFocusable(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
        }

        //画游戏帧
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //边框和背景
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.rgb(0, 48, 210));
            canvas.drawRect(fullZone, paint);
            paint.setColor(Color.BLACK);
            canvas.drawRect(gameZone,paint);
            canvas.drawRect(doorZoneB, paint);
            canvas.drawRect(doorZoneA, paint);

            //内部填充
            if (isOver) {
                //游戏结束
                paint.setColor(Color.RED);
                paint.setTextSize(40);
                canvas.drawText("Game Over!", 50, 200, paint);
            } else {
                //游戏进行中
                //画球
                paint.setColor(Color.rgb(240,240,0));
                canvas.drawCircle(ballPositionX, ballPositionY, ballSize, paint);

                //画玩家
                paint.setColor(Color.rgb(124,249,102));
                canvas.drawCircle(
                        playerCircleAPositionX,
                        playerCircleAPositionY,
                        playerCircleSize,
                        paint
                );
                paint.setColor(Color.BLACK);
                canvas.drawCircle(
                        playerCircleAPositionX,
                        playerCircleAPositionY,
                        playerInnerCircleSize,
                        paint
                );

            }
        }
    }


    /******************************* Other Components *******************************/



    /**
     * handler，调动gameView刷新
     */
    final class myHandler extends Handler {
        private WeakReference<GameActivity> mMainActivityWeakReference;

        public myHandler(GameActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_WHAT) {
                gameView.invalidate();
            }
            super.handleMessage(msg);
        }
    }


    /**
     * 定时任务
     */
    private TimerTask getNewTimerTask(){
        return new TimerTask() {

            @Override
            public void run() {
                if(!isOver) {
                    /*

                    // 小球边框碰撞事件
                    if (ballPositionX - gamePaddingLeft <= ballSize) {
                        ballSpeedX = Math.abs(ballSpeedX);
                    } else if (ballPositionX + gamePaddingRight >= tableWidth - ballSize) {
                        ballSpeedX = -Math.abs(ballSpeedX);
                    }

                    if (ballPositionY - gamePaddingTop <= ballSize) {
                        // 己方得分
                        if (ballPositionX > doorSideLeft && ballPositionX < doorSideRight) {
                            scoreA += 1;
                            Log.d(TAG, String.format("You Scored! current score : %d -- %d", scoreA, scoreB));
                            if (scoreA >= 7) {
                                isOver = true;
                            } else {
                                isStart = false;
                                ballPositionX = (int) (tableWidth * BALL_INITIAL_RATE_X);
                                ballPositionY = (int) (tableHeight * BALL_INITIAL_RATE_Y);
                                ballSpeedX = 0;
                                ballSpeedY = 0;
                                playerCircleAPositionX = (int) (tableWidth * PLAYER_A_INITIAL_RATE_X);
                                playerCircleAPositionY = (int) (tableHeight * PLAYER_A_INITIAL_RATE_Y);
                                return;
                            }
                        }
                        ballSpeedY = Math.abs(ballSpeedY);
                    } else if (ballPositionY + gamePaddingBottom >= tableHeight - ballSize) {

                        //对方得分
                        if (ballPositionX > doorSideLeft && ballPositionX < doorSideRight) {
                            scoreB += 1;
                            Log.d(TAG, String.format("Your rival Scored! current score : %d -- %d", scoreA, scoreB));
                            if (scoreB >= 7) {
                                isOver = true;
                            } else {
                                isStart = false;
                                ballPositionX = (int) (tableWidth * BALL_INITIAL_RATE_X);
                                ballPositionY = (int) (tableHeight * BALL_INITIAL_RATE_Y);
                                ballSpeedX = 0;
                                ballSpeedY = 0;
                                playerCircleAPositionX = (int) (tableWidth * PLAYER_A_INITIAL_RATE_X);
                                playerCircleAPositionY = (int) (tableHeight * PLAYER_A_INITIAL_RATE_Y);
                                return;
                            }
                        }
                        ballSpeedY = -Math.abs(ballSpeedY);
                    }


                    // 小球和玩家碰撞事件
                    double ball_playerA_dis = Math.sqrt(
                            Math.pow(ballPositionX - playerCircleAPositionX, 2)
                                    + Math.pow(ballPositionY - playerCircleAPositionY, 2)
                    );
                    if (ball_playerA_dis < ballSize + playerCircleSize) {

                        Log.d(TAG, "Bounce!!");
                        if (!isStart) {
                            isStart = true;
                        }

                        ballSpeedX += (int) (2 * PLAYER_CIRCLE_MASS * (playerCircleASpeedX - ballSpeedX)
                                / (PLAYER_CIRCLE_MASS + BALL_MASS) * BOUNCE_DECAY_RATE);
                        ballSpeedX += (int) (ballPositionX - playerCircleAPositionX) * BOUNCE_ACCELERATE_RATE *
                                (ballSize + playerCircleSize - ball_playerA_dis) / (ballSize + playerCircleSize);

                        ballSpeedY += (int) (2 * PLAYER_CIRCLE_MASS * (playerCircleASpeedY - ballSpeedY)
                                / (PLAYER_CIRCLE_MASS + BALL_MASS) * BOUNCE_DECAY_RATE);
                        ballSpeedY += (int) (ballPositionY - playerCircleAPositionY) * BOUNCE_ACCELERATE_RATE *
                                (ballSize + playerCircleSize - ball_playerA_dis) / (ballSize + playerCircleSize);
                    }

                    // 防止speed过大
                    if (ballSpeedX > ballSize * BALL_SPEED_MAX_RATE) {
                        ballSpeedX = (int) (ballSize * BALL_SPEED_MAX_RATE);
                    } else if (ballSpeedX < -ballSize * BALL_SPEED_MAX_RATE) {
                        ballSpeedX = -(int) (ballSize * BALL_SPEED_MAX_RATE);
                    }

                    if (ballSpeedY > ballSize * BALL_SPEED_MAX_RATE) {
                        ballSpeedY = (int) (ballSize * BALL_SPEED_MAX_RATE);
                    } else if (ballSpeedY < -ballSize * BALL_SPEED_MAX_RATE) {
                        ballSpeedY = -(int) (ballSize * BALL_SPEED_MAX_RATE);
                    }

                    //防止speed过小
                    if (isStart) {
                        if (ballSpeedX >= 0 && ballSpeedX < ballSize * BALL_SPEED_MIN_RATE) {
                            ballSpeedX = (int) (ballSize * BALL_SPEED_MIN_RATE);
                        } else if (ballSpeedX < 0 && ballSpeedX > -ballSize * BALL_SPEED_MIN_RATE) {
                            ballSpeedX = -(int) (ballSize * BALL_SPEED_MIN_RATE);
                        }
                        if (ballSpeedY >= 0 && ballSpeedY < ballSize * BALL_SPEED_MIN_RATE) {
                            ballSpeedY = (int) (ballSize * BALL_SPEED_MIN_RATE);
                        } else if (ballSpeedY < 0 && ballSpeedY > -ballSize * BALL_SPEED_MIN_RATE) {
                            ballSpeedY = -(int) (ballSize * BALL_SPEED_MIN_RATE);
                        }
                    }

                    ballPositionY += ballSpeedY;
                    ballPositionX += ballSpeedX;

                     */

                    handler.sendEmptyMessage(MSG_WHAT);        //刷新视图
                }
            }
        };
    }


    /**
     * 异常处理
     */

    @Override
    protected void onStop(){
        timerTask.cancel();
        timer.cancel();
        Log.d(TAG, "GamePage Stop");
        super.onStop();

    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "GamePage Restart");
        timer = new Timer();
        timerTask = getNewTimerTask();
        timer.schedule(timerTask, 0, TIMER_PERIOD);
        super.onRestart();
    }


    @Override
    protected void onPause(){
        Log.d(TAG, "GamePage Pause");
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "GamePage Destroy");
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


}