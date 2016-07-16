package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.admin.adapter.RatingOptionsAdapter;
import com.admin.constants.AppConstants;
import com.admin.database.Question;
import com.admin.crystalrating.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingCardFragment extends Fragment implements RatingOptionsAdapter.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    TextView questionNameTextView, questionNumberTextView, selectedRatingTextView;
    Question question;
    RecyclerView ratingOptionsRecyclerView;
    RatingBar ratingBar;
    LinearLayout ratingOptionsLinearLayout, ratingBarLinearLayout;
    int questionNumber, totalQuestions;

    private OnFragmentInteractionListener mListener;

    public RatingCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RatingCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingCardFragment newInstance(int questionNumber, Question question, OnFragmentInteractionListener mListener, int totalQuestions) {
        RatingCardFragment fragment = new RatingCardFragment();
        fragment.question = question;
        fragment.mListener = mListener;
        fragment.questionNumber = questionNumber;
        fragment.totalQuestions = totalQuestions;
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
        View ratingCard = inflater.inflate(R.layout.fragment_rating_card, container, false);
        questionNameTextView = (TextView) ratingCard.findViewById(R.id.questionNameTextView);
        questionNumberTextView = (TextView) ratingCard.findViewById(R.id.questionNumberTextView);
        selectedRatingTextView = (TextView) ratingCard.findViewById(R.id.selectedRatingTextView);
        ratingOptionsRecyclerView = (RecyclerView) ratingCard.findViewById(R.id.ratingOptionsRecyclerView);
        ratingOptionsLinearLayout = (LinearLayout) ratingCard.findViewById(R.id.ratingOptionsLinearLayout);
        ratingBarLinearLayout = (LinearLayout) ratingCard.findViewById(R.id.ratingBarLinearLayout);
        ratingBar = (RatingBar) ratingCard.findViewById(R.id.ratingBar);
        questionNameTextView.setText(question.getName());
        questionNumberTextView.setText("#" + questionNumber + " of " + totalQuestions);

        if(question.getQuestionInputType()!=null && question.getQuestionInputType().equals(AppConstants.STAR_RATING)) {
            ratingOptionsLinearLayout.setVisibility(View.GONE);
            ratingBarLinearLayout.setVisibility(View.VISIBLE);
            final String[] ratingValues = question.getRatingValues().split(";");
            ratingBar.setNumStars(ratingValues.length);
            ratingBar.setMax(ratingValues.length);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {
                    if(fromUser) {
                        question.setSelectedOption(((int)v)+"");
                        selectedRatingTextView.setText(ratingValues[((int)v)-1]);
                        mListener.onQuestionAnswered();
                    }
                }
            });
        }

        else {
            ratingBarLinearLayout.setVisibility(View.GONE);
            ratingOptionsLinearLayout.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ratingCard.getContext(), LinearLayoutManager.VERTICAL, false);
            if(question.getQuestionInputType().equals(AppConstants.OPTION_RATING)) {
                layoutManager.setReverseLayout(true);
            }
            ratingOptionsRecyclerView.setLayoutManager(layoutManager);

            if (ratingOptionsRecyclerView.getAdapter() == null) {
                RatingOptionsAdapter ratingOptionsAdapter = new RatingOptionsAdapter(R.layout.rating_option_item, question, this);
                ratingOptionsRecyclerView.setAdapter(ratingOptionsAdapter);
            }
        }

        return ratingCard;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(int position) {
        mListener.onQuestionAnswered();
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
        void onQuestionAnswered();
    }
}
