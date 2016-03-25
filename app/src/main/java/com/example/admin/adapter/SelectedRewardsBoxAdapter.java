package com.example.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.database.Reward;
import com.example.admin.freddyspeaks.R;
import com.example.admin.tasks.FetchRewardImageTask;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 3/20/2016.
 */
public class SelectedRewardsBoxAdapter extends RecyclerView.Adapter<SelectedRewardsBoxAdapter.SelectedRewardHolder> {

    private int layoutResourceId;
    private List<Reward> rewardList;
    private OnAdapterInteractionListener onAdapterInteractionListener;

    public SelectedRewardsBoxAdapter(int layoutResourceId, List<Reward> rewardList, OnAdapterInteractionListener onAdapterInteractionListener) {
        this.layoutResourceId = layoutResourceId;
        this.rewardList = rewardList;
        this.onAdapterInteractionListener = onAdapterInteractionListener;
    }


    @Override
    public SelectedRewardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, null);
        return new SelectedRewardHolder(view);

    }

    @Override
    public void onBindViewHolder(SelectedRewardHolder holder, final int position) {
        final Reward reward = rewardList.get(position);

        FetchRewardImageTask fetchRewardImageTask = new FetchRewardImageTask(holder.selectedRewardImage);
        fetchRewardImageTask.execute(reward.getImage());

        holder.selectedRewardName.setText(reward.getName());
        holder.selectedRewardCost.setText(reward.getCost());
        holder.selectedRewardDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardList.remove(position);
                notifyDataSetChanged();
                onAdapterInteractionListener.deleteButtonClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
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

    public List<Reward> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<Reward> rewardList) {
        this.rewardList = rewardList;
    }

    public interface OnAdapterInteractionListener {
        // TODO: Update argument type and name
        void deleteButtonClicked(int position);
    }
}