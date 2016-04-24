package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.freddyspeaks.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link SalesReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalesReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    public SalesReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SalesReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SalesReportFragment newInstance(String param1, String param2) {
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
        return salesReportFragment;
    }

}
