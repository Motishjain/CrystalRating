package com.example.admin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.database.Question;
import com.example.admin.freddyspeaks.R;

/**
 * Created by Admin on 3/26/2016.
 */
public class RatingOptionsAdapter extends RecyclerView.Adapter<RatingOptionsAdapter.RatingOptionHolder>{

    private int layoutResourceId;
    private Question question;
    String optionValues[];
    String emoticonIds[];
    boolean selected[];
    int selectedOption;
    int emoticonHeight, emoticonWidth;
    float optionTextSize;
    Context context;

    public RatingOptionsAdapter(int layoutResourceId, Question question) {
        this.layoutResourceId = layoutResourceId;
        this.question = question;
        optionValues = question.getRatingValues().split(",");
        emoticonIds = question.getEmoticonIds().split(",");
        selected = new boolean[question.getRatingValues().length()];
        if(question.getSelectedOption()!=null && !question.getSelectedOption().equals("")) {
            selected[Integer.parseInt(question.getSelectedOption())-1] = true;
        }
    }

    @Override
    public RatingOptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent,false);
        RatingOptionHolder ratingOptionHolder = new RatingOptionHolder(view);
        emoticonWidth = ratingOptionHolder.ratingOptionEmoticon.getLayoutParams().width;
        emoticonHeight = ratingOptionHolder.ratingOptionEmoticon.getLayoutParams().height;
        optionTextSize = ratingOptionHolder.selectedOptionTextView.getTextSize();
        context = ratingOptionHolder.ratingOptionEmoticon.getContext();
        return ratingOptionHolder;
    }

    @Override
    public void onBindViewHolder(final RatingOptionHolder holder, final int position) {

        //TODO emoticon
        int emoticonIdResource = context.getResources().getIdentifier("emoticon_1"/*+emoticonIds[position]*/,
                "drawable", context.getPackageName());

        holder.ratingOptionEmoticon.setImageResource(emoticonIdResource);
        holder.selectedOptionTextView.setText(optionValues[position]);

        if(selected[position]) {
            holder.ratingOptionEmoticon.getLayoutParams().width = emoticonWidth + 2;
            holder.ratingOptionEmoticon.getLayoutParams().height = emoticonHeight + 2;
            holder.selectedOptionTextView.setTextSize(optionTextSize + 2);
            holder.selectedOptionTextView.setTypeface(null, Typeface.BOLD);
            LayerDrawable background = (LayerDrawable)holder.ratingOptionsLayout.getBackground();
            ((GradientDrawable)(background.findDrawableByLayerId(background.getId(0)))).setStroke(2, Color.BLACK);
        }
        else {
            holder.ratingOptionEmoticon.getLayoutParams().width = emoticonWidth;
            holder.ratingOptionEmoticon.getLayoutParams().height = emoticonHeight;
            holder.selectedOptionTextView.setTextSize(optionTextSize);
            holder.selectedOptionTextView.setTypeface(null, Typeface.NORMAL);
            LayerDrawable background = (LayerDrawable)holder.ratingOptionsLayout.getBackground();
            ((GradientDrawable)(background.findDrawableByLayerId(background.getId(0)))).setStroke(0, Color.BLACK);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected[selectedOption] = false;
                selectedOption = position;
                question.setSelectedOption((selectedOption + 1) + "");
                selected[position] = true;
                notifyDataSetChanged();
            }
        };
        holder.ratingOptionsLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return optionValues.length;
    }


    static class RatingOptionHolder extends RecyclerView.ViewHolder{

        ImageView ratingOptionEmoticon;
        TextView selectedOptionTextView;
        RelativeLayout ratingOptionsLayout;

        public RatingOptionHolder(View view){
            super(view);
            ratingOptionEmoticon = (ImageView) view.findViewById(R.id.ratingOptionEmoticon);
            selectedOptionTextView = (TextView) view.findViewById(R.id.selectedOptionTextView);
            ratingOptionsLayout = (RelativeLayout) view.findViewById(R.id.ratingOptionsLayout);
        }
    }
}
