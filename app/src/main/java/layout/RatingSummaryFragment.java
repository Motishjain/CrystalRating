package layout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.admin.freddyspeaks.R;
import com.admin.tasks.FetchAndFragmentFeedbackTask;
import com.admin.view.CustomProgressDialog;

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
public class RatingSummaryFragment extends Fragment implements RatingChartFragment.OnFragmentInteractionListener{


    TextView fromDateTextView, toDateTextView;
    ProgressDialog progressDialog;

    Date fromDate, toDate;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;


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

        simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        //Set to date for feedback
        calendar = Calendar.getInstance();
        toDate = calendar.getTime();
        setDateTextView(toDateTextView, toDate);

        //Set from date for feedback
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        fromDate = calendar.getTime();
        setDateTextView(fromDateTextView, fromDate);

        progressDialog = CustomProgressDialog.createCustomProgressDialog(this.getActivity());
        progressDialog.show();
        FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate, toDate, progressDialog);
        fetchAndFragmentFeedbackTask.execute(this);
        return ratingSummaryFragment;
    }

    public void changeFromDate(View v) {
        calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this.getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().after(toDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if (!fromDate.equals(calendar.getTime())) {
                            fromDate = calendar.getTime();
                            FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate,toDate, progressDialog);
                            fetchAndFragmentFeedbackTask.execute(RatingSummaryFragment.this);
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
        DatePickerDialog toDatePickerDialog = new DatePickerDialog(this.getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().before(fromDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if (!toDate.equals(calendar.getTime())) {
                            toDate = calendar.getTime();
                            FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate,toDate, progressDialog);
                            fetchAndFragmentFeedbackTask.execute(RatingSummaryFragment.this);
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
}
