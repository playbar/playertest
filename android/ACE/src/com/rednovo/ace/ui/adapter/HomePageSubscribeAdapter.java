package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.MySubscribeListResult;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.List;

/**
 * Created by lilong on 16/3/3.
 */
public class HomePageSubscribeAdapter extends BaseAdapter{

    private Context mContext;
    private List<MySubscribeListResult.MySubscribe> anchorsList;

    public HomePageSubscribeAdapter(Context cxt) {
        this.mContext = cxt;

    }

    public void setMySubscribeList(List<MySubscribeListResult.MySubscribe> mySubscribeList) {
        if(mySubscribeList != null) {
            this.anchorsList = mySubscribeList;
        }
    }

    @Override
    public int getCount() {

        if (anchorsList != null) {
            return anchorsList.size();
        }

        return 0;
    }

    @Override
    public MySubscribeListResult.MySubscribe getItem(int position) {

        if (anchorsList != null) {
            return anchorsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_home_page_subscribe_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MySubscribeListResult.MySubscribe subscribeUser = anchorsList.get(position);
        viewHolder.anchorsNickname.setText(subscribeUser.getNickName());

        String signature = subscribeUser.getSignature();

        if(StringUtils.isEmpty(signature)) {
            signature = mContext.getString(R.string.signature_default_text);
        }
        viewHolder.userSignature.setText(signature);

        FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, subscribeUser.getProfile(), ImageRequest.ImageType.SMALL);
        return convertView;
    }

    private class ViewHolder {

        SimpleDraweeView anchorsHead;
        TextView anchorsNickname, userSignature;

        public ViewHolder(View view) {
            anchorsHead = (SimpleDraweeView) view.findViewById(R.id.fresco_anchors_head);
            anchorsNickname = (TextView) view.findViewById(R.id.tv_anchors_nickname);
            userSignature = (TextView) view.findViewById(R.id.tv_user_signature);

        }
    }

}
