package layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.admin.adapter.SelectRewardsBoxAdapter;
import com.admin.database.Reward;
import com.admin.freddyspeaks.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectRewardsBoxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectRewardsBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectRewardsBoxFragment extends Fragment implements SelectRewardsBoxAdapter.RewardSelectionListener {

    // TODO: Rename and change types of parameters
    private List<Reward> rewardList;

    private int level;

    private OnFragmentInteractionListener mListener;

    private TextView rewardsLevelHeader, rewardsLevelSubHeader;
    private RecyclerView fragmentRewardsList;

    private SelectRewardsBoxAdapter selectRewardsBoxAdapter;

    public SelectRewardsBoxFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * rewardList: Having list of rewards for a particular level
     * @return A new instance of fragment SelectRewardsBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectRewardsBoxFragment newInstance(int level,List<Reward> rewardList) {
        SelectRewardsBoxFragment fragment = new SelectRewardsBoxFragment();
        fragment.level = level;
        fragment.rewardList = rewardList;
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
        View selectRewardBoxFragment =  inflater.inflate(R.layout.fragment_select_rewards_box, container, false);
        fragmentRewardsList = (RecyclerView) selectRewardBoxFragment.findViewById(R.id.fragmentRewardsList);
        rewardsLevelHeader = (TextView) selectRewardBoxFragment.findViewById(R.id.rewardsLevelHeader);
        rewardsLevelSubHeader = (TextView) selectRewardBoxFragment.findViewById(R.id.rewardsLevelSubHeader);

        setLevelHeader(level);

        LinearLayoutManager layoutManager = new LinearLayoutManager(selectRewardBoxFragment.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fragmentRewardsList.setLayoutManager(layoutManager);
        selectRewardsBoxAdapter = new SelectRewardsBoxAdapter(R.layout.select_reward_item, rewardList,this);
        fragmentRewardsList.setAdapter(selectRewardsBoxAdapter);
        return selectRewardBoxFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.fragmentCreated(level);
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void rewardClicked(int index,boolean checked) {
        mListener.rewardClicked(level,index,checked);
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
        void rewardClicked(int level, int index, boolean checked);

        void fragmentCreated(int level);
    }

    private void setLevelHeader(int level) {
        rewardsLevelHeader.setText("Level "+this.level+" rewards ");
        int lowerRange = 0, upperRange = 0;
        switch (level) {
            case 1:
                lowerRange = 10;
                break;
            case 2:
                lowerRange = 20;
                upperRange = 40;
                break;
            case 3:
                lowerRange = 60;
                upperRange = 80;
                break;
        }
        String subHeader;
        if(upperRange==0) {
            subHeader = "(Rs."+lowerRange+")";
        }
        else {
            subHeader = "(Rs."+lowerRange+" to Rs."+upperRange+")";
        }
        rewardsLevelHeader.setText("Level "+this.level+" rewards ");
        rewardsLevelSubHeader.setText(subHeader);
    }

}
