//@formatter:off
package com.iot.volunteer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iot.volunteer.R;
import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

public class AdapterKnowledge extends RecyclerView.Adapter<AdapterKnowledge.HolderKnowledge> {
    ArrayList<ItemNews>     knowledgeList = new ArrayList<>();
    private ItemClickListener mClickListener;

    public AdapterKnowledge(ArrayList<ItemNews> knowledgeList) {
        this.knowledgeList = knowledgeList;
    }

    @Override
    public HolderKnowledge onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_knowledge, parent, false);
        return new HolderKnowledge(itemView);
    }

    @Override
    public void onBindViewHolder(HolderKnowledge viewHolder, int position) {
        ItemNews item = knowledgeList.get(position);
        viewHolder.tvTitle.setText(item.title);
        try {
            Picasso.get().load(item.picture).into(viewHolder.ivPicture);
        } catch (Exception e) {
           //
        }
    }

    @Override
    public int getItemCount() {
        return knowledgeList.size();
    }

    public class HolderKnowledge extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView     tvTitle;
        public ImageView    ivPicture;

        public HolderKnowledge(View root) {
            super(root);
            tvTitle = root.findViewById(R.id.tvTitle);
            ivPicture = root.findViewById(R.id.ivPicture);
            root.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}