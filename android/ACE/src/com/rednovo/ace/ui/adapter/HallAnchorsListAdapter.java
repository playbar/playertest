package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.HotResult;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页主播列表
 * Created by lilong on 16/2/27.
 */
public class HallAnchorsListAdapter extends BaseAdapter {

    private List<HotResult.Data> anchorsList = new ArrayList<HotResult.Data>();
    private List<HotResult.User> userList = new ArrayList<HotResult.User>();
    private Context mContext;
    private Map<String, Object> itemData;
    private View llErrorPagerLayout;
    private TextView tvErrorHint;
    private ImageView ivErrorPager;

    private boolean isNetAvailable = true;

    public HallAnchorsListAdapter(Context cxt) {
        mContext = cxt;
        itemData = new HashMap<String, Object>();

        llErrorPagerLayout = View.inflate(mContext, R.layout.error_view_layout, null);
        ivErrorPager = (ImageView) llErrorPagerLayout.findViewById(R.id.error_pager_icon);
        tvErrorHint = (TextView) llErrorPagerLayout.findViewById(R.id.tv_error_hint);
        int bannerHeight = ScreenUtils.getScreenWidth(cxt);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, bannerHeight);
        llErrorPagerLayout.setLayoutParams(params);
    }

    public void setData(List<HotResult.Data> datas, List<HotResult.User> users) {
        isNetAvailable = true;
        anchorsList.clear();
        userList.clear();
        if (datas != null && users != null) {

            anchorsList.addAll(datas);
            userList.addAll(users);
        }

        notifyDataSetChanged();
    }

    public void setMyEmptyContent(boolean isDataEmpty) {
        anchorsList.clear();
        userList.clear();
        if (isDataEmpty) {
            this.isNetAvailable = true;
            tvErrorHint.setText("暂时没有直播!");
            ivErrorPager.setImageResource(R.drawable.img_no_content_bg);
        } else {
            tvErrorHint.setText(mContext.getResources().getString(R.string.no_internet_message));
            ivErrorPager.setImageResource(R.drawable.img_no_internet_bg);
            this.isNetAvailable = false;
        }
    }

    public boolean isNetAvailable() {
        return isNetAvailable;
    }

    @Override
    public int getCount() {

        if (anchorsList != null && anchorsList.size() > 0) {
            return anchorsList.size();
        }
        return 1;
    }

    @Override
    public Map<String, Object> getItem(int position) {
        itemData.clear();
        if(position < anchorsList.size() && position < userList.size()) {
            itemData.put("roomInfo", anchorsList.get(position));
            itemData.put("userInfo", userList.get(position));
            return itemData;
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (anchorsList.size() == 0) {
            return llErrorPagerLayout;
        } else {
            ViewHolder viewHolder;

            if (convertView == null || (convertView == llErrorPagerLayout)) {
                convertView = View.inflate(mContext, R.layout.adapter_anchors_list_item, null);
                viewHolder = new ViewHolder(convertView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // 主播信息
            if (userList.size() > position) {
                HotResult.User user = userList.get(position);
                if (user != null) {
                    viewHolder.anchorsNickname.setText(user.getNickName());
                    viewHolder.anchorsHead.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline), ScalingUtils.ScaleType.FIT_CENTER);
                    FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, user.getProfile(), ImageRequest.ImageType.SMALL);

                    String showImg = user.getShowImg();
                    if (!StringUtils.isEmpty(showImg)) {
                        viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.home_page_placeholder_img), ScalingUtils.ScaleType.FIT_CENTER);
                        FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, showImg, ImageRequest.ImageType.SMALL);
                    } else {
                        viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.home_page_placeholder_img), ScalingUtils.ScaleType.FIT_CENTER);
                        FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, user.getProfile(), ImageRequest.ImageType.SMALL);
                    }
                } else {
                    viewHolder.anchorsNickname.setText("");
                    viewHolder.anchorsHead.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline));
                    FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, "", ImageRequest.ImageType.SMALL);
                    viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline));
                    FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, "", ImageRequest.ImageType.SMALL);
                }
            }

            // 房间信息
            if (anchorsList.size() > position) {
                HotResult.Data hot = anchorsList.get(position);
                if (hot != null) {
                    String pos = hot.getPosition().replace("/", "-");
                    if (TextUtils.isEmpty(pos)) {
                        pos = "未知";
                    }
                    viewHolder.anchorsInfo.setText(String.format(mContext.getResources().getString(R.string.person_cnt), hot.getMemberCnt(), pos));
                    viewHolder.roomTitle.setText(hot.getTitle());
                }
            }

            return convertView;
        }
    }

    public class ViewHolder {

        public SimpleDraweeView anchorsHead, liveCover;
        public TextView anchorsNickname, anchorsInfo, roomTitle;
        public ImageView anchorsLiveing;


        public ViewHolder(View view) {
            anchorsHead = (SimpleDraweeView) view.findViewById(R.id.fresco_anchors_head);
            anchorsNickname = (TextView) view.findViewById(R.id.tv_anchors_nickname);
            anchorsInfo = (TextView) view.findViewById(R.id.tv_anchors_info);
            anchorsLiveing = (ImageView) view.findViewById(R.id.iv_achors_liveing);
            liveCover = (SimpleDraweeView) view.findViewById(R.id.fresco_live_cover);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) liveCover.getLayoutParams();
            params.height = ShowUtils.getWidthPixels();

            liveCover.setLayoutParams(params);
            roomTitle = (TextView) view.findViewById(R.id.tv_room_title);
        }
    }
}
