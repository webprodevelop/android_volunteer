//@formatter:off
package com.iot.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemRescue;

import java.util.ArrayList;

public class AdapterRescue extends ArrayAdapter<ItemRescue> {
    ArrayList<ItemRescue>     rescueList = new ArrayList<>();
    public AdapterRescue(Context context, ArrayList<ItemRescue> rescueList) {
        super(context, 0, rescueList);

        this.rescueList = rescueList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemRescue item = getItem(position);

        HolderRescue viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_rescue, parent, false);
            viewHolder = new HolderRescue(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderRescue) convertView.getTag();
        }

        if ((position % 2) == 0)
            viewHolder.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(),
                    android.R.color.white));
        else {
            viewHolder.llRoot.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.color_bg_even));
        }

        viewHolder.tvVictim.setText(item.victim);
        viewHolder.tvRescueTime.setText(item.rescueTime);
        if (item.status == 1) {
            viewHolder.tvStatus.setText(getContext().getResources().getString(R.string.str_finish));
        } else if (item.status == 2) {
            viewHolder.tvStatus.setText(getContext().getResources().getString(R.string.str_finish));
        } else if (item.status == 3) {
            viewHolder.tvStatus.setText(getContext().getResources().getString(R.string.str_finish));
        }
        viewHolder.tvIntegral.setText(String.valueOf(item.integral));

        return convertView;
    }

    public class HolderRescue {

        public LinearLayout llRoot;
        public TextView     tvVictim;
        public TextView     tvRescueTime;
        public TextView     tvStatus;
        public TextView     tvIntegral;

        public HolderRescue(View root) {
            llRoot = root.findViewById(R.id.llRoot);
            tvVictim = root.findViewById(R.id.tvVictim);
            tvRescueTime = root.findViewById(R.id.tvRescueTime);
            tvStatus = root.findViewById(R.id.tvStatus);
            tvIntegral = root.findViewById(R.id.tvIntegral);
        }
    }
}