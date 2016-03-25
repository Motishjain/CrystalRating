package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.Question;
import com.example.admin.freddyspeaks.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    TextView questionNameTextView,selectedOptionTextView;
    RatingBar ratingBar;
    String questionName;
    String[] optionValues;
    String questionType;
    String questionInputType;
    int selectedRating;

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
    public static RatingCardFragment newInstance(Question question, OnFragmentInteractionListener mListener) {
        RatingCardFragment fragment = new RatingCardFragment();
        Bundle args = new Bundle();
        fragment.questionName = question.getName();
        if(question.getRatingValues()!=null) {
            fragment.optionValues = question.getRatingValues().split(",");
        }
        fragment.questionType = question.getQuestionType();
        fragment.questionInputType = question.getQuestionInputType();
        fragment.mListener = mListener;
        fragment.setArguments(args);
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
        questionNameTextView.setText(questionName);
        ratingBar = (RatingBar) ratingCard.findViewById(R.id.ratingBar);
        ratingBar.setClickable(true);
        ratingBar.setStepSize(1);getView();
        selectedOptionTextView = (TextView) ratingCard.findViewById(R.id.selectedOptionTextView);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                selectedRating = Math.round(rating)-1;
                selectedOptionTextView.setText(optionValues[selectedRating]);
            }
        });
        return ratingCard;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


    public TextView getSelectedOptionTextView() {
        return selectedOptionTextView;
    }

    public void setSelectedOptionTextView(TextView selectedOptionTextView) {
        this.selectedOptionTextView = selectedOptionTextView;
    }

    public TextView getQuestionNameTextView() {
        return questionNameTextView;
    }

    public void setQuestionNameTextView(TextView questionNameTextView) {
        this.questionNameTextView = questionNameTextView;
    }
}
