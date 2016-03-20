package com.example.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.database.Reward;
import com.example.admin.freddyspeaks.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/20/2016.
 */
public class SelectRewardsBoxAdapter extends ArrayAdapter<Reward> {
    private Context context;
    private int layoutResourceId;
    private List<Reward> rewardList;
    RewardSelectionListener rewardSelectionListener;

    public interface RewardSelectionListener {
        void rewardClicked(int index,boolean checked);
    }

    public SelectRewardsBoxAdapter(Context context, int layoutResourceId, List<Reward> rewardList, RewardSelectionListener rewardSelectionListener) {
        super(context, layoutResourceId, rewardList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.rewardList = rewardList;
        this.rewardSelectionListener = rewardSelectionListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.selectRewardImage = (ImageView) convertView.findViewById(R.id.selectRewardImage);
            holder.selectRewardName = (TextView) convertView.findViewById(R.id.selectRewardName);
            holder.selectRewardCost = (TextView) convertView.findViewById(R.id.selectRewardCost);
            holder.selectRewardCheckbox = (CheckBox) convertView.findViewById(R.id.selectRewardCheckbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Reward reward = rewardList.get(position);
        //holder.selectRewardImage.setImageURI(Uri.parse(reward.getImage()));
        holder.selectRewardName.setText(reward.getName());
        holder.selectRewardCost.setText(reward.getCost());
        holder.selectRewardCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
                rewardSelectionListener.rewardClicked(position, checked);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView selectRewardImage;
        TextView selectRewardCost;
        TextView selectRewardName;
        CheckBox selectRewardCheckbox;
    }
}