package com.rednovo.ace.widget.live;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.CacheUserInfoUtils;
import com.rednovo.ace.common.GiftUtils;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.LiveInfoUtils;
import com.rednovo.ace.core.session.SendUtils;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.MsgLog;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.events.ChatMessage;
import com.rednovo.ace.data.events.CommonGiftAnimEvent;
import com.rednovo.ace.data.events.EnterRoomEvent;
import com.rednovo.ace.data.events.KeyBoardEvent;
import com.rednovo.ace.data.events.KickOutEvent;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.data.events.RoomBroadcastEvent;
import com.rednovo.ace.data.events.SendGiftResponse;
import com.rednovo.ace.net.api.ReqRelationApi;
import com.rednovo.ace.net.api.ReqRoomApi;
import com.rednovo.ace.net.api.ReqUserApi;
import com.rednovo.ace.net.parser.AudienceResult;
import com.rednovo.ace.net.parser.BaseResult;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.net.request.RequestCallback;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.ace.view.dialog.SimpleLoginDialog;
import com.rednovo.ace.widget.gift.CommonGiftView;
import com.rednovo.ace.widget.live.gift.GiftDialog;
import com.rednovo.ace.widget.parise.VoteSurface;
import com.rednovo.libs.common.KeyBoardUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.widget.emoji.ExpressionPanel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * 直播间视频上层的view
 */
public class LiveView extends RelativeLayout implements GalleryAdapter.OnItemClickLitener, View.OnClickListener, DialogInterface.OnDismissListener, SimpleUserInfoDialog.OnNoLoginListener, RoomChatAdapter.OnChatItemClickLitener, AbsListView.OnScrollListener {
    private static final int MSG_ENTER_ROOM = 0x05;
    private static final int MSG_FOLLOW_NOTICE = 0x06;
    private static final int FOLLOW_TIME = 60 * 1000;
    private EditText et;
    private ImageView ivEmoji, ivChat, ivGift, ivShare, ivClose, ivCamera, ivWarmClose;
    private TextView tvSend, tvAudience, tvCollect, tvAnchorName, tvEnterRoom, tvWarm, tvTicketNum;
    private CheckBox cbVoice, cbFlicker, cbSkincare;
    private LinearLayout llTop, llWarm, llFollow;
    private RelativeLayout rlInput, rlBtns, rlAnchor, rlVoice, rlFlicker;
    // 横向头像列表
    private RecyclerView hrView;
    // 聊天list
    private ListView listView;
    private RoomChatAdapter roomChatAdapter;
    private List<MsgLog> chatDatas = new ArrayList<MsgLog>();

    private GalleryAdapter mAdapter;
    private List<AudienceResult.MemberListEntity> mDatas = new ArrayList<AudienceResult.MemberListEntity>();
    private ExpressionPanel vEmoji;
    private SimpleDraweeView ivAnchor;
    private GiftDialog giftDialog;
    private SimpleUserInfoDialog infoDialog;
    private ShareDialog shareDialog;

    private boolean isAnchor;
    private boolean isKeyBoardVisible;
    private View giftLayout;
    private VoteSurface vsPariseView;
    private boolean needEmoji;
    private PanelLayout mPanelRoot;
    private OnClickListener onClickListener;
    private CommonGiftView commonGiftView1;
    private CommonGiftView commonGiftView2;
    private boolean isScrolling;
    private SimpleLoginDialog simpleLoginDialog = null;

    private LinkedHashMap<String, List<ReciveGiftInfo>> receiveGiftMsgMap = new LinkedHashMap<String, List<ReciveGiftInfo>>();
    private int count = 0;

    private static MessageHandler mMessageHandler;
    private static long lastEnterTime;
    private boolean isFirstShowGiftDialog = true;
    private long lastClickTime;

    @Override
    public void loginNotice() {
        showLoginDilaog();
    }
    @Override
    public void followNotice() {
        mMessageHandler.removeMessages(MSG_FOLLOW_NOTICE);
        llFollow.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        switch (i) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                isScrolling = false;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                isScrolling = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    private static class MessageHandler extends Handler {

        WeakReference<LiveView> mActivityReference;

        MessageHandler(LiveView view) {
            mActivityReference = new WeakReference<LiveView>(view);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            final LiveView view = mActivityReference.get();
            if (view != null) {
                switch (msg.what) {
                    case MSG_ENTER_ROOM:
                        view.clearEnterRoom();
                        break;
                    case MSG_FOLLOW_NOTICE:
                        view.clearFollowNotice();
                        break;
                    default:
                        break;
                }
            }
        }

    }

    public void clearEnterRoom() {
        tvEnterRoom.setText("");
    }

    public void clearFollowNotice() {
        llFollow.setVisibility(View.GONE);
    }

    public void setIsAnchor(boolean isAnchor) {
        this.isAnchor = isAnchor;
        if (isAnchor) {
            ivGift.setVisibility(View.GONE);
            ivShare.setVisibility(View.GONE);
            rlVoice.setVisibility(View.VISIBLE);
            ivCamera.setVisibility(View.VISIBLE);
            rlFlicker.setVisibility(View.VISIBLE);
            cbSkincare.setVisibility(View.VISIBLE);

            //放大聊天区域
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = (int) getContext().getResources().getDimension(R.dimen.chat_anchor_height);
            tvEnterRoom.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            roomChatAdapter.setTextSize(18);

            RelativeLayout.LayoutParams giftLayoutParams = (LayoutParams) giftLayout.getLayoutParams();
            giftLayoutParams.bottomMargin = ShowUtils.dip2px(getContext(), 50);
            giftLayoutParams.addRule(RelativeLayout.ABOVE, R.id.tv_enter_room);

        } else {
            //不是主播，请求是否已经关注
            requestRelation(LiveInfoUtils.getStartId());
        }

    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.onClickListener = mOnClickListener;
        if (onClickListener != null) {
            cbVoice.setOnClickListener(onClickListener);
            ivCamera.setOnClickListener(onClickListener);
            cbFlicker.setOnClickListener(onClickListener);
            cbSkincare.setOnClickListener(onClickListener);
        }
    }

    public LiveView(Context context) {
        super(context);
        initView(context);
    }

    public LiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_live_view, this);
        mMessageHandler = new MessageHandler(this);
        et = (EditText) findViewById(R.id.et);
        ivChat = (ImageView) findViewById(R.id.iv_chat);
        ivGift = (ImageView) findViewById(R.id.iv_gift);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivEmoji = (ImageView) findViewById(R.id.iv_emoji);
        ivWarmClose = (ImageView) findViewById(R.id.iv_close_warm);
        cbVoice = (CheckBox) findViewById(R.id.cb_voice);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);
        cbFlicker = (CheckBox) findViewById(R.id.cb_flicker);
        cbSkincare = (CheckBox) findViewById(R.id.cb_skincare);
        ivAnchor = (SimpleDraweeView) findViewById(R.id.iv_anchor);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvWarm = (TextView) findViewById(R.id.tv_warm);
        tvSend.setEnabled(false);
        tvAudience = (TextView) findViewById(R.id.tv_audience);
        tvCollect = (TextView) findViewById(R.id.tv_collect);
        tvAnchorName = (TextView) findViewById(R.id.tv_anchorName);
        tvEnterRoom = (TextView) findViewById(R.id.tv_enter_room);
        tvTicketNum = (TextView) findViewById(R.id.tv_ticket_num);
        rlBtns = (RelativeLayout) findViewById(R.id.rl_btns);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llWarm = (LinearLayout) findViewById(R.id.ll_warm);
        llFollow = (LinearLayout) findViewById(R.id.ll_follow);
        rlInput = (RelativeLayout) findViewById(R.id.rl_input);
        rlVoice = (RelativeLayout) findViewById(R.id.rl_voice);
        rlFlicker = (RelativeLayout) findViewById(R.id.rl_flicker);
        rlAnchor = (RelativeLayout) findViewById(R.id.rl_anchor);
        hrView = (RecyclerView) findViewById(R.id.h_recyclerview);
        mPanelRoot = (PanelLayout) findViewById(R.id.panel_root);
        vEmoji = (ExpressionPanel) findViewById(R.id.view_emoji);
        vEmoji.setTextView(et, false, tvSend);
        vEmoji.setMaxLength(50);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hrView.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(getContext(), mDatas);
        hrView.setAdapter(mAdapter);
        mAdapter.setOnItemClickLitener(this);

        listView = (ListView) findViewById(R.id.list_view);
        roomChatAdapter = new RoomChatAdapter(getContext(), 0, chatDatas);
        listView.setAdapter(roomChatAdapter);
        roomChatAdapter.setOnChatItemClickLitener(this);
        listView.setOnTouchListener(new MyOnTouch());
        listView.setOnScrollListener(this);

        ivChat.setOnClickListener(this);
        ivEmoji.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        ivGift.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        rlAnchor.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        et.setOnClickListener(this);
        ivWarmClose.setOnClickListener(this);
        llFollow.setOnClickListener(this);

        giftLayout = findViewById(R.id.ll_gift_layout);
        commonGiftView1 = (CommonGiftView) findViewById(R.id.gift_anim_layout_one);
        commonGiftView2 = (CommonGiftView) findViewById(R.id.gift_anim_layout_two);

        vsPariseView = (VoteSurface) findViewById(R.id.vs_parise_view);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int giftId = 12;
//                int giftCnt = 1;
//                Random random = new Random();
//                for(int i = 0; i < 100; i++) {
//                    ReciveGiftInfo giftInfo = new ReciveGiftInfo(Globle.KEY_EVENT_RECEIVE_GIFT);
//                    giftInfo.setSenderName("送礼" + (4705 + (i % 2)));
//                    giftInfo.setSenderId((4705) + "");
//                    giftId = random.nextInt(2) == 1 ? 17:12;
//                    giftInfo.setGiftId(giftId + "");
//                    giftInfo.setReceiverId("4233");
//                    giftInfo.setReceiverName("接受礼物人");
//                    giftInfo.setGiftCnt(giftCnt++);
//
//                    GiftListResult.GiftListEntity giftEntity = GiftUtils.getGiftById(giftId + ""); // 17完美 8香蕉
//                    if(giftEntity != null) {
//                        giftInfo.setGiftUrl(giftEntity.getPic());
//                        giftInfo.setGiftName(giftEntity.getName());
//
//                        EventBus.getDefault().post(giftInfo);
//                    }else {
//                        ShowUtils.showToast("礼物信息缓存为空");
//                    }
//                    giftInfo.setGiftId("18");
//                    giftInfo.setId(Globle.KEY_EVENT_RECEIVE_SPECIAL_GIFT);
//                    EventBus.getDefault().post(giftInfo);
//                }
//            }
//        }, 2000);
        addTips();
    }

    private void addTips() {
        if (!TextUtils.isEmpty(CacheUserInfoUtils.getShowTips())) {
            MsgLog msgLog = new MsgLog();
            msgLog.msgType = MsgLog.TYPE_TIPS;
            msgLog.msgContent = CacheUserInfoUtils.getShowTips();
            addMsg(msgLog);
        }
    }

    /**
     * 主播信息
     */
    public void initAnChorData() {

        ivAnchor.getHierarchy().setPlaceholderImage(R.drawable.head_online);
        ivAnchor.getHierarchy().setFailureImage(AceApplication.getApplication().getResources().getDrawable(R.drawable.head_offline));
        if (isAnchor) {
            FrescoEngine.setSimpleDraweeView(ivAnchor, UserInfoUtils.getUserInfo().getProfile(), ImageRequest.ImageType.SMALL);
            tvAnchorName.setText(UserInfoUtils.getUserInfo().getNickName());
            tvAudience.setText("0");
            tvCollect.setText("0");
        } else {
            FrescoEngine.setSimpleDraweeView(ivAnchor, LiveInfoUtils.getProFile(), ImageRequest.ImageType.SMALL);
            tvAnchorName.setText(LiveInfoUtils.getNickName());
            tvAudience.setText(TextUtils.isEmpty(LiveInfoUtils.getAudienceCnt()) ? "0" : LiveInfoUtils.getAudienceCnt());
            tvCollect.setText("0");
        }
        getAudience();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        //关闭房间定时刷新观众
//        AlarmUtil.alarm(30, RefreshAudienceReceiver.class);
    }

    @Override
    protected void onDetachedFromWindow() {
//        AlarmUtil.alarm(-1, RefreshAudienceReceiver.class);
        EventBus.getDefault().unregister(this);
        if (mMessageHandler != null) {
            mMessageHandler.removeCallbacksAndMessages(null);
            mMessageHandler = null;
        }
//        GiftDialog.onActivityDestory();
        commonGiftView1.setAnimatorFlag(false);
        commonGiftView2.setAnimatorFlag(false);
        vsPariseView = null;
        super.onDetachedFromWindow();
    }

    @Override
    public void onChatItemClick(MsgLog msgLog) {
        hideKeyBoard();
        int msgType = msgLog.getMsgType();
        if (msgType != MsgLog.TYPE_TIPS && msgType != MsgLog.TYPE_SYSTEM)
            showUserInfoDialog(msgLog.getSendNumber());
    }

    @Override
    public void onLinkClick(String url) {
        if (!TextUtils.isEmpty(url) && !isAnchor) {
            Intent intent = new Intent(getContext(), ACEWebActivity.class);
            intent.putExtra("url", url);
            getContext().startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        AudienceResult.MemberListEntity memberListEntity = mAdapter.getmDatas().get(position);
        showUserInfoDialog(memberListEntity.getUserId());
    }

    /**
     * 个人信息弹窗
     *
     * @param id
     */
    private void showUserInfoDialog(String id) {
        if (multiClick()) return;
        if (!TextUtils.isEmpty(id)) {
            if (id.equals("-1")) {
                return;
            }
//            infoDialog = SimpleUserInfoDialog.getSimpleUserInfoDialog(getContext(), 0);
            if (infoDialog == null) {
                infoDialog = new SimpleUserInfoDialog(getContext(), 0);
                infoDialog.setOnNoLoginListener(this);
                if (isAnchor) {
                    infoDialog.setIsAnchor(isAnchor);
                }
            }
            infoDialog.setUserId(id);
            infoDialog.show();
        }
    }

    /**
     * 防止连续点击
     *
     * @return
     */
    private boolean multiClick() {
        if (System.currentTimeMillis() - lastClickTime < 800) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_chat:
                if (multiClick()) return;
                if (UserInfoUtils.isAlreadyLogin()) {
                    KeyBoardUtils.openKeybord(et, getContext());
                    et.requestFocus();
                } else {
                    showLoginDilaog();
                }
                break;
            case R.id.iv_emoji:
                if (mPanelRoot.getVisibility() == View.VISIBLE) {
                    KeyBoardUtils.openKeybord(et, getContext());
                    // hideKeyBoard();
                } else {
                    needEmoji = true;
                    KeyBoardUtils.closeKeybord(et, getContext());
                    mPanelRoot.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_send:
                String content = et.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    sendMsg(content);
                    et.setText("");
                    // hideKeyBoard();
                }
                break;
            case R.id.iv_gift:
                if (multiClick()) return;
                if (!UserInfoUtils.isAlreadyLogin()) {
                    showLoginDilaog();
                    return;
                }
                if (giftDialog == null) {
                    List<GiftListResult.GiftListEntity> giftList = GiftUtils.getGiftList();
                    if (giftList != null && giftList.size() != 0) {
//                    if (isFirstShowGiftDialog)
//                        GiftDialog.onActivityDestory();
//                    giftDialog = GiftDialog.getGiftDialog(getContext(), 0);
                        if (giftDialog == null) {
                            giftDialog = new GiftDialog(getContext(), 0);
                            giftDialog.setOnDismissListener(this);
                        }
                        giftDialog.show();
                        if (isFirstShowGiftDialog) {
                            giftDialog.setGifts(giftList);
                            tvEnterRoom.setAlpha(0);
                            listView.setAlpha(0);
                            rlBtns.setAlpha(0);
                            isFirstShowGiftDialog = false;
                        }
                    } else {

                    }
                } else {
                    giftDialog.show();
                    tvEnterRoom.setAlpha(0);
                    listView.setAlpha(0);
                    rlBtns.setAlpha(0);
                }
                break;
            case R.id.iv_share:
                if (multiClick()) return;
                shareDialog = ShareDialog.getShareDialog((Activity) getContext(), 0);
                shareDialog.setOnDismissListener(this);
                shareDialog.setShare(isAnchor);
                rlBtns.setAlpha(0);
                shareDialog.show();
                break;
            case R.id.iv_close:
                EventBus.getDefault().post(new BaseEvent(Globle.KEY_ONCLICK_LIVE_FINISH));
                break;
            case R.id.rl_anchor:
                String startId;
                if (isAnchor) {
                    startId = UserInfoUtils.getUserInfo().getUserId();
                } else {
                    startId = LiveInfoUtils.getStartId();
                }
                showUserInfoDialog(startId);
                break;
            case R.id.et:
                KeyBoardUtils.openKeybord(et, getContext());
                break;
            case R.id.iv_close_warm:
                llWarm.setVisibility(View.GONE);
                break;
            case R.id.ll_follow:
                ReqRelationApi.reqSubscibe(getContext(), UserInfoUtils.getUserInfo().getUserId(), LiveInfoUtils.getStartId(), new RequestCallback<BaseResult>() {
                    @Override
                    public void onRequestSuccess(BaseResult object) {
                    }

                    @Override
                    public void onRequestFailure(BaseResult error) {
                    }
                });
                llFollow.setVisibility(View.GONE);
                mMessageHandler.removeMessages(MSG_FOLLOW_NOTICE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isAnchor && !(mPanelRoot.getVisibility() == View.VISIBLE || isKeyBoardVisible)) {
                    vsPariseView.click();

                    String userId = "";
                    if (UserInfoUtils.isAlreadyLogin()) {
                        userId = UserInfoUtils.getUserInfo().getUserId();
                    }
                    SendUtils.sendParise(LiveInfoUtils.getShowId(), userId, "1");
                }
                hideKeyBoard();
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 键盘弹出和收回
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onKeyBoardEvent(KeyBoardEvent event) {
        boolean show = event.isShow();
        isKeyBoardVisible = show;
        if (show) {
            rlBtns.setVisibility(View.GONE);
            rlInput.setVisibility(View.VISIBLE);
            if (llTop.getVisibility() == View.VISIBLE)
                hideTop(true);

        } else {
            if (needEmoji) {
                needEmoji = false;
                return;
            }
            rlInput.setVisibility(View.GONE);
            rlBtns.setVisibility(View.VISIBLE);
            if (llTop.getVisibility() != View.VISIBLE) {
                hideTop(false);
            }

        }
    }

    /**
     * 顶部布局
     *
     * @param hide :true,隐藏;false,显示
     */
    private void hideTop(boolean hide) {
        llTop.clearAnimation();
        if (hide) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_out_from_top);
            llTop.setVisibility(View.GONE);
            llTop.startAnimation(animation);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(giftLayout.getLayoutParams());
            params.bottomMargin = ShowUtils.dip2px(getContext(), 10);
            params.addRule(RelativeLayout.ABOVE, R.id.tv_enter_room);

            giftLayout.setLayoutParams(params);
            giftLayout.requestLayout();

            if (isAnchor) {
                ViewGroup.LayoutParams params1 = listView.getLayoutParams();
                params1.height = (int) getContext().getResources().getDimension(R.dimen.chat_audience_height);
            }
        } else {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_in_from_top);
            llTop.setVisibility(View.VISIBLE);
            llTop.startAnimation(animation);

            if (isAnchor) {
                ViewGroup.LayoutParams params1 = listView.getLayoutParams();
                params1.height = (int) getContext().getResources().getDimension(R.dimen.chat_anchor_height);

                RelativeLayout.LayoutParams params = (LayoutParams) giftLayout.getLayoutParams();
                params.bottomMargin = ShowUtils.dip2px(getContext(), 50);
                params.addRule(RelativeLayout.ABOVE, R.id.tv_enter_room);

                giftLayout.setLayoutParams(params);
                giftLayout.requestLayout();
            } else {
                RelativeLayout.LayoutParams params = (LayoutParams) giftLayout.getLayoutParams();
                params.bottomMargin = ShowUtils.dip2px(getContext(), 160);
                params.addRule(RelativeLayout.ABOVE, R.id.tv_enter_room);
                giftLayout.setLayoutParams(params);
                giftLayout.requestLayout();
            }
        }
    }

    /**
     * 收到socket消息
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onReceiveMsg(BaseEvent msg) {
        switch (msg.id) {
            case Globle.KEY_EVENT_CHAT:
                MsgLog msgLog = ((ChatMessage) msg).mMsgLog;
                addMsg(msgLog);
                break;
            case Globle.KEY_RECEIVE_WARN:
                MsgLog warn = ((ChatMessage) msg).mMsgLog;
                if (!TextUtils.isEmpty(warn.getMsgContent())) {
                    tvWarm.setText(warn.getMsgContent());
                    llWarm.setVisibility(View.VISIBLE);
                }
                break;
            case Globle.KEY_EVENT_ROOM_BROADCAST:
                RoomBroadcastEvent roomBroadcastEvent = (RoomBroadcastEvent) msg;
                if (Integer.parseInt(roomBroadcastEvent.getSupportCnt()) > 0) {
                    vsPariseView.add(Integer.parseInt(roomBroadcastEvent.getSupportCnt()));
                }
                tvAudience.setText(roomBroadcastEvent.getMemberCnt());
                tvCollect.setText(roomBroadcastEvent.getTotalSupportCnt());
                String income = roomBroadcastEvent.getIncome();
                if (!TextUtils.isEmpty(income))
                    tvTicketNum.setText(income);
                break;
            case Globle.KEY_EVENT_RECEIVE_GIFT:
                ReciveGiftInfo receiveGiftInfo = (ReciveGiftInfo) msg;

                synchronized (this) {
                    String mapKey = receiveGiftInfo.getSenderId() + receiveGiftInfo.getGiftId();
                    List<ReciveGiftInfo> temp = receiveGiftMsgMap.get(mapKey);
                    if (temp == null) {
                        List<ReciveGiftInfo> reciveGiftInfoList = new ArrayList<ReciveGiftInfo>(2);
                        reciveGiftInfoList.add(receiveGiftInfo);
                        receiveGiftMsgMap.put(mapKey, reciveGiftInfoList);
                    } else {
                        temp.add(receiveGiftInfo);
                    }
                }

                // 礼物2号跑道空闲，并且当前礼物与礼物1跑道上的(发送人+礼物)不统一，发送执行动画消息
                if (!commonGiftView2.isRunning() && !commonGiftView1.compareTo(receiveGiftInfo.getSenderId() + receiveGiftInfo.getGiftId())) {
                    CommonGiftAnimEvent animFinishEvent = new CommonGiftAnimEvent();
                    animFinishEvent.id = Globle.KEY_EVENT_GIFT_FINISH;
                    animFinishEvent.giftViewTag = 2;

                    EventBus.getDefault().post(animFinishEvent);
                    // 礼物1号跑道空闲，并且当前礼物与礼物2跑道上的(发送人+礼物)不统一，发送执行动画消息
                } else if (!commonGiftView1.isRunning() && !commonGiftView2.compareTo(receiveGiftInfo.getSenderId() + receiveGiftInfo.getGiftId())) {
                    CommonGiftAnimEvent animFinishEvent = new CommonGiftAnimEvent();
                    animFinishEvent.id = Globle.KEY_EVENT_GIFT_FINISH;
                    animFinishEvent.giftViewTag = 1;

                    EventBus.getDefault().post(animFinishEvent);
                    // 礼物1跑道正被占用，当前礼物消息的(发送人+礼物)与跑道上的一致，设置收到新消息标记
                } else if (commonGiftView1.compareTo(receiveGiftInfo.getSenderId() + receiveGiftInfo.getGiftId())) {
                    commonGiftView1.hasReceivedNewMsg(true);
                    // 礼物2跑道正被占用，当前礼物消息的(发送人+礼物)与跑道上的一致，设置收到新消息标记
                } else if (commonGiftView2.compareTo(receiveGiftInfo.getSenderId() + receiveGiftInfo.getGiftId())) {
                    commonGiftView2.hasReceivedNewMsg(true);
                }
                break;
            case Globle.KEY_EVENT_GIFT_FINISH:
                CommonGiftAnimEvent giftAnimEvent = (CommonGiftAnimEvent) msg;
                synchronized (this) {
                    if (receiveGiftMsgMap.size() > 0) {
                        if (giftAnimEvent.giftViewTag == 2) {
                            Iterator<String> iterator = receiveGiftMsgMap.keySet().iterator();
                            String mapKey = iterator.next();
                            // 选择将要执行的礼物与另一个跑道上的礼物不是同一个人发的同一种礼物
                            while (commonGiftView1.isRunning() && commonGiftView1.compareTo(mapKey)) {
                                if (iterator.hasNext()) {
                                    mapKey = iterator.next();
                                } else {
                                    mapKey = null;
                                    break;
                                }
                            }

                            if (mapKey != null && (commonGiftView2.compareTo(mapKey) || commonGiftView2.compareTo(""))) {
                                commonGiftView2.startAnimation(mapKey, receiveGiftMsgMap.remove(mapKey));
                            } else if (commonGiftView2.getViewStatus() == CommonGiftView.GIFTVIEWPAUSE) {
                                commonGiftView2.stopAnimation();
                            }

                        } else if (giftAnimEvent.giftViewTag == 1) {
                            Iterator iterator = receiveGiftMsgMap.keySet().iterator();
                            String mapKey = (String) iterator.next();
                            while (commonGiftView2.isRunning() && commonGiftView2.compareTo(mapKey)) {
                                if (iterator.hasNext()) {
                                    mapKey = (String) iterator.next();
                                } else {
                                    mapKey = null;
                                    break;
                                }
                            }

                            if (mapKey != null && (commonGiftView1.compareTo(mapKey) || commonGiftView1.compareTo(""))) {
                                commonGiftView1.startAnimation(mapKey, receiveGiftMsgMap.remove(mapKey));
                            } else if (commonGiftView1.getViewStatus() == CommonGiftView.GIFTVIEWPAUSE) {
                                commonGiftView1.stopAnimation();
                            }
                        }
                    } else {

                        if (giftAnimEvent.giftViewTag == 1 && commonGiftView1.isRunning()) {
                            commonGiftView1.stopAnimation();
                        } else if (giftAnimEvent.giftViewTag == 2 && commonGiftView2.isRunning()) {
                            commonGiftView2.stopAnimation();
                        }
                    }
                }

                break;
            case Globle.KEY_ENTER_ROOM_MSG:
                //进场
                EnterRoomEvent event = (EnterRoomEvent) msg;
//                if(!TextUtils.isEmpty(event.getUserId()) && UserInfoUtils.isAlreadyLogin() && isAnchor && UserInfoUtils.getUserInfo().getUserId().equals(event.getUserId())){
//                    //主播自己进场
//                    return;
//                }
                if (!TextUtils.isEmpty(event.getUserId()) && !event.getUserId().equals("-1")) {
                    tvEnterRoom.setText(getContext().getString(R.string.welcome_erter_room, event.getNickName()));
                    if (mMessageHandler == null)
                        mMessageHandler = new MessageHandler(this);
                    mMessageHandler.removeMessages(MSG_ENTER_ROOM);
                    lastEnterTime = System.currentTimeMillis();
                    mMessageHandler.sendEmptyMessageDelayed(MSG_ENTER_ROOM, 5000);
                }
                AudienceResult.MemberListEntity audience = new AudienceResult.MemberListEntity();
                audience.setSex(event.getSex());
                audience.setProfile(event.getProfile());
                audience.setUserId(event.getUserId());
                mAdapter.addData(audience);
                break;
            case Globle.KEY_KICKOUT_MSG:
                //踢人
                KickOutEvent kick = (KickOutEvent) msg;
                if (kick.isResponse() && kick.isSuccess()) {
//                    ShowUtils.showToast(getContext().getString(R.string.tv_kick_success));
                } else if (!kick.isResponse() && UserInfoUtils.isAlreadyLogin()) {
                    String userId = kick.getUserId();
                    if (!TextUtils.isEmpty(userId) && userId.equals(UserInfoUtils.getUserInfo().getUserId())) {
                        ShowUtils.showToast(getContext().getString(R.string.kick_out_msg));
                        ivClose.performClick();
                    }
                }
                break;
            case Globle.KEY_ALARM_REQUEST_AUDIENCE:
//                getAudience();
                break;
            case Globle.SEND_GIFT_RESPONSE:
                if (giftDialog != null && giftDialog.isShowing()) {
                    SendGiftResponse response = (SendGiftResponse) msg;
                    giftDialog.setBalance(response);
                }
                break;
            case Globle.KEY_EVENT_RECEIVE__GIFT_FORCHAT:
                ReciveGiftInfo giftChatMsg = (ReciveGiftInfo) msg;
                //生成聊天消息
                MsgLog giftMsg = new MsgLog();
                giftMsg.setChatMode(MsgLog.CHAT_MODE_GROUP);
                giftMsg.setMsgType(MsgLog.TYPE_GIFT);
                giftMsg.setMsgContent(getContext().getString(R.string.gift_msg, giftChatMsg.getGiftName()));
                giftMsg.setSendNumber(giftChatMsg.getSenderId());
                giftMsg.setNickName(giftChatMsg.getSenderName());
                addMsg(giftMsg);
                break;
            default:
                break;
        }
    }

    public List<ReciveGiftInfo> getData(String userId) {
        synchronized (this) {
            Iterator<String> iterator = receiveGiftMsgMap.keySet().iterator();
            if (iterator.hasNext()) {
                if (userId.equals(iterator.next())) {
                    return receiveGiftMsgMap.remove(userId);
                }
            }

            return null;
        }
    }

    private void addMsg(MsgLog msgLog) {
        roomChatAdapter.addData(msgLog, true);
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isScrolling)
                    listView.smoothScrollToPosition(roomChatAdapter.getCount() - 1);
            }
        }, 100);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Animation move = AnimationUtils.loadAnimation(getContext(), R.anim.anim_in_from_bottom);
        rlBtns.clearAnimation();
        rlBtns.setAlpha(1);
        rlBtns.startAnimation(move);
        if (dialog.getClass().equals(GiftDialog.class)) {
            Animation alpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha_in);
            listView.clearAnimation();
            listView.setAlpha(1);
            listView.startAnimation(alpha);
            tvEnterRoom.clearAnimation();
            tvEnterRoom.setAlpha(1);
            tvEnterRoom.startAnimation(alpha);
        }
    }

    class MyOnTouch implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    hideKeyBoard();
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 隐藏键盘和表情
     */
    public void hideKeyBoard() {
        if (mPanelRoot.getVisibility() == View.VISIBLE) {
            mPanelRoot.setVisibility(View.GONE);
            hideTop(false);
        } else {
            KeyBoardUtils.closeKeybord(et, getContext());
        }
        rlInput.setVisibility(View.GONE);
        rlBtns.setVisibility(View.VISIBLE);
    }

    /**
     * 用于观众接到主播下播通知时,隐藏所有弹出框
     */
    public void hideDialog() {
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.dismiss();
        }
        if (shareDialog != null && shareDialog.isShowing()) {
            shareDialog.dismiss();
        }
        if (giftDialog != null && giftDialog.isShowing()) {
            giftDialog.dismiss();
        }
        if (simpleLoginDialog != null && simpleLoginDialog.isShowing()) {
            simpleLoginDialog.dismiss();
        }
        hideKeyBoard();
    }

    public boolean canGoBack() {
        if (mPanelRoot.getVisibility() == View.VISIBLE || isKeyBoardVisible) {
            hideKeyBoard();
            return true;
        }
        return false;
    }


    public boolean isMuteChecked() {

        return cbVoice.isChecked();
    }

    public void setFlashswitch(boolean enabled) {
        if (enabled) {
            rlFlicker.setVisibility(View.VISIBLE);
            cbFlicker.setChecked(false);
        } else {
            rlFlicker.setVisibility(View.GONE);
        }
    }

    /**
     * 获取观众列表
     */
    private void getAudience() {
        String id;
        if (isAnchor) {
            id = UserInfoUtils.getUserInfo().getUserId();
        } else {
            id = LiveInfoUtils.getShowId();
        }
        ReqRoomApi.reqAudienceList((Activity) getContext(), id, "1", "1000", new RequestCallback<AudienceResult>() {
            @Override
            public void onRequestSuccess(AudienceResult object) {
                List<AudienceResult.MemberListEntity> memberList = object.getMemberList();
                int size = Math.min(1000, memberList.size());
                ArrayList<AudienceResult.MemberListEntity> memberListEntities = new ArrayList<AudienceResult.MemberListEntity>();
                memberListEntities.addAll(memberList.subList(0, size));
                Collections.reverse(memberListEntities);
                AudienceResult.MemberListEntity omit = new AudienceResult.MemberListEntity();
                omit.setUserId("-1");
                omit.setProfile("res://" + getContext().getPackageName() + "/" + R.drawable.omit);
                memberListEntities.add(omit);
                mAdapter.setmDatas(memberListEntities);
                tvAudience.setText(object.getMemberSize());
                tvCollect.setText(object.getSupportCnt());
            }

            @Override
            public void onRequestFailure(AudienceResult error) {

            }
        });
    }

    private void sendMsg(String content) {
        UserInfoResult.UserEntity user = UserInfoUtils.getUserInfo();
        String showId = "";
        if (isAnchor) {
            showId = user.getUserId();
        } else {
            showId = LiveInfoUtils.getShowId();
        }

        MsgLog msgLog = new MsgLog();
        msgLog.sendNumber = user.getUserId();
        msgLog.showId = showId;
        msgLog.nickName = user.getNickName();
        msgLog.msgContent = content;
        msgLog.chatMode = MsgLog.CHAT_MODE_GROUP;
        msgLog.msgType = MsgLog.TYPE_TXT_MSG;
        addMsg(msgLog);
        SendUtils.sendMessage(msgLog);
    }


    public void showLoginDilaog() {
        if (simpleLoginDialog == null) {
            simpleLoginDialog = new SimpleLoginDialog(getContext());
        }
        simpleLoginDialog.show();
    }

    public EditText getEditView() {
        return et;
    }

    public void destroyPariseView() {
        vsPariseView.destroyHolder();
    }

    /**
     * 请求是否关注了主播
     *
     * @param startId
     */
    private void requestRelation(String startId) {
        if (UserInfoUtils.isAlreadyLogin() && !TextUtils.isEmpty(startId)) {
            String userId = UserInfoUtils.getUserInfo().getUserId();
            ReqUserApi.requestUserInfo(this, startId, userId, new RequestCallback<UserInfoResult>() {
                @Override
                public void onRequestSuccess(UserInfoResult object) {
                    if (object != null) {
                        String relatoin = object.getUser().getExtendData().getRelatoin();
                        if (!TextUtils.isEmpty(relatoin) && relatoin.equals("1")) {
                            //已经关注
                        } else {
                            llFollow.setVisibility(View.VISIBLE);
                            mMessageHandler.sendEmptyMessageDelayed(MSG_FOLLOW_NOTICE, FOLLOW_TIME);
                        }
                    }
                }

                @Override
                public void onRequestFailure(UserInfoResult error) {

                }
            });
        }
    }

    public CheckBox getCheckBox(int id) {
        CheckBox cb = null;
        switch (id) {
            case R.id.cb_voice:
                cb = cbVoice;
                break;
            case R.id.cb_flicker:
                cb = cbFlicker;
                break;
            case R.id.cb_skincare:
                cb = cbSkincare;
                break;
        }
        return cb;
    }

    public void clearSpecialEffects() {
        vsPariseView.stopDraw();
        receiveGiftMsgMap.clear();
        commonGiftView1.setAnimatorFlag(false);
        commonGiftView2.setAnimatorFlag(false);
    }
}
