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
import com.rednovo.ace.net.parser.MyFansListResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.List;

/**
 * Created by Dk on 16/2/26.
 */
public class MyFansAdapter extends BaseAdapter {

    private Context context;

    private List<MyFansListResult.MyFansResult> myFansList;

    public MyFansAdapter(Context context) {
        this.context = context;
    }

    public void setMyFansList(List<MyFansListResult.MyFansResult> myFansList) {
        if (myFansList != null) {
            this.myFansList = myFansList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (myFansList != null) {
            return myFansList.size();
        }
        return 0;
    }

    @Override
    public MyFansListResult.MyFansResult getItem(int position) {
        if (myFansList != null) {
            return myFansList.get(position);
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
        if (myFansList.get(position) != null) {
            Context cxt = BaseApplication.getApplication().getApplicationContext();
            ((GenericDraweeHierarchy) holder.imgPortrait.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_offline));

            FrescoEngine.setSimpleDraweeView(holder.imgPortrait, myFansList.get(position).getProfile(), ImageRequest.ImageType.DEFAULT);
            holder.tvName.setText(myFansList.get(position).getNickName());
            if(!TextUtils.isEmpty(myFansList.get(position).getSignature())){
                holder.tvSignature.setText(myFansList.get(position).getSignature());
            }
//            holder.imgLevelIcon.setImageResource(LevelUtils.getLevelIcon(myFansList.get(position).getRank()));
        }
        return convertView;
    }

    public void clear() {
        myFansList.clear();
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
