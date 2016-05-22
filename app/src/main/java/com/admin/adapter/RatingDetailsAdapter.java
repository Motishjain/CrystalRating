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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin on 3/26/2016.
 */
public class RatingDetailsAdapter extends RecyclerView.Adapter<RatingDetailsAdapter.RatingDetailHolder>{

    private int layoutResourceId;
    List<FeedbackResponse> feedbackResponseList;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.US);

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
        ratingDetailHolder.billDateTextView.setText(simpleDateFormat.format(feedbackResponse.getCreatedDate()));
        ratingDetailHolder.userPhoneNumberCellTextView.setText(feedbackResponse.getUserPhoneNumber());
        if(feedbackResponse.getBillAmount()!=null && feedbackResponse.getBillAmount().trim().length()>0) {
            ratingDetailHolder.billAmountCellTextView.setText("Rs." + feedbackResponse.getBillAmount());
        }
        if(position%2==0) {
            ratingDetailHolder.ratingDetailLayout.setBackgroundResource(R.drawable.rating_details_item_cream_bg);
        }
        else {

        }

    }

    @Override
    public int getItemCount() {

        return feedbackResponseList.size();
    }


    static class RatingDetailHolder extends RecyclerView.ViewHolder{

        TextView billDateTextView, userPhoneNumberCellTextView, billAmountCellTextView;
        LinearLayout ratingDetailLayout;

        public RatingDetailHolder(View view){
            super(view);
            ratingDetailLayout = (LinearLayout) view.findViewById(R.id.ratingDetailLayout);
            billDateTextView = (TextView) view.findViewById(R.id.billDateTextView);
            userPhoneNumberCellTextView = (TextView) view.findViewById(R.id.userPhoneNumberCellTextView);
            billAmountCellTextView = (TextView) view.findViewById(R.id.billAmountCellTextView);

        }
    }
}
