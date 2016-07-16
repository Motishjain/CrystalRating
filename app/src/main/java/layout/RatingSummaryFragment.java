package layout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admin.dialogs.CustomDialogFragment;
import com.admin.crystalrating.R;
import com.admin.tasks.FetchAndFragmentFeedbackTask;
import com.admin.view.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link RatingSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingSummaryFragment extends Fragment implements RatingChartFragment.OnFragmentInteractionListener, CustomDialogFragment.CustomDialogListener{


    TextView fromDateTextView, toDateTextView;
    ProgressDialog progressDialog;

    Date fromDate, toDate;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    ImageView fromDateImage,toDateImage;
    CustomDialogFragment dialogDateSelectionPrompt;
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    LinearLayout serverNotReachableView, ratingCategoryFragments;
    Button tryAgainButton;

    public RatingSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment RatingSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingSummaryFragment newInstance() {
        RatingSummaryFragment fragment = new RatingSummaryFragment();
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
        View ratingSummaryFragment = inflater.inflate(R.layout.fragment_rating_summary, container, false);
        fromDateTextView = (TextView) ratingSummaryFragment.findViewById(R.id.fromDate);
        toDateTextView = (TextView) ratingSummaryFragment.findViewById(R.id.toDate);
        fromDateImage = (ImageView) ratingSummaryFragment.findViewById(R.id.fromDateImage);
        toDateImage = (ImageView) ratingSummaryFragment.findViewById(R.id.toDateImage);
        serverNotReachableView = (LinearLayout) ratingSummaryFragment.findViewById(R.id.serverNotReachableView);
        ratingCategoryFragments = (LinearLayout) ratingSummaryFragment.findViewById(R.id.ratingCategoryFragments);
        tryAgainButton = (Button) ratingSummaryFragment.findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchRatings();
            }
        });

        Date createdDate = null;
        simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        try {
            createdDate = simpleDateFormat.parse(sharedPreferences.getString("createdDate", null));
        }
        catch(ParseException e) {
            Log.e("Rating Chart","Date parse failed");
        }


        fromDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFromDate(view);
            }
        });

        toDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeToDate(view);
            }
        });

        //Set to date for feedback
        calendar = Calendar.getInstance();
        toDate = calendar.getTime();
        setDateTextView(toDateTextView, toDate);

        //Set from date for feedback
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        fromDate = calendar.getTime();

        if(fromDate.before(createdDate)) {
            fromDate = createdDate;
        }

        setDateTextView(fromDateTextView, fromDate);

        progressDialog = CustomProgressDialog.createCustomProgressDialog(this.getActivity());
        fetchRatings();
        return ratingSummaryFragment;
    }

    public void changeFromDate(View v) {
        calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        fromDatePickerDialog = new DatePickerDialog(this.getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().after(toDate)) {
                            fromDatePickerDialog.dismiss();
                            if(dialogDateSelectionPrompt==null) {
                                dialogDateSelectionPrompt = CustomDialogFragment.newInstance(R.layout.dialog_date_mismatch, RatingSummaryFragment.this, "From Date cannot be greater than To date");
                                dialogDateSelectionPrompt.show(getFragmentManager(), "");
                            }
                            return;
                        }
                        if (!fromDate.equals(calendar.getTime())) {
                            fromDate = calendar.getTime();
                            fetchRatings();
                        }
                        setDateTextView(fromDateTextView, fromDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.setTitle("From:");
        fromDatePickerDialog.show();
    }

    public void changeToDate(View v) {
        calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        toDatePickerDialog = new DatePickerDialog(this.getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().before(fromDate)) {
                            toDatePickerDialog.dismiss();
                            if(dialogDateSelectionPrompt==null) {
                                dialogDateSelectionPrompt = CustomDialogFragment.newInstance(R.layout.dialog_date_mismatch, RatingSummaryFragment.this, "To Date cannot be less than From Date");
                                dialogDateSelectionPrompt.show(getFragmentManager(), "");
                            }
                            return;
                        }
                        if (!toDate.equals(calendar.getTime())) {
                            toDate = calendar.getTime();
                            fetchRatings();
                        }
                        setDateTextView(toDateTextView, toDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog.setTitle("To:");
        toDatePickerDialog.show();
    }

    public void setDateTextView(TextView textView, Date date) {
        textView.setText(simpleDateFormat.format(date));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDialogPositiveClick() {
        dialogDateSelectionPrompt.dismiss();
        dialogDateSelectionPrompt = null;
    }

    @Override
    public void onDialogNegativeClick() {

    }

    public void fetchRatings() {
        progressDialog.show();
        FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask();
        fetchAndFragmentFeedbackTask.execute(RatingSummaryFragment.this);
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public LinearLayout getServerNotReachableView() {
        return serverNotReachableView;
    }

    public LinearLayout getRatingCategoryFragments() {
        return ratingCategoryFragments;
    }
}
