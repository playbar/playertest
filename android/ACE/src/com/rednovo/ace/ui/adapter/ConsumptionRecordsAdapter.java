package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rednovo.ace.R;

/**
 * Created by Dk on 16/3/1.
 */
public class ConsumptionRecordsAdapter extends BaseAdapter{

    private Context context;

    public ConsumptionRecordsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.layout_recharge_records_item, null);
            holder = new ViewHolder();
            holder.llTitle = (LinearLayout) convertView.findViewById(R.id.ll_recharge_records_title);
            holder.tvTitleDate = (TextView) convertView.findViewById(R.id.tv_recharge_records_item_title_time);
            holder.tvGive = (TextView) convertView.findViewById(R.id.tv_consumption_records_item_body_get_text);
            holder.tvGiftNum = (TextView) convertView.findViewById(R.id.tv_consumption_records_item_body_gift_num_text);
            holder.tvGiftName = (TextView) convertView.findViewById(R.id.tv_consumption_records_item_body_gift_name_text);
            holder.tvPayNum = (TextView) convertView.findViewById(R.id.tv_consumption_records_item_body_pay_num_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {

        public LinearLayout llTitle;

        public TextView tvTitleDate;

        public TextView tvGive;

        public TextView tvGiftNum;

        public TextView tvGiftName;

        public TextView tvPayNum;
    }
}
