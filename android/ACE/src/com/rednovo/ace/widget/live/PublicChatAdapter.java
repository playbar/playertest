package com.rednovo.ace.widget.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.MsgLog;
import com.rednovo.ace.widget.MTextView;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.EmojiUtils;

import java.util.List;

/**
 * 直播间公聊适配器
 */
public class PublicChatAdapter extends RecyclerView.Adapter<PublicChatAdapter.MyViewHolder> {
    private LayoutInflater mInflater;
    private List<MsgLog> mMsgLogs;
    private Context mContext;
    private float textSize = 17;

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * ItemClick的回调接口
     *
     * @author zhy
     */
    public interface OnChatItemClickLitener {
        void onChatItemClick(View view, int position);
        void onLinkClick(String url);
    }

    private OnChatItemClickLitener mOnChatItemClickLitener;

    public void setOnChatItemClickLitener(OnChatItemClickLitener mOnChatItemClickLitener) {
        this.mOnChatItemClickLitener = mOnChatItemClickLitener;
    }

    public PublicChatAdapter(Context context, List<MsgLog> datats) {
        mInflater = LayoutInflater.from(context);
        mMsgLogs = datats;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.adapter_p_chat_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MsgLog msgLog=mMsgLogs.get(position);
//        int chatMode = msgLog.getChatMode();
        int msgType = msgLog.getMsgType();
        String name = "";
        SpannableString emojiString;
        SpannableStringBuilder content;
        Spanned spanned;
        int spanStart;
        int spanEnd;
        MyClickSpan clickSpan;
        URLSpan[] urls;
        SpannableStringBuilder style;
        holder.tv.setVisibility(View.GONE);
        holder.tvNormal.setVisibility(View.GONE);
        switch (msgType){
            case MsgLog.TYPE_TXT_MSG:
                holder.tv.setVisibility(View.VISIBLE);
                name = msgLog.nickName+":";
                content = new SpannableStringBuilder(name);
                emojiString = EmojiUtils.getEmojiString(mContext, msgLog.msgContent, Constant.EMOJI_ZZ);
                content.append(emojiString);
                if(UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId().equals(msgLog.getSendNumber())){
                    content.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_ff6e4c)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    content.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_ffd200)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.tv.setMText(content);
                holder.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_TIPS:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_ff2a2a));
                name = "<" + mContext.getString(R.string.room_tips)+">";
                content = new SpannableStringBuilder(name);
                spanned = Html.fromHtml(msgLog.msgContent);
                content.append(spanned);
                spanStart = 0;
                spanEnd = 0;


                clickSpan = new MyClickSpan("");
                urls = content.getSpans(0, content.length(), URLSpan.class);
                style = new SpannableStringBuilder(content);
                style.clearSpans();// should clear old spans
                for (URLSpan url : urls) {
                    clickSpan = new MyClickSpan(url.getURL());
                    spanStart = content.getSpanStart(url);
                    spanEnd = content.getSpanEnd(url);
                }
                content.setSpan(clickSpan,spanStart,spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.tvNormal.setText(content);
                holder.tvNormal.setMovementMethod(LinkMovementMethod.getInstance());
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_GIFT:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setText(msgLog.getMsgContent());
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_edff59));
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_SYSTEM:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_ff1edf));
                name = "<"+ mContext.getString(R.string.room_system)+">";
                content = new SpannableStringBuilder(name);
                spanned = Html.fromHtml(msgLog.msgContent);
                content.append(spanned);
                spanStart = 0;
                spanEnd = 0;


                clickSpan = new MyClickSpan("");
                urls = content.getSpans(0, content.length(), URLSpan.class);
                style = new SpannableStringBuilder(content);
                style.clearSpans();// should clear old spans
                for (URLSpan url : urls) {
                    clickSpan = new MyClickSpan(url.getURL());
                    spanStart = content.getSpanStart(url);
                    spanEnd = content.getSpanEnd(url);
                }
                content.setSpan(clickSpan,spanStart,spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.tvNormal.setText(content);
                holder.tvNormal.setMovementMethod(LinkMovementMethod.getInstance());
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_SHARE:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setText(msgLog.nickName+msgLog.getMsgContent());
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_3affd3));
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_SUPPORT:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setText(msgLog.nickName+msgLog.getMsgContent());
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_c85dff));
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            case MsgLog.TYPE_FOLLOW:
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvNormal.setText(msgLog.nickName+msgLog.getMsgContent());
                holder.tvNormal.setTextColor(mContext.getResources().getColor(R.color.color_ffd201));
                holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
                break;
            default:
                break;
        }
        //如果设置了回调，则设置点击事件
        if (mOnChatItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnChatItemClickLitener.onChatItemClick(holder.itemView, position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mMsgLogs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MTextView tv;
        TextView tvNormal;
        public MyViewHolder(View view) {
            super(view);
            tv = (MTextView) view.findViewById(R.id.tv_content);
            tvNormal = (TextView) view.findViewById(R.id.tv_content_normal);
        }
    }

    public List<MsgLog> getmDatas() {
        return mMsgLogs;
    }

    public void setmDatas(List<MsgLog> mDatas) {
        this.mMsgLogs = mDatas;
    }

    public synchronized void addData(MsgLog msgLog,boolean notify) {
        if(mMsgLogs != null && mMsgLogs.size()>=100){
            mMsgLogs.remove(0);
        }
        mMsgLogs.add(msgLog);
        if(notify)
            notifyDataSetChanged();
    }
    class MyClickSpan extends ClickableSpan {
        private String url;
        public MyClickSpan(String url) {
            super();
            this.url = url;
        }
        @Override
        public void updateDrawState(TextPaint ds) {
//            ds.setColor(ds.linkColor);
            ds.setColor(mContext.getResources().getColor(R.color.color_0090ff)); //设置链接的文本颜色
            ds.setUnderlineText(true);
        }
        @Override
        public void onClick(View widget) {
            if (mOnChatItemClickLitener != null) {
                mOnChatItemClickLitener.onLinkClick(url);
            }
        }
    }
}
