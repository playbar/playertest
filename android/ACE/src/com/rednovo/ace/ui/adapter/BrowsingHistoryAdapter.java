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
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.List;

/**
 * Created by Dk on 16/2/27.
 */
public class BrowsingHistoryAdapter extends BaseAdapter {

    private Context context;

    private List<LiveInfo> myHistory;

    public BrowsingHistoryAdapter(Context context, List<LiveInfo> myHistory) {
        this.context = context;
        this.myHistory = myHistory;
    }

    @Override
    public int getCount() {
        if (myHistory != null) {
            return myHistory.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (myHistory != null) {
            return myHistory.get(position);
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
        if (myHistory.get(position) != null) {
            ((GenericDraweeHierarchy) holder.imgPortrait.getHierarchy()).setFailureImage(AceApplication.getApplication().getResources().getDrawable(R.drawable.head_offline));
            FrescoEngine.setSimpleDraweeView(holder.imgPortrait, myHistory.get(position).getProfile(), ImageRequest.ImageType.DEFAULT);
            holder.tvName.setText(myHistory.get(position).getNickName());
            if(!TextUtils.isEmpty(myHistory.get(position).getSignature())){
                holder.tvSignature.setText(myHistory.get(position).getSignature());
            }
//            holder.imgLevelIcon.setImageResource(LevelUtils.getLevelIcon(myHistory.get(position).getRank()));
        }
        return convertView;
    }

    private class ViewHolder {
        public SimpleDraweeView imgPortrait;
        public TextView tvName;
        public TextView tvSignature;
        public ImageView imgLevelIcon;
        public ImageView imgLive;
    }
}
