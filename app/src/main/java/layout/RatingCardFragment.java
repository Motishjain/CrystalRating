package layout;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mjai37.constants.AppConstants;
import com.example.mjai37.freddyspeaks.R;

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
    TextView questionText,selectedOptionValue;
    RatingBar ratingBar;
    String questionName;
    String[] optionValues;

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
    public static RatingCardFragment newInstance(String questionName, String[] optionValues) {
        RatingCardFragment fragment = new RatingCardFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.QUESTION_NAME, questionName);
        args.putStringArray(AppConstants.OPTION_VALUES, optionValues);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionName = getArguments().getString(AppConstants.QUESTION_NAME);
            optionValues = getArguments().getStringArray(AppConstants.OPTION_VALUES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        questionText = (TextView) container.findViewById(R.id.questionText);
        ratingBar = (RatingBar) container.findViewById(R.id.ratingBar);
        ratingBar.setClickable(true);
        ratingBar.setStepSize(1);
        selectedOptionValue = (TextView) container.findViewById(R.id.selectedOptionValue);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                selectedOptionValue.setText(optionValues[Math.round(rating)]);
            }
        });
        return inflater.inflate(R.layout.fragment_rating_card, container, false);
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
}
