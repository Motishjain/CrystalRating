package com.admin.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.admin.adapter.RatingDetailsAdapter;
import com.admin.freddyspeaks.R;
import com.admin.webservice.response_objects.FeedbackResponse;

import java.util.List;

/**
 * Created by verona1024.
 */
public class CustomPieChartDialog extends DialogFragment {

    private List<FeedbackResponse> feedbackResponseList;
    private int layoutResourceId;

    public static CustomPieChartDialog newInstance(int layoutResourceId, List<FeedbackResponse> feedbackResponseList) {
        CustomPieChartDialog fragment = new CustomPieChartDialog();

        fragment.feedbackResponseList = feedbackResponseList;
        fragment.layoutResourceId = layoutResourceId;

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(layoutResourceId, null);

        RecyclerView ratingDetailsRecyclerView = (RecyclerView) dialog.findViewById(R.id.ratingDetailsRecyclerView);
        TextView ratingDetailsCloseButton = (TextView) dialog.findViewById(R.id.ratingDetailsCloseButton);

        RatingDetailsAdapter ratingOptionsAdapter = new RatingDetailsAdapter(R.layout.rating_detail_item, feedbackResponseList);
        ratingDetailsRecyclerView.setAdapter(ratingOptionsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ratingDetailsRecyclerView.setLayoutManager(layoutManager);
        ratingDetailsCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Inflate and set the layout for the dialog
        builder.setView(dialog);

        return builder.create();
    }
}
