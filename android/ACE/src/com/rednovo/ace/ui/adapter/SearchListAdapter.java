package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.SearchResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.common.LevelUtils;
import com.rednovo.libs.common.StringUtils;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilong on 16/2/27.
 */
public class SearchListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchResult.UserListEntity> anchorsList = new ArrayList<SearchResult.UserListEntity>();

    public SearchListAdapter(Context cxt) {
        this.mContext = cxt;

    }

    public void setData(List<SearchResult.UserListEntity> list) {
        this.anchorsList.clear();
        if (list != null) {
            this.anchorsList.addAll(list);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (anchorsList != null) {
            return anchorsList.size();
        }

        return 0;
    }

    @Override
    public SearchResult.UserListEntity getItem(int position) {
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
            convertView = View.inflate(mContext, R.layout.adapter_search_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchResult.UserListEntity userListEntity = anchorsList.get(position);
        if (userListEntity != null) {
            viewHolder.anchorsNickname.setText(userListEntity.getNickName());
            Context cxt = BaseApplication.getApplication().getApplicationContext();
            ((GenericDraweeHierarchy) viewHolder.anchorsHead.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_online));
            FrescoEngine.setSimpleDraweeView(viewHolder.anchorsHead, userListEntity.getProfile(), ImageRequest.ImageType.SMALL);

            String signature = userListEntity.getSignature();

            if(StringUtils.isEmpty(signature)) {
                signature = mContext.getString(R.string.signature_default_text);
            }
            viewHolder.userSignature.setText(signature);
        }
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
