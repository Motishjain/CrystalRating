package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.admin.adapter.RatingOptionsAdapter;
import com.admin.constants.AppConstants;
import com.admin.database.Question;
import com.admin.freddyspeaks.R;

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
    TextView questionNameTextView, questionNumberTextView;
    Question question;
    RecyclerView ratingOptionsRecyclerView;
    RatingBar ratingBar;
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
        ratingOptionsRecyclerView = (RecyclerView) ratingCard.findViewById(R.id.ratingOptionsRecyclerView);
        ratingBar = (RatingBar) ratingCard.findViewById(R.id.ratingBar);
        questionNameTextView.setText(question.getName());
        questionNumberTextView.setText("#" + questionNumber + " of " + totalQuestions);

        if(question.getQuestionInputType()!=null && question.getQuestionInputType().equals(AppConstants.STAR_RATING)) {
            ratingOptionsRecyclerView.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setNumStars(question.getRatingValues().split(",").length);
            ratingBar.setMax(question.getRatingValues().split(",").length);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    question.setSelectedOption(((int)v)+"");
                    mListener.onQuestionAnswered();
                }
            });
        }

        else {
            ratingBar.setVisibility(View.GONE);
            ratingOptionsRecyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(ratingCard.getContext(), LinearLayoutManager.VERTICAL, false);
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
