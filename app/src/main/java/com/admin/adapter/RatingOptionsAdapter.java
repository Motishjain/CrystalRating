package com.admin.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admin.database.Question;
import com.admin.freddyspeaks.R;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.util.HashMap;
import java.util.Map;

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
    int selectedOptionBgColor, unSelectedOptionBgColor;
    RatingOptionsAdapter.OnItemSelectedListener onItemSelectedListener;
    Map<String, String> emotionsMap;


    public RatingOptionsAdapter(int layoutResourceId, Question question, RatingOptionsAdapter.OnItemSelectedListener onItemSelectedListener) {
        this.layoutResourceId = layoutResourceId;
        this.question = question;
        this.onItemSelectedListener = onItemSelectedListener;
        optionValues = question.getRatingValues().split(",");
        emoticonIds = question.getEmoticonIds().split(",");
        selected = new boolean[optionValues.length];
        emotionsMap=new HashMap<>();

        emotionsMap.put("very_satisfied","\uE106");
        emotionsMap.put("satisfied","\uE056");
        emotionsMap.put("neutral","\uD83D\uDE10");
        emotionsMap.put("dissatisfied","\uE40E");
        emotionsMap.put("very_dissatisfied","\uE058");

        if(question.getSelectedOption()!=null && !question.getSelectedOption().equals("")) {
            selectedOption = Integer.parseInt(question.getSelectedOption())-1;
            selected[selectedOption] = true;
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
        selectedOptionBgColor = ContextCompat.getColor(context.getApplicationContext(),R.color.selected_option_bg);
        unSelectedOptionBgColor = ContextCompat.getColor(context.getApplicationContext(),R.color.unselected_option_bg);
        return ratingOptionHolder;
    }

    @Override
    public void onBindViewHolder(final RatingOptionHolder holder, final int position) {

        holder.ratingOptionEmoticon.setText(emotionsMap.get(emoticonIds[position]));
        holder.selectedOptionTextView.setText(optionValues[position]);

        if(selected[position]) {
            holder.ratingOptionEmoticon.getLayoutParams().width = emoticonWidth + 2;
            holder.ratingOptionEmoticon.getLayoutParams().height = emoticonHeight + 2;
            holder.selectedOptionTextView.setTypeface(null, Typeface.BOLD);
            LayerDrawable background = (LayerDrawable)holder.ratingOptionsLayout.getBackground();
            ((GradientDrawable)(background.findDrawableByLayerId(background.getId(0)))).setColor(selectedOptionBgColor);
        }
        else {
            holder.ratingOptionEmoticon.getLayoutParams().width = emoticonWidth;
            holder.ratingOptionEmoticon.getLayoutParams().height = emoticonHeight;
            holder.selectedOptionTextView.setTypeface(null, Typeface.NORMAL);
            LayerDrawable background = (LayerDrawable)holder.ratingOptionsLayout.getBackground();
            ((GradientDrawable)(background.findDrawableByLayerId(background.getId(0)))).setColor(unSelectedOptionBgColor);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected[selectedOption] = false;
                selectedOption = position;
                question.setSelectedOption((selectedOption + 1) + "");
                selected[position] = true;
                notifyDataSetChanged();
                onItemSelectedListener.onItemSelected(position);
            }
        };
        holder.ratingOptionsLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return optionValues.length;
    }


    static class RatingOptionHolder extends RecyclerView.ViewHolder{

        EmojiconTextView ratingOptionEmoticon;
        TextView selectedOptionTextView;
        LinearLayout ratingOptionsLayout;

        public RatingOptionHolder(View view){
            super(view);
            ratingOptionEmoticon= (EmojiconTextView) view.findViewById(R.id.txtEmojicon);
            selectedOptionTextView = (TextView) view.findViewById(R.id.selectedOptionTextView);
            ratingOptionsLayout = (LinearLayout) view.findViewById(R.id.ratingOptionsLayout);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }
}
