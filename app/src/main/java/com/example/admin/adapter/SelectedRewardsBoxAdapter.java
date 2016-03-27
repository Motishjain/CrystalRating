package com.example.admin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.database.Reward;
import com.example.admin.database.SelectedReward;
import com.example.admin.freddyspeaks.R;
import com.example.admin.tasks.FetchRewardImageTask;

import java.util.List;

/**
 * Created by Admin on 3/20/2016.
 */
public class SelectedRewardsBoxAdapter extends RecyclerView.Adapter<SelectedRewardsBoxAdapter.SelectedRewardHolder> {

    private int layoutResourceId;
    private List<SelectedReward> selectedRewardList;
    private OnAdapterInteractionListener onAdapterInteractionListener;

    public SelectedRewardsBoxAdapter(int layoutResourceId, List<SelectedReward> selectedRewardList, OnAdapterInteractionListener onAdapterInteractionListener) {
        this.layoutResourceId = layoutResourceId;
        this.selectedRewardList = selectedRewardList;
        this.onAdapterInteractionListener = onAdapterInteractionListener;
    }


    @Override
    public SelectedRewardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, null);
        return new SelectedRewardHolder(view);

    }

    @Override
    public void onBindViewHolder(SelectedRewardHolder holder, final int position) {
        final Reward reward = selectedRewardList.get(position).getReward();

        FetchRewardImageTask fetchRewardImageTask = new FetchRewardImageTask(holder.selectedRewardImage);
        fetchRewardImageTask.execute(reward.getImage());

        holder.selectedRewardName.setText(reward.getName());
        holder.selectedRewardCost.setText(reward.getCost());
        holder.selectedRewardDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRewardList.remove(position);
                notifyDataSetChanged();
                onAdapterInteractionListener.deleteButtonClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedRewardList.size();
    }


    static class SelectedRewardHolder extends RecyclerView.ViewHolder{

        ImageView selectedRewardImage;
        TextView selectedRewardCost;
        TextView selectedRewardName;
        ImageButton selectedRewardDeleteButton;

        public SelectedRewardHolder(View view){
            super(view);
            selectedRewardImage = (ImageView) view.findViewById(R.id.selectedRewardImage);
            selectedRewardName = (TextView) view.findViewById(R.id.selectedRewardName);
            selectedRewardCost = (TextView) view.findViewById(R.id.selectedRewardCost);
            selectedRewardDeleteButton = (ImageButton) view.findViewById(R.id.selectedRewardDeleteButton);
        }
    }


    public List<SelectedReward> getSelectedRewardList() {
        return selectedRewardList;
    }

    public void setSelectedRewardList(List<SelectedReward> selectedRewardList) {
        this.selectedRewardList = selectedRewardList;
    }

    public interface OnAdapterInteractionListener {
        // TODO: Update argument type and name
        void deleteButtonClicked(int position);
    }
}