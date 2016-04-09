package com.example.admin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.database.Reward;
import com.example.admin.freddyspeaks.R;
import com.example.admin.tasks.FetchRewardImageTask;
import com.example.admin.tasks.SaveRewardImageTask;

import java.util.List;

/**
 * Created by Admin on 3/20/2016.
 */
public class SelectRewardsBoxAdapter extends RecyclerView.Adapter<SelectRewardsBoxAdapter.SelectRewardHolder> {

    private int layoutResourceId;
    private List<Reward> rewardList;
    RewardSelectionListener rewardSelectionListener;
    private int selectedLevel;

    public SelectRewardsBoxAdapter(int layoutResourceId, List<Reward> rewardList, RewardSelectionListener rewardSelectionListener ,int selectedLevel) {
        this.layoutResourceId = layoutResourceId;
        this.rewardList = rewardList;
        this.rewardSelectionListener = rewardSelectionListener;
        this.selectedLevel = selectedLevel;
    }


    @Override
    public SelectRewardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent,false);
        return new SelectRewardHolder(view);

    }

    @Override
    public void onBindViewHolder(SelectRewardHolder holder, final int position) {
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
        holder.selectRewardCost.setText(reward.getCost());
        holder.selectRewardCheckbox.setChecked(reward.isSelected());

        if(selectedLevel!=0 && selectedLevel!=reward.getLevel()){
            holder.selectRewardCheckbox.setEnabled(false);
            holder.selectRewardFrameLayout.setAlpha(0.5f);
        }
        else {
            holder.selectRewardCheckbox.setEnabled(true);
            holder.selectRewardFrameLayout.setAlpha(1.0f);
        }

        holder.selectRewardCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                reward.setSelected(checked);
                rewardSelectionListener.rewardClicked(position, checked);
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
        FrameLayout selectRewardFrameLayout;

        public SelectRewardHolder(View view){
            super(view);
            selectRewardImage = (ImageView) view.findViewById(R.id.selectRewardImage);
            selectRewardName = (TextView) view.findViewById(R.id.selectRewardName);
            selectRewardCost = (TextView) view.findViewById(R.id.selectRewardCost);
            selectRewardCheckbox = (CheckBox) view.findViewById(R.id.selectRewardCheckbox);
            selectRewardFrameLayout = (FrameLayout) view.findViewById(R.id.selectRewardFrameLayout);
        }
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }
}