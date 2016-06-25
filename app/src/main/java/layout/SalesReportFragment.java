package layout;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.admin.freddyspeaks.R;
import com.admin.view.CustomProgressDialog;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.response_objects.DailySaleResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link SalesReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalesReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    LineChart salesReportChart;
    Spinner monthSpinner, yearSpinner;
    List<String> monthsList, yearsList;
    ProgressDialog progressDialog;
    List<DailySaleResponse> monthlySalesList;
    SimpleDateFormat simpleDateFormat;

    public static String TAG = "SalesReport";

    public SalesReportFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SalesReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SalesReportFragment newInstance() {
        SalesReportFragment fragment = new SalesReportFragment();
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
        View salesReportFragment = inflater.inflate(R.layout.fragment_sales_report, container, false);
        salesReportChart = (LineChart) salesReportFragment.findViewById(R.id.salesReportChart);
        monthSpinner = (Spinner) salesReportFragment.findViewById(R.id.monthSpinner);
        yearSpinner = (Spinner) salesReportFragment.findViewById(R.id.yearSpinner);
        progressDialog = CustomProgressDialog.createCustomProgressDialog(this.getActivity());

        Calendar gc = Calendar.getInstance();

        monthsList = new ArrayList<>();
        monthsList.addAll(Arrays.asList(new DateFormatSymbols().getMonths()));

        yearsList = getArrayListYears();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                R.layout.component_spinner_sales_reports, monthsList);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Rating summary", "Item selected");
                progressDialog.show();
                fetchSalesData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Rating summary", "Nothing selected");
            }
        });
        monthSpinner.setSelection(gc.get(Calendar.MONTH));

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(getContext(),
                R.layout.component_spinner_sales_reports, yearsList);
        yearSpinner.setAdapter(yearsAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Rating summary", "Item selected");
                progressDialog.show();
                fetchSalesData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Rating summary", "Nothing selected");
            }
        });
        yearSpinner.setSelection(gc.get(Calendar.YEAR) - 2000);

        progressDialog.show();
        fetchSalesData();
        return salesReportFragment;
    }

    public void fetchSalesData() {
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String outletCode = sharedPreferences.getString("outletCode", null);

        Call<List<DailySaleResponse>> fetchSalesDataCall = restEndpointInterface.fetchSalesData(outletCode, Calendar.getInstance().get(Calendar.YEAR) + "", monthSpinner.getSelectedItemPosition()+"");
        fetchSalesDataCall.enqueue(new Callback<List<DailySaleResponse>>() {
            @Override
            public void onResponse(Call<List<DailySaleResponse>> call, Response<List<DailySaleResponse>> response) {
                if (response.isSuccess()) {
                    monthlySalesList = response.body();
                    refreshLineChart();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<DailySaleResponse>> call, Throwable t) {
                Log.e("RatingSummary", "Unable to fetch feedback", t);
                progressDialog.dismiss();
            }
        });
    }

    public void refreshLineChart() {

        //To store day of month, total sales information
        Map<Integer, Double> salesMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        for (DailySaleResponse dailySaleResponse : monthlySalesList) {
            salesMap.put(dailySaleResponse.getDayOfMonth(), Double.parseDouble(dailySaleResponse.getTotalSale()));
        }

        calendar.set(Calendar.MONTH, monthSpinner.getSelectedItemPosition());

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        String[] dayLabels = new String[]{"", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th",
                "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
                "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th", "31st"};

        for (int day = 1; day < calendar.getMaximum(Calendar.DAY_OF_MONTH); day++) {
            entries.add(new Entry(salesMap.get(day) != null ? salesMap.get(day).floatValue() : 0, day - 1));
            labels.add(dayLabels[day - 1]);
        }

        LineDataSet dataset = new LineDataSet(entries, "Sales Amount");

        Legend legend = salesReportChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = salesReportChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setAxisLineWidth(2);
        xAxis.setLabelsToSkip(5);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#282D43"));

        YAxis yAxisLeft = salesReportChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setAxisLineColor(Color.parseColor("#282D43"));

        YAxis yAxisRight = salesReportChart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawTopYLabelEntry(false);
        yAxisRight.setDrawLabels(false);

        LineData data = new LineData(labels, dataset);

        data.setHighlightEnabled(false);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(value>0) {
                    return value+"";
                }
                else {
                    return "";
                }
            }
        });
        salesReportChart.setData(data);
        salesReportChart.invalidate();
    }

    private ArrayList<String> getArrayListYears(){
        ArrayList<String> years = new ArrayList<>();
        for (int i = 2000; i <= 2030; i++){
            years.add("" + i);
        }

        return years;
    }
}