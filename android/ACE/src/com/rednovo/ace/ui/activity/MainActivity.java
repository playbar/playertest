package com.rednovo.ace.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.common.UpdateAttacher;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.ui.activity.live.CreateLiveActivity;
import com.rednovo.ace.ui.fragment.HallFragment;
import com.rednovo.ace.ui.fragment.MyFragment;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.ace.view.dialog.certifyNoticeDialog;
import com.rednovo.ace.widget.tabbar.NavigationBar;
import com.rednovo.ace.widget.tabbar.NavigationBar.TabBarClickListener;
import com.rednovo.libs.common.SharedPreferenceKey;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StorageUtils;
import com.rednovo.libs.common.Utils;
import com.rednovo.libs.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements TabBarClickListener, OnClickListener {
    private static final String LOG_TAG = "MainActivity";
    private NavigationBar mNavigationBar;
    private static final int TAB_HALL_INDEX = 0;
    private static final int TAB_MY_INDEX = 1;
    private HallFragment mHallFragment;
    private MyFragment myFragment;
    private Fragment[] fragments;
    private ImageView imgRoom;
    private long exitTime = 0;


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.rednovo.ace.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;
    private SimpleLoginDialog simpleLoginDialog;
    private certifyNoticeDialog certifyDialog;
    private UpdateAttacher updateAttacher;
    private AMapLocationClientOption locationOption;
    private AMapLocationClient locationClient;
   // private HttpBase requserApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        getLocation();
        registerMessageReceiver();

        //检查更新
        updateAttacher = new UpdateAttacher(this);
        if (updateAttacher.checkUpdateVer() && !updateAttacher.refuseUpdate()) {
            updateAttacher.showUpdateDialog();
        }
    }


    private void initView() {
        initFragment();
        switchFragment(TAB_HALL_INDEX);
        imgRoom = (ImageView) findViewById(R.id.img_room);
        mNavigationBar = (NavigationBar) findViewById(R.id.tb_navigationBar);
        mNavigationBar.setNavigationBarClickListener(this);
        mNavigationBar.selectFristTab();
        imgRoom.setOnClickListener(this);

    }

    private void initData() {

    }

    private void initFragment() {
        mHallFragment = (HallFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_hall);
        myFragment = (MyFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_my);
        fragments = new Fragment[]{mHallFragment, myFragment};

    }

    private void switchFragment(int which) {
        getSupportFragmentManager().beginTransaction().hide(fragments[0]).hide(fragments[1]).show(fragments[which]).commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_room:
                createRoom();
                break;
            default:
                break;
        }
    }


    private void createRoom() {
        if (UserInfoUtils.isAlreadyLogin()) {
//            if (CacheUserInfoUtils.isLiveAuthentication()) {
//                if (!NetWorkUtil.checkNetwork())
//                    return;
//                ShowUtils.showCreateRoomLoading(this, true, onCancelListener);
//                requserApi = ReqUserApi.certify(this, UserInfoUtils.getUserInfo().getUserId(), new RequestCallback<CertifyResult>() {
//                    @Override
//                    public void onRequestSuccess(CertifyResult object) {
//                        ShowUtils.dimossCreateRoomLoaing();
//                        if (object != null && object.getCertify() != null) {
//                            String certifyUrl = object.getCertifyUrl();
//                            certifyState(object.getCertify(), certifyUrl);
//                        }
//                    }
//
//                    @Override
//                    public void onRequestFailure(CertifyResult error) {
//                        ShowUtils.showToast(R.string.network_exception);
//                    }
//                });
//            } else {
            redirect(CreateLiveActivity.class);
            overridePendingTransition(R.anim.trans_anim_in, R.anim.trans_anim_no);
            //           }
        } else {
            if (simpleLoginDialog == null) {
                simpleLoginDialog = new SimpleLoginDialog(this);
            }
            simpleLoginDialog.show();
        }
    }

    /**
     * 实名认证提醒
     *
     * @param type :0审核中 1 审核成功 2审核失败;"":尚未认证
     */
    private void certifyState(String type, String certifyUrl) {
        if ("1".equals(type)) {
            redirect(CreateLiveActivity.class);
            overridePendingTransition(R.anim.trans_anim_in, R.anim.trans_anim_no);
        } else {
            if (certifyDialog == null) {
                certifyDialog = new certifyNoticeDialog(this, 0);
            }
            certifyDialog.setCertifyUrl(certifyUrl);
            certifyDialog.setType(type);
            certifyDialog.show();
        }
    }

    @Override
    public void onTabBarClickListener(int itemId) {
        switch (itemId) {
            case TAB_HALL_INDEX:
                switchFragment(itemId);
                break;
            case TAB_MY_INDEX:
                switchFragment(itemId);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ShowUtils.showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                AceApplication.getApplication().appExit();


            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        LiveInfoUtils.putIsShow(false);
        isForeground = true;
        updateAttacher.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        updateAttacher.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        updateAttacher.onDestory();
        super.onDestroy();
        if (simpleLoginDialog != null) {
            simpleLoginDialog = null;
        }
    }


    private void getLocation() {
        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置为单次定位
        locationOption.setOnceLocation(true);
        // 设置定位监听
        locationClient.setLocationListener(mAMapLocationListener);
        // 设置是否需要显示地址信息(是否逆地理编码)
        locationOption.setNeedAddress(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                String locationInfo = "";
                if (!TextUtils.isEmpty(aMapLocation.getCity())) {
                    locationInfo = aMapLocation.getCity();
                }
                StorageUtils.getSharedPreferences().edit().putString(SharedPreferenceKey.LOCATION, locationInfo).apply();
            }
            locationClient.stopLocation();
            if (locationClient != null) {
                locationClient.onDestroy();
                locationClient = null;
                locationOption = null;
            }
        }
    };

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    private DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          //  requserApi.cancelReq();
        }
    };


    /**
     * 极光推送的自定义消息接收，暂时不用
     */
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!Utils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

}
