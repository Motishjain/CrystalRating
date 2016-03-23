package com.example.admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, null);

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
        }

        holder.selectRewardName.setText(reward.getName());
        holder.selectRewardCost.setText(reward.getCost());
        holder.selectRewardCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox)v).isChecked();
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

        public SelectRewardHolder(View view){
            super(view);
            selectRewardImage = (ImageView) view.findViewById(R.id.selectRewardImage);
            selectRewardName = (TextView) view.findViewById(R.id.selectRewardName);
            selectRewardCost = (TextView) view.findViewById(R.id.selectRewardCost);
            selectRewardCheckbox = (CheckBox) view.findViewById(R.id.selectRewardCheckbox);
        }
    }
}