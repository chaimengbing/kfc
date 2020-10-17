package com.auw.kfc.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auw.kfc.R;
import com.auw.kfc.constant.Constant;
import com.auw.kfc.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 扫描二维码结果展示页面
 * 包括：
 * 1、餐品还在制作
 * 2、柜中没有可取的餐品
 * 3、机器/系统故障
 * 4、未能识别当前二维码
 * 5、取餐码已经失效（已经取过餐了）
 */
public class ScanResultActivity extends BaseActivity {

    private static final String TAG = ScanResultActivity.class.getName();
    private Button rightButton;
    private Button leftButton;
    private ImageView scanResultImageView;
    private TextView resultTipsTextView;
    private TextView resultContentTextView;
    private TextView backHomeTextView;
    private TextView scanFailTextView;
    private LinearLayout scanSuccessLayout;
    private LinearLayout scanFailedLayout;
    private LinearLayout scanFailed4Layout;

    private CountDownHandler countDownHandler;
    private String result;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_scan_result );
        initView();
        initData( getIntent() );

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );
        initData( intent );
    }

    private void initData(Intent intent) {
        countDownHandler = new CountDownHandler( this );
        scanFailedLayout.setVisibility( View.GONE );
        scanSuccessLayout.setVisibility( View.VISIBLE );
        resultContentTextView.setVisibility( View.VISIBLE );
        leftButton.setVisibility( View.GONE );
        backHomeTextView.setVisibility( View.GONE );
        if (intent != null) {
            result = intent.getStringExtra( Constant.SCAN_RESULT );
            String cellNo = "";
            StringBuffer cellSB = new StringBuffer();
            if (result.contains( Constant.CELL_OPEN_SUCCESS )) {
                cellNo = result.replace( Constant.CELL_OPEN_SUCCESS, "" );
                Type type = new TypeToken<List<String>>() {
                }.getType();

                Gson gson = new Gson();
                List<String> cellNumbers = gson.fromJson( cellNo, type );

                if (cellNumbers != null) {
                    for (String s : cellNumbers) {
                        cellSB.append( s ).append( "," );
                    }
                    count = cellNumbers.size();
                }
                result = Constant.CELL_OPEN_SUCCESS;
            }
            if (!TextUtils.isEmpty( result )) {
                if (result.equals( Constant.CELL_OPEN_SUCCESS )) {
                    //格子打开成功
                    scanResultImageView.setImageResource( R.mipmap.icon_cell_open_success );
                    resultTipsTextView.setText( cellSB.toString().substring( 0, cellSB.length() - 1 ) + "号餐柜已开启" );
                    resultContentTextView.setText( "请您及时取出餐品并关闭柜门，谢谢" );
                    leftButton.setVisibility( View.VISIBLE );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                    playSound( count );


                } else if (result.equals( Constant.MAKEING_MEALS )) {
                    //餐品还在制作
                    scanResultImageView.setImageResource( R.mipmap.icon_no_order );
                    resultTipsTextView.setText( "您的餐品还在制作中" );
                    resultContentTextView.setText( "请稍等一会，备餐情况请查看叫号屏" );
                    leftButton.setVisibility( View.VISIBLE );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                } else if (result.equals( Constant.NO_MEALS )) {
                    //没有餐品
                    scanResultImageView.setImageResource( R.mipmap.icon_no_meals );
                    resultTipsTextView.setText( "取餐柜暂时没有可取餐品" );
                    resultContentTextView.setText( "您可以先进行自助点餐" );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                } else if (result.equals( Constant.HARD_ERROR )) {
                    //机器/系统故障
                    scanResultImageView.setImageResource( R.mipmap.icon_breakdown );
                    resultTipsTextView.setText( "机器/系统故障" );
                    resultContentTextView.setText( "请及时与餐厅工作人员联系" );

                } else if (result.equals( Constant.QR_CODE_ERROR )) {
                    //未能识别当前二维码
                    scanResultImageView.setImageResource( R.mipmap.icon_qr_code_error );
                    resultTipsTextView.setText( "未能识别当前二维码" );
                    resultContentTextView.setText( "取餐码请在手机订单详情页或收银条上查找并使用" );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                } else if (result.equals( Constant.ALREADY_TAKE_MEALS )) {
                    //取餐码已经失效（已经取过餐了）
                    scanResultImageView.setImageResource( R.mipmap.icon_already_take_meals );
                    resultTipsTextView.setText( "取餐码已经失效" );
                    resultContentTextView.setText( "您已经取过餐了" );
                    rightButton.setBackgroundResource( R.drawable.kfc_red_aleady_use_selector );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                } else if (result.equals( Constant.CELL_FULL )) {
                    //取餐码已经失效（已经取过餐了）
                    scanResultImageView.setImageResource( R.mipmap.icon_cell_full );
                    resultTipsTextView.setText( "取餐柜已满，请到柜台取餐" );
                    resultContentTextView.setVisibility( View.GONE);
                    rightButton.setBackgroundResource( R.drawable.kfc_red_aleady_use_selector );

                    Message message = new Message();
                    message.obj = 4000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "5S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );

                } else if (result.equals( Constant.SCAN_FAILED )) {
                    //扫码失败
                    scanSuccessLayout.setVisibility( View.GONE );
                    scanFailedLayout.setVisibility( View.VISIBLE );
                    scanFailTextView.setText( getString( R.string.qrcord_Failure_content1 ) );
                    countDownHandler = new CountDownHandler( this );


                    Message message = new Message();
                    message.obj = 59000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "59S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );
                } else if (result.equals( Constant.SCAN_MANY_FAILED )) {
                    //扫码失败
                    scanSuccessLayout.setVisibility( View.GONE );
                    scanFailedLayout.setVisibility( View.VISIBLE );
                    scanFailed4Layout.setVisibility( View.VISIBLE );
                    scanFailTextView.setText( getString( R.string.qrcord_Failure_content2 ) );

                    countDownHandler = new CountDownHandler( this );


                    Message message = new Message();
                    message.obj = 59000;
                    countDownHandler.sendMessageDelayed( message, 1000 );
                    backHomeTextView.setText( "59S返回首页" );
                    backHomeTextView.setVisibility( View.VISIBLE );
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    private void playSound(final int loopCount) {
        SoundPool soundPool;
        //实例化SoundPool

        //sdk版本21是SoundPool 的一个分水岭
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams( 1 );
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType( AudioManager.STREAM_MUSIC );
            //加载一个AudioAttributes
            builder.setAudioAttributes( attrBuilder.build() );
            soundPool = builder.build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            soundPool = new SoundPool( 1, AudioManager.STREAM_MUSIC, 0 );
        }

        //可以通过四种途径来记载一个音频资源：
        //1.通过一个AssetFileDescriptor对象
        //int load(AssetFileDescriptor afd, int priority)
        //2.通过一个资源ID
        //int load(Context context, int resId, int priority)
        //3.通过指定的路径加载
        //int load(String path, int priority)
        //4.通过FileDescriptor加载
        //int load(FileDescriptor fd, long offset, long length, int priority)
        //声音ID 加载音频资源,这里用的是第二种，第三个参数为priority，声音的优先级*API中指出，priority参数目前没有效果，建议设置为1。
        final int voiceId = soundPool.load( getApplicationContext(), R.raw.open_sound_music, 1 );
        //异步需要等待加载完成，音频才能播放成功
        soundPool.setOnLoadCompleteListener( new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    //第一个参数soundID
                    //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
                    //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
                    //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
                    //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
                    //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
                    soundPool.play( voiceId, 1, 1, 1, loopCount - 1, (float) 1.5 );
                }
            }
        } );
    }

    private void initView() {
        rightButton = findViewById( R.id.bottom_right_button );
        leftButton = findViewById( R.id.bottom_left_button );
        scanResultImageView = findViewById( R.id.scan_result_imageview );
        resultTipsTextView = findViewById( R.id.result_tips_textview );
        resultContentTextView = findViewById( R.id.result_content_textview );
        backHomeTextView = findViewById( R.id.back_home_textview );
        scanSuccessLayout = findViewById( R.id.scan_success_layout );
        scanFailedLayout = findViewById( R.id.scan_failed_layout );
        scanFailTextView = findViewById( R.id.scan_fail_textview );
        scanFailed4Layout = findViewById( R.id.scan_fail_4_layout );

        rightButton.setOnClickListener( view -> startActivity( new Intent( getApplicationContext(), HomeActivity.class ) ) );
        leftButton.setOnClickListener( view -> startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) ) );
    }

    public static class CountDownHandler extends Handler {

        public final WeakReference<ScanResultActivity> weakReference;

        public CountDownHandler(ScanResultActivity activity) {
            weakReference = new WeakReference<>( activity );
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            ScanResultActivity activity = weakReference.get();
            int value = (int) msg.obj;
            String result = "";
            if (activity != null) {
                result = activity.getResult();
            }
            if (!TextUtils.isEmpty( result )) {
                if (result.equals( Constant.SCAN_FAILED )) {
                    activity.getScanFail().setText( activity.getString( R.string.qrcord_Failure_content1 ) + "  " + String.valueOf( value / 1000 ) + "S" );
                } else if (result.equals( Constant.SCAN_MANY_FAILED )) {
                    activity.getScanFail().setText( activity.getString( R.string.qrcord_Failure_content2 ) + "  " + String.valueOf( value / 1000 ) + "S" );
                } else {
                    activity.getBackHome().setText( String.valueOf( value / 1000 ) + "S返回首页" );
                }
            } else {
                return;
            }
            msg = Message.obtain();
            msg.obj = value - 1000;
            if (value > 1000) {
                sendMessageDelayed( msg, 1000 );
            } else {
                activity.startActivity( new Intent( activity.getContext(), HomeActivity.class ) );
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d( TAG, "onPause::" );
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages( null );
            countDownHandler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d( TAG, "onDestroy::" );
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages( null );
            countDownHandler = null;
        }
    }

    private TextView getBackHome() {
        return backHomeTextView;
    }

    private TextView getScanFail() {
        return scanFailTextView;
    }

    private String getResult() {
        return result;
    }

    private Context getContext() {
        return getApplicationContext();
    }
}
