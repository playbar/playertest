package com.rednovo.ace.widget.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.AudienceResult;
import com.rednovo.libs.BaseApplication;
import com.rednovo.libs.net.fresco.FrescoEngine;

import java.util.List;

/**
 * 直播界面顶部横向头像列表适配
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    /**
     * ItemClick的回调接口
     *
     * @author zhy
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;

    public List<AudienceResult.MemberListEntity> getmDatas() {
        return mDatas;
    }

    public synchronized void setmDatas(List<AudienceResult.MemberListEntity> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    private List<AudienceResult.MemberListEntity> mDatas;

    public GalleryAdapter(Context context, List<AudienceResult.MemberListEntity> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        SimpleDraweeView mImg;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.adapter_horizontal_rview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (SimpleDraweeView) view.findViewById(R.id.id_index_gallery_item_image);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        AudienceResult.MemberListEntity data = mDatas.get(i);
        Context cxt = BaseApplication.getApplication().getApplicationContext();
        ((GenericDraweeHierarchy) viewHolder.mImg.getHierarchy()).setPlaceholderImage(R.drawable.head_online);
        ((GenericDraweeHierarchy) viewHolder.mImg.getHierarchy()).setFailureImage(cxt.getResources().getDrawable(R.drawable.head_offline));
        FrescoEngine.setSimpleDraweeView(viewHolder.mImg, data.getProfile(), ImageRequest.ImageType.SMALL);
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });

        }
    }

    public synchronized void addData(AudienceResult.MemberListEntity userData) {
        if (mDatas != null) {
            mDatas.add(0, userData);
            notifyDataSetChanged();
        }
    }

}