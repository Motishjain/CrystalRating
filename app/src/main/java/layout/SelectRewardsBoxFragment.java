package layout;

import android.app.Activity;
import android.app.Fragment;
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

import com.example.admin.adapter.SelectRewardsBoxAdapter;
import com.example.admin.database.Reward;
import com.example.admin.freddyspeaks.R;

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

    private Integer selectedLevel;

    private OnFragmentInteractionListener mListener;

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
        //TODO
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View selectRewardBoxFragment =  inflater.inflate(R.layout.fragment_select_rewards_box, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(selectRewardBoxFragment.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fragmentRewardsList = (RecyclerView) selectRewardBoxFragment.findViewById(R.id.fragmentRewardsList);
        fragmentRewardsList.setLayoutManager(layoutManager);
        selectRewardsBoxAdapter = new SelectRewardsBoxAdapter(R.layout.select_reward_item, rewardList,this,selectedLevel);
        fragmentRewardsList.setAdapter(selectRewardsBoxAdapter);
        return selectRewardBoxFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
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
    }

    public Integer getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(Integer selectedLevel) {
        this.selectedLevel = selectedLevel;
        selectRewardsBoxAdapter.setSelectedLevel(selectedLevel);
        selectRewardsBoxAdapter.notifyDataSetChanged();
    }
}
