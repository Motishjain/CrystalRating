package com.admin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admin.database.Question;
import com.admin.freddyspeaks.R;
import com.admin.webservice.response_objects.FeedbackResponse;

import java.util.List;

/**
 * Created by Admin on 3/26/2016.
 */
public class RatingDetailsAdapter extends RecyclerView.Adapter<RatingDetailsAdapter.RatingDetailHolder>{

    private int layoutResourceId;
    List<FeedbackResponse> feedbackResponseList;

    public RatingDetailsAdapter(int layoutResourceId, List<FeedbackResponse> feedbackResponseList) {
        this.layoutResourceId = layoutResourceId;
        this.feedbackResponseList = feedbackResponseList;
    }

    @Override
    public RatingDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        RatingDetailHolder ratingDetailHolder = new RatingDetailHolder(view);
        return ratingDetailHolder;
    }

    @Override
    public void onBindViewHolder(final RatingDetailHolder ratingDetailHolder, final int position) {
        FeedbackResponse feedbackResponse = feedbackResponseList.get(position);
        ratingDetailHolder.userNameCellTextView.setText(feedbackResponse.getUserName());
        ratingDetailHolder.userPhoneNumberCellTextView.setText(feedbackResponse.getUserPhoneNumber());
        ratingDetailHolder.billNumberCellTextView.setText(feedbackResponse.getBillNumber());
        ratingDetailHolder.billAmountCellTextView.setText(feedbackResponse.getBillAmount());

    }

    @Override
    public int getItemCount() {

        return feedbackResponseList.size();
    }


    static class RatingDetailHolder extends RecyclerView.ViewHolder{

        TextView userNameCellTextView, userPhoneNumberCellTextView, billNumberCellTextView, billAmountCellTextView;

        public RatingDetailHolder(View view){
            super(view);
            userNameCellTextView = (TextView) view.findViewById(R.id.userNameCellTextView);
            userPhoneNumberCellTextView = (TextView) view.findViewById(R.id.userPhoneNumberCellTextView);
            billNumberCellTextView = (TextView) view.findViewById(R.id.billNumberCellTextView);
            billAmountCellTextView = (TextView) view.findViewById(R.id.billAmountCellTextView);

        }
    }
}
