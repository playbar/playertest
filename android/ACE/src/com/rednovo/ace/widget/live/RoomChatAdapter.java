package com.rednovo.ace.widget.live;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.MsgLog;
import com.rednovo.ace.widget.MTextView;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.EmojiUtils;

import java.util.List;

/**
 * 房间聊天消息
 *
 * @author Xd
 */
public class RoomChatAdapter extends ArrayAdapter<MsgLog> {
    private static final String TAG = "RoomChatAdapter";
    private List<MsgLog> mMsgLogs;
    private Context mContext;
    private float textSize = 17;
    private OnChatItemClickLitener mOnChatItemClickLitener;

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public RoomChatAdapter(Context context, int resource, List<MsgLog> datats) {
        super(context, resource, datats);
        this.mContext = context;
        this.mMsgLogs = datats;
    }

    @Override
    public int getCount() {
        return mMsgLogs.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_p_chat_item, null);
            holder = new ViewHolder();
            holder.tv = (MTextView) convertView.findViewById(R.id.tv_content);
            holder.tvNormal = (TextView) convertView.findViewById(R.id.tv_content_normal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MsgLog msgLog = mMsgLogs.get(position);
        int msgType = msgLog.getMsgType();
        String name = "";
        SpannableString emojiString;
        SpannableStringBuilder content;
        holder.tv.setVisibility(View.GONE);
        holder.tvNormal.setVisibility(View.GONE);
        switch (msgType) {
            case MsgLog.TYPE_TXT_MSG:
                holder.tv.setVisibility(View.VISIBLE);
                name = msgLog.nickName + ":";
                content = new SpannableStringBuilder(name);
                emojiString = EmojiUtils.getEmojiString(mContext, msgLog.msgContent, Constant.EMOJI_ZZ);
                content.append(emojiString);
                if (UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId().equals(msgLog.getSendNumber())) {
                    content.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_ff6e4c)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    content.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_ffd200)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.tv.setMText(content);
                holder.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case MsgLog.TYPE_TIPS:
                name = "<" + mContext.getString(R.string.room_tips) + ">";
                sysChat(holder, msgLog, mContext.getResources().getColor(R.color.color_ff2a2a), name);
                break;
            case MsgLog.TYPE_SYSTEM:
                name = "<" + mContext.getString(R.string.room_system) + ">";
                sysChat(holder, msgLog, mContext.getResources().getColor(R.color.color_ff1edf), name);
                break;
            case MsgLog.TYPE_GIFT:
                tipsChat(holder, msgLog, mContext.getResources().getColor(R.color.color_edff59));
                break;
            case MsgLog.TYPE_SHARE:
                tipsChat(holder, msgLog, mContext.getResources().getColor(R.color.color_3affd3));
                break;
            case MsgLog.TYPE_SUPPORT:
                tipsChat(holder, msgLog, mContext.getResources().getColor(R.color.color_c85dff));
                break;
            case MsgLog.TYPE_FOLLOW:
                tipsChat(holder, msgLog, mContext.getResources().getColor(R.color.color_ffd201));
                break;
            default:
                break;
        }
        holder.tv.setOnClickListener(new MyClickListener(msgLog));
        holder.tvNormal.setOnClickListener(new MyClickListener(msgLog));
        return convertView;
    }

    /**
     * 系统消息，直播提醒
     *
     * @param holder
     * @param msgLog
     * @param color
     * @param name
     */
    private void sysChat(ViewHolder holder, MsgLog msgLog, int color, String name) {
        SpannableStringBuilder content;
        Spanned spanned;
        int spanStart;
        int spanEnd;
        MyClickSpan clickSpan;
        URLSpan[] urls;
        SpannableStringBuilder style;
        holder.tvNormal.setVisibility(View.VISIBLE);
        holder.tvNormal.setTextColor(color);
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
        content.setSpan(clickSpan, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tvNormal.setText(content);
        holder.tvNormal.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 点赞，关注，分享，送礼物消息
     *
     * @param holder
     * @param msgLog
     * @param color
     */
    private void tipsChat(ViewHolder holder, MsgLog msgLog, int color) {
        String name;
        SpannableStringBuilder content;
        holder.tvNormal.setVisibility(View.VISIBLE);
        name = msgLog.nickName;
        content = new SpannableStringBuilder(name + " " + msgLog.getMsgContent());
        content.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_ffd200)), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvNormal.setText(content);
        holder.tvNormal.setTextColor(color);
        holder.tvNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }


    public List<MsgLog> getmDatas() {
        return mMsgLogs;
    }

    public void setmDatas(List<MsgLog> mDatas) {
        this.mMsgLogs = mDatas;
    }

    public synchronized void addData(MsgLog msgLog, boolean notify) {
        if (mMsgLogs != null && mMsgLogs.size() >= 100) {
            mMsgLogs.remove(0);
        }
        mMsgLogs.add(msgLog);
        if (notify)
            notifyDataSetChanged();
    }

    class ViewHolder {
        public MTextView tv;
        public TextView tvNormal;
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


    public interface OnChatItemClickLitener {
        void onChatItemClick(MsgLog msgLog);

        void onLinkClick(String url);
    }

    public void setOnChatItemClickLitener(OnChatItemClickLitener mOnChatItemClickLitener) {
        this.mOnChatItemClickLitener = mOnChatItemClickLitener;
    }

    class MyClickListener implements View.OnClickListener {
        private MsgLog msg;

        public MyClickListener(MsgLog msg) {
            this.msg = msg;
        }

        @Override
        public void onClick(View view) {
            if (mOnChatItemClickLitener != null) {
                mOnChatItemClickLitener.onChatItemClick(msg);
            }
        }
    }
}
