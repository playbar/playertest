package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.MySubscribeListResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/2/26.
 */
public class MySubscribeAdapter extends BaseAdapter {

    private Context context;

    private List<MySubscribeListResult.MySubscribe> mySubscribeList = new ArrayList<MySubscribeListResult.MySubscribe>();

    public MySubscribeAdapter(Context context) {
        this.context = context;
    }

    public void setMySubscribeList(List<MySubscribeListResult.MySubscribe> mySubscribeList) {
        if (mySubscribeList != null) {
            this.mySubscribeList = mySubscribeList;
        }
    }

    @Override
    public int getCount() {
        if (mySubscribeList != null) {

            return mySubscribeList.size();
        }
        return 0;
    }

    @Override
    public MySubscribeListResult.MySubscribe getItem(int position) {
        if (mySubscribeList != null) {
            return mySubscribeList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_pulltorefresh_item, null);
            holder = new ViewHolder();
            holder.imgPortrait = (SimpleDraweeView) convertView.findViewById(R.id.img_ptrf_item_portrait);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_ptrf_item_name);
            holder.tvSignature = (TextView) convertView.findViewById(R.id.tv_ptrf_item_signature);
            holder.imgLevelIcon = (ImageView) convertView.findViewById(R.id.img_ptrf_item_level_icon);
            holder.imgLive = (ImageView) convertView.findViewById(R.id.img_ptrf_item_live);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mySubscribeList.get(position) != null) {
            Context cxt = BaseApplication.getApplication().getApplicationContext();
            ((GenericDraweeHierarchy) holder.imgPortrait.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_offline));

            FrescoEngine.setSimpleDraweeView(holder.imgPortrait, mySubscribeList.get(position).getProfile(), ImageRequest.ImageType.DEFAULT);
            holder.tvName.setText(mySubscribeList.get(position).getNickName());
            if(!TextUtils.isEmpty(mySubscribeList.get(position).getSignature())){
                holder.tvSignature.setText(mySubscribeList.get(position).getSignature());
            }
//            holder.imgLevelIcon.setImageResource(LevelUtils.getLevelIcon(mySubscribeList.get(position).getRank()));
        }
        return convertView;
    }

    public void clear() {
        mySubscribeList.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public SimpleDraweeView imgPortrait;
        public TextView tvName;
        public TextView tvSignature;
        public ImageView imgLevelIcon;
        public ImageView imgLive;
    }
}
