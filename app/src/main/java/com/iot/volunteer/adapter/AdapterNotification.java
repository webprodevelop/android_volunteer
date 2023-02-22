//@formatter:off
package com.iot.volunteer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.volunteer.Prefs;
import com.iot.volunteer.R;
import com.iot.volunteer.activity.ActivityNotification;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.util.Util;

import java.util.ArrayList;
import java.util.Date;

public class AdapterNotification extends ArrayAdapter<ItemNotification> {
    ArrayList<ItemNotification>     notificationList = new ArrayList<>();
    public AdapterNotification(Context context, ArrayList<ItemNotification> notifications) {
        super(context, 0, notifications);

        notificationList = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemNotification item = getItem(position);

        HolderNotification viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_notification, parent, false);
            viewHolder = new HolderNotification(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderNotification) convertView.getTag();
        }

        if (item.isRead == 1) {
            viewHolder.tvTitle.setTextColor(getContext().getColor(android.R.color.darker_gray));
            viewHolder.tvTime.setTextColor(getContext().getColor(android.R.color.darker_gray));
            viewHolder.tvContent.setTextColor(getContext().getColor(android.R.color.darker_gray));
        } else {
            viewHolder.tvTitle.setTextColor(Color.BLACK);
            viewHolder.tvTime.setTextColor(Color.BLACK);
            viewHolder.tvContent.setTextColor(Color.BLACK);
        }
        viewHolder.tvDelete.setBackgroundResource(R.drawable.selector_small_red_button_fill);
        viewHolder.tvDelete.setText(R.string.str_delete);

        if (item.type.equals("task")) {
//            viewHolder.tvTitle.setText(R.string.str_alarm_title);
            if (item.taskStatus.equals("create") && Prefs.Instance().getTaskId().equals(item.taskId)) {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.sos_active));
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.tween);
                viewHolder.ivIcon.startAnimation(myFadeInAnimation);
                viewHolder.tvTitle.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_red));
                viewHolder.tvStatus.setText(R.string.str_progress_status);
                viewHolder.tvStatus.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_red));
            } else {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.sos_inactive));
                viewHolder.tvStatus.setText(R.string.str_finish_status);
            }
        }
        else {
//            viewHolder.tvTitle.setText(R.string.str_notice_title);

            if (item.isRead == 0) {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.notice_active));
                viewHolder.tvTitle.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_notification_active));
                viewHolder.tvStatus.setText(R.string.str_unread_status);
                viewHolder.tvStatus.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_notification_active));
            } else {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.notice_inactive));
                viewHolder.tvStatus.setText(R.string.str_read_status);
            }
        }

        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvTime.setText(Util.getDateTimeFormatString(new Date(item.time)));
        viewHolder.tvContent.setText(item.content);
//        viewHolder.tvView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Prefs.Instance().getTaskId().equals(item.data)) {
//                    ((ActivityNotification)getContext()).showNotification(item);
//                }
//            }
//        });

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

                final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

                TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
                TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
                TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
                tvConfirm.setText(R.string.str_delete_confirm);

                btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

                btnConfirm.setOnClickListener(v -> {
                    confirmDlg.dismiss();
                    ((ActivityNotification) getContext()).deleteNotification(item);
                });

                confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmDlg.setView(confirmView);
                confirmDlg.show();
            }
        });

        return convertView;
    }

    public class HolderNotification {

        public TextView     tvTitle;
        public TextView		tvStatus;
        public TextView		tvContent;
        public TextView		tvTime;
        public TextView		tvView;
        public TextView		tvDelete;
        public ImageView    ivIcon;

        public HolderNotification(View root) {
            tvTitle = root.findViewById(R.id.ID_TXTVIEW_TITLE);
            tvStatus = root.findViewById(R.id.ID_TXTVIEW_STATS);
            tvContent = root.findViewById(R.id.ID_TXTVIEW_CONTENT);
            tvTime = root.findViewById(R.id.ID_TXTVIEW_TIME);
            tvView = root.findViewById(R.id.ID_BTN_VIEW);
            tvDelete = root.findViewById(R.id.ID_BTN_DELETE);
            ivIcon = root.findViewById(R.id.ID_IMGVIEW_ICON);
        }
    }
}