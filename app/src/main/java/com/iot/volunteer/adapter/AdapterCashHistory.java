//@formatter:off
package com.iot.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemCashHistory;

import java.util.ArrayList;

public class AdapterCashHistory extends ArrayAdapter<ItemCashHistory> {
    ArrayList<ItemCashHistory>     cashHistoryList = new ArrayList<>();
    public AdapterCashHistory(Context context, ArrayList<ItemCashHistory> cashHistoryList) {
        super(context, 0, cashHistoryList);

        this.cashHistoryList = cashHistoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemCashHistory item = getItem(position);

        HolderCashHistory viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_cash_history, parent, false);
            viewHolder = new HolderCashHistory(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderCashHistory) convertView.getTag();
        }

        viewHolder.tvTime.setText(item.time);
        viewHolder.tvAmount.setText(String.valueOf(item.amount));
//        if (item.status == 1) {
//            viewHolder.tvStatus.setText(getContext().getResources().getString(R.string.str_processing));
//        } else if (item.status == 2) {
//            viewHolder.tvStatus.setText(getContext().getResources().getString(R.string.str_finish));
//        }
        viewHolder.tvStatus.setText(item.status);
        viewHolder.tvIntegral.setText(String.valueOf(item.integral));

        return convertView;
    }

    public class HolderCashHistory {

        public TextView     tvTime;
        public TextView     tvAmount;
        public TextView     tvStatus;
        public TextView     tvIntegral;

        public HolderCashHistory(View root) {
            tvTime = root.findViewById(R.id.tvTime);
            tvAmount = root.findViewById(R.id.tvAmount);
            tvStatus = root.findViewById(R.id.tvStatus);
            tvIntegral = root.findViewById(R.id.tvIntegral);
        }
    }
}