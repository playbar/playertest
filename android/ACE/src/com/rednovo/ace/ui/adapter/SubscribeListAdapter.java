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
import com.rednovo.ace.net.parser.HallSubscribeResult;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lilong on 16/3/8.
 */
public class SubscribeListAdapter extends BaseAdapter {


    private List<HallSubscribeResult.UserListEntity> anchorsList = new ArrayList<HallSubscribeResult.UserListEntity>();
    private List<HallSubscribeResult.ShowListEntity> showList = new ArrayList<HallSubscribeResult.ShowListEntity>();
    private Map<String, Object> itemData;
    private Context mContext;
    private View llErrorPagerLayout;
    private TextView tvErrorHint;
    private ImageView ivErrorPager;
    private boolean isNetAvailable = true;

    public SubscribeListAdapter(Context cxt) {
        mContext = cxt;
        itemData = new HashMap<String, Object>();

        llErrorPagerLayout = View.inflate(mContext, R.layout.error_view_layout, null);
        ivErrorPager = (ImageView) llErrorPagerLayout.findViewById(R.id.error_pager_icon);
        tvErrorHint = (TextView) llErrorPagerLayout.findViewById(R.id.tv_error_hint);
        int bannerHeight = ScreenUtils.getScreenWidth(cxt);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, bannerHeight);
        llErrorPagerLayout.setLayoutParams(params);
    }

    public void setData(List<HallSubscribeResult.UserListEntity> anchorsList, List<HallSubscribeResult.ShowListEntity> showList) {
        isNetAvailable = true;
        this.anchorsList.clear();
        this.showList.clear();
        if (anchorsList != null) {
            this.anchorsList.addAll(anchorsList);
        }

        if (showList != null) {
            this.showList.addAll(showList);
        }

        notifyDataSetChanged();
    }

    public void setMyEmptyContent(boolean isDataEmpty) {
        anchorsList.clear();
        showList.clear();
        if (isDataEmpty) {
            isNetAvailable = true;
            tvErrorHint.setText("暂时没有直播!");
            ivErrorPager.setImageResource(R.drawable.img_no_content_bg);
        } else {
            isNetAvailable = false;
            tvErrorHint.setText(mContext.getResources().getString(R.string.no_internet_message));
            ivErrorPager.setImageResource(R.drawable.img_no_internet_bg);
        }
    }

    public boolean isNetAvailable() {
        return isNetAvailable;
    }

    @Override
    public int getCount() {
        if (showList != null && showList.size() > 0) {
            return showList.size();
        }
        return 1;
    }

    @Override
    public Map<String, Object> getItem(int position) {
        itemData.clear();
        if (position < anchorsList.size() && position < showList.size()) {
            itemData.put("userInfo", anchorsList.get(position));
            itemData.put("roomInfo", showList.get(position));
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
        if (showList.size() == 0) {
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

            if (anchorsList.size() > position) {
                HallSubscribeResult.UserListEntity userListEntity = anchorsList.get(position);
                if (userListEntity != null) {

                    viewHolder.anchorsNickname.setText(userListEntity.getNickName());
                    viewHolder.anchorsHead.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline), ScalingUtils.ScaleType.FIT_CENTER);
                    FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, userListEntity.getProfile(), ImageRequest.ImageType.SMALL);

                    String showImg = userListEntity.getShowImg();
                    if (!StringUtils.isEmpty(showImg)) {
                        viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.home_page_placeholder_img), ScalingUtils.ScaleType.FIT_CENTER);
                        FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, showImg, ImageRequest.ImageType.SMALL);
                    } else {
                        viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.home_page_placeholder_img), ScalingUtils.ScaleType.FIT_CENTER);
                        FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, userListEntity.getProfile(), ImageRequest.ImageType.SMALL);
                    }
                } else {
                    viewHolder.anchorsNickname.setText("");
                    viewHolder.anchorsHead.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline), ScalingUtils.ScaleType.FIT_CENTER);
                    FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, "", ImageRequest.ImageType.SMALL);
                    viewHolder.liveCover.getHierarchy().setFailureImage(mContext.getResources().getDrawable(R.drawable.head_offline), ScalingUtils.ScaleType.FIT_CENTER);
                    FrescoEngine.setSimpleDraweeView(viewHolder.liveCover, "", ImageRequest.ImageType.SMALL);
                }
            }

            if (showList.size() > position) {
                HallSubscribeResult.ShowListEntity showListEntity = showList.get(position);

                if (showListEntity != null) {
                    String pos = showListEntity.getPosition().replace("/", "-");
                    if (TextUtils.isEmpty(pos)) {
                        pos = "未知";
                    }
                    viewHolder.anchorsInfo.setText(String.format(mContext.getResources().getString(R.string.person_cnt), showListEntity.getMemberCnt(), pos));
                    viewHolder.roomTitle.setText(showListEntity.getTitle());
                } else {
                    viewHolder.anchorsInfo.setText("");
                    viewHolder.roomTitle.setText("");
                }
            }
        }
        return convertView;
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
