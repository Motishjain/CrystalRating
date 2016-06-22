package layout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.admin.database.Question;
import com.admin.dialogs.CustomPieChartDialog;
import com.admin.freddyspeaks.R;
import com.admin.webservice.response_objects.FeedbackResponse;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingChartFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    String header;
    double averageRating;
    List<Question> questionList;
    boolean isExpanded;
    LinearLayout chartContainer,chartHeaderContainer;

    /*Map to save ratings for each question id (String). In turn, for every question id there is a map
     storing list of feedbackId indexes for each ratingOption*/
    Map<String, Map<Integer, List<Integer>>> questionWiseRatingFeedbackIndexList;
    Spinner questionsSpinner;
    PieChart ratingSummaryChart;
    TextView ratingChartHeader, ratingValue;
    Question selectedQuestion;
    List<FeedbackResponse> feedbackResponseList;
    View colorStrip;
    ImageView arrow;
    private CustomPieChartDialog dialogInfo;

    public RatingChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RatingChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingChartFragment newInstance(String header, double averageRating, List<Question> questionList, List<FeedbackResponse> feedbackResponseList, Map<String, Map<Integer, List<Integer>>> questionWiseRatingFeedbackIndexList) {
        RatingChartFragment fragment = new RatingChartFragment();
        fragment.header = header;
        fragment.averageRating = averageRating;
        fragment.questionList = questionList;
        fragment.feedbackResponseList = feedbackResponseList;
        fragment.questionWiseRatingFeedbackIndexList = questionWiseRatingFeedbackIndexList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ratingChartFragment = inflater.inflate(R.layout.fragment_rating_chart, container, false);
        ratingSummaryChart = (PieChart) ratingChartFragment.findViewById(R.id.ratingSummaryChart);
        ratingChartHeader = (TextView) ratingChartFragment.findViewById(R.id.ratingChartHeader);
        chartHeaderContainer = (LinearLayout) ratingChartFragment.findViewById(R.id.chartHeaderContainer);
        chartContainer = (LinearLayout) ratingChartFragment.findViewById(R.id.chartContainer);
        ratingValue = (TextView) ratingChartFragment.findViewById(R.id.ratingValue);
        questionsSpinner = (Spinner) ratingChartFragment.findViewById(R.id.questionsSpinner);
        colorStrip = ratingChartFragment.findViewById(R.id.ratingLeftLine);
        arrow = (ImageView) ratingChartFragment.findViewById(R.id.imageViewDropdown);

        chartHeaderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandCollapseChart(chartContainer);
            }
        });

        ratingSummaryChart.setNoDataText("No ratings found for selected question");
        ratingSummaryChart.setDrawHoleEnabled(false);
        ratingSummaryChart.setUsePercentValues(true);
        ratingSummaryChart.setDrawSliceText(false);
        ratingSummaryChart.setDescription("");

        ratingChartHeader.setText(header);
        ratingValue.setText(round(averageRating,2)+"");

        // After all preparings set color according pie
        ratingValue.setTextColor(getAverageStringColor(round(averageRating,2)));
        colorStrip.setBackgroundColor(getAverageStringColor(round(averageRating,2)));

        Legend legend = ratingSummaryChart.getLegend();
        legend.setTextSize(15);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        legend.setWordWrapEnabled(true);

        ratingSummaryChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int optionIndex = e.getXIndex();
                List<Integer> feedbackIndexList = questionWiseRatingFeedbackIndexList.get(selectedQuestion.getQuestionId()).get(optionIndex + 1);
                List<FeedbackResponse> feedbackResponseSubList = new ArrayList<>();
                for (Integer feedbackIndex : feedbackIndexList) {
                    feedbackResponseSubList.add(feedbackResponseList.get(feedbackIndex));
                }
                openRatingDetailsDialog(feedbackResponseSubList);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        List<String> questionNames = new ArrayList<>();

        for (Question question : questionList) {
            questionNames.add(question.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, questionNames);
        questionsSpinner.setAdapter(dataAdapter);

        questionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Rating summary", "Item selected");
                selectedQuestion = questionList.get(position);
                refreshPieChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Rating summary", "Nothing selected");
            }
        });
        questionsSpinner.setSelected(false);

        return ratingChartFragment;
    }

    /**
     * Get color of the average.
     * @return
     */
    public int getAverageStringColor(double average){
        if (0 <= average && average <= 1){
            return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating1);
        }

        if (1 < average && average <= 2){
            return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating2);
        }

        if (2 < average && average <= 3){
            return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating3);
        }

        if (3 < average && average <= 4){
            return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating4);
        }

        if (4 < average && average <= 5){
            return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating5);
        }

        // Default value
        return ContextCompat.getColor(getActivity().getApplicationContext(),R.color.rating4);
    }

    public void refreshPieChart() {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String[] options = selectedQuestion.getRatingValues().split(";");
        Map<Integer, List<Integer>> ratingWiseFeedbackList = questionWiseRatingFeedbackIndexList.get(selectedQuestion.getQuestionId());

        for (String option : options) {
            labels.add(option);
        }

        for (int optionIndex = 0; optionIndex < options.length; optionIndex++) {
            Integer count = ratingWiseFeedbackList.get(optionIndex + 1) == null ? 0 : ratingWiseFeedbackList.get(optionIndex + 1).size();
            entries.add(new Entry(count, optionIndex));
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(new int[]{R.color.rating1, R.color.rating2, R.color.rating3, R.color.rating4, R.color.rating5}, getActivity().getApplicationContext());
        dataset.setValueTextSize(12);
        dataset.setSliceSpace(1.0f);
        PieData data = new PieData(labels, dataset);

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int intValue = (int) value;
                if(intValue>0)
                {
                    return intValue + "%";
                }
                else {
                    return "";
                }
            }
        });
        ratingSummaryChart.setData(data);
        ratingSummaryChart.invalidate();
    }


    void openRatingDetailsDialog(List<FeedbackResponse> feedbackResponseSubList) {
        dialogInfo = CustomPieChartDialog.newInstance(R.layout.rating_details_popup, feedbackResponseSubList);
        dialogInfo.show(getActivity().getSupportFragmentManager(), "");
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void expandCollapseChart(View v){
        if(isExpanded) {
            collapse(v);
            isExpanded = false;

            arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
        }
        else {
            expand(v);
            isExpanded = true;

            arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
