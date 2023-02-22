//@formatter:off
package com.iot.volunteer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemNews;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecommended extends ArrayAdapter<ItemNews> {
    ArrayList<ItemNews>     recommendedList = new ArrayList<>();
    public AdapterRecommended(Context context, ArrayList<ItemNews> recommendedList) {
        super(context, 0, recommendedList);

        this.recommendedList = recommendedList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemNews item = getItem(position);

        HolderRecommended viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_recommended, parent, false);
            viewHolder = new HolderRecommended(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderRecommended) convertView.getTag();
        }

        viewHolder.tvTitle.setText(item.title);
        if (item.picture != null && !item.picture.isEmpty()) {
            Picasso.get().load(item.picture)
                    .into(viewHolder.ivPicture);
        }

        return convertView;
    }

    public class HolderRecommended {

        public TextView     tvTitle;
        public ImageView    ivPicture;

        public HolderRecommended(View root) {
            tvTitle = root.findViewById(R.id.tvTitle);
            ivPicture = root.findViewById(R.id.ivPicture);
        }
    }
}