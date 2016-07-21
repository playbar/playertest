package com.rednovo.ace.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rednovo.ace.R;
import com.rednovo.ace.net.parser.GoodListResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dk on 16/2/29.
 */
public class RechargeGridViewAdapter extends BaseAdapter {

    private Context context;

    private List<GoodListResult.GoodListEntity> goodList = new ArrayList<GoodListResult.GoodListEntity>();

    private int selectPosition = -1;

    public RechargeGridViewAdapter(Context context) {
        this.context = context;
    }

    public void setGoodList(List<GoodListResult.GoodListEntity> goodList) {
        this.goodList = goodList;
    }

    @Override
    public int getCount() {
        return goodList.size();
    }

    @Override
    public GoodListResult.GoodListEntity getItem(int position) {
        return goodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_recharge_item, null);
            holder = new ViewHolder();
            holder.tvCoin = (TextView) convertView.findViewById(R.id.tv_recharge_item_coin);
            holder.tvGive = (TextView) convertView.findViewById(R.id.tv_recharge_item_give);
            holder.tvSum = (TextView) convertView.findViewById(R.id.tv_recharge_item_sum);
            holder.cutLine = convertView.findViewById(R.id.tv_recharge_item_cut_line);
            holder.tvCoinUnit = (TextView) convertView.findViewById(R.id.tv_recharge_item_coin_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(goodList.get(position).getCoinPrice()%10000 == 0){
            holder.tvCoin.setText(context.getString(R.string.wans, goodList.get(position).getCoinPrice() / 10000));
        }else{
            holder.tvCoin.setText(goodList.get(position).getCoinPrice() + "");
        }
        holder.tvSum.setText(context.getString(R.string.yuans, goodList.get(position).getRmbPrice() + ""));
        if(position == selectPosition){
            convertView.setEnabled(false);
            holder.tvCoin.setTextColor(Color.parseColor("#ff5b18"));
            holder.tvSum.setTextColor(Color.parseColor("#ff5b18"));
            holder.tvCoinUnit.setTextColor(Color.parseColor("#ff5b18"));
            holder.cutLine.setBackgroundResource(R.color.color_ff5b18);
        }else{
            convertView.setEnabled(true);
            holder.tvCoin.setTextColor(Color.parseColor("#888888"));
            holder.tvSum.setTextColor(Color.parseColor("#888888"));
            holder.tvCoinUnit.setTextColor(Color.parseColor("#888888"));
            holder.cutLine.setBackgroundResource(R.color.color_888888);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView tvCoin;
        public TextView tvGive;
        public TextView tvSum;
        public TextView tvCoinUnit;
        public View cutLine;
    }

    public void setSelectPosition(int position){
        selectPosition = position;
    }
}
