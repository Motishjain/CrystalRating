package com.admin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admin.database.Reward;
import com.admin.freddyspeaks.R;
import com.admin.tasks.FetchRewardImageTask;
import com.admin.tasks.SaveRewardImageTask;

import java.util.List;

/**
 * Created by Admin on 3/20/2016.
 */
public class SelectRewardsBoxAdapter extends RecyclerView.Adapter<SelectRewardsBoxAdapter.SelectRewardHolder> {

    private int layoutResourceId;
    private List<Reward> rewardList;
    RewardSelectionListener rewardSelectionListener;

    public SelectRewardsBoxAdapter(int layoutResourceId, List<Reward> rewardList, RewardSelectionListener rewardSelectionListener) {
        this.layoutResourceId = layoutResourceId;
        this.rewardList = rewardList;
        this.rewardSelectionListener = rewardSelectionListener;
    }


    @Override
    public SelectRewardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent,false);
        return new SelectRewardHolder(view);

    }

    @Override
    public void onBindViewHolder(final SelectRewardHolder holder, final int position) {
        final Reward reward = rewardList.get(position);

        if(reward.getImage()!=null && reward.getImage().length>0){
            FetchRewardImageTask fetchRewardImageTask = new FetchRewardImageTask(holder.selectRewardImage);
            fetchRewardImageTask.execute(reward.getImage());
        }
        else {
            FetchRewardImageTask fetchRewardImageTask = new FetchRewardImageTask(holder.selectRewardImage,reward);
            fetchRewardImageTask.execute(reward.getImage());
            SaveRewardImageTask saveRewardImageTask = new SaveRewardImageTask(holder.selectRewardImage.getContext(),reward);
            saveRewardImageTask.execute(reward);
        }

        holder.selectRewardName.setText(reward.getName());
        holder.selectRewardCost.setText("Rs."+reward.getCost());
        holder.selectRewardCheckbox.setChecked(reward.isSelected());

        holder.selectRewardCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
                reward.setSelected(checked);
                rewardSelectionListener.rewardClicked(position, checked);
            }
        });

        holder.selectRewardFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.selectRewardCheckbox.isEnabled()) {
                    boolean checked = !holder.selectRewardCheckbox.isChecked();
                    holder.selectRewardCheckbox.setChecked(checked);
                    reward.setSelected(checked);
                    rewardSelectionListener.rewardClicked(position, checked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public interface RewardSelectionListener {
        void rewardClicked(int index,boolean checked);
    }


    static class SelectRewardHolder extends RecyclerView.ViewHolder{

        ImageView selectRewardImage;
        TextView selectRewardCost;
        TextView selectRewardName;
        CheckBox selectRewardCheckbox;
        LinearLayout selectRewardFrameLayout;

        public SelectRewardHolder(View view){
            super(view);
            selectRewardImage = (ImageView) view.findViewById(R.id.selectRewardImage);
            selectRewardName = (TextView) view.findViewById(R.id.selectRewardName);
            selectRewardCost = (TextView) view.findViewById(R.id.selectRewardCost);
            selectRewardCheckbox = (CheckBox) view.findViewById(R.id.selectRewardCheckbox);
            selectRewardFrameLayout = (LinearLayout) view.findViewById(R.id.selectRewardFrameLayout);
        }
    }
}