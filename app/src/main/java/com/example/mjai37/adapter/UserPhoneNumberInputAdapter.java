package com.example.mjai37.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.mjai37.database.User;
import com.example.mjai37.freddyspeaks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mjai37 on 2/27/2016.
 */
public class UserPhoneNumberInputAdapter extends ArrayAdapter<User> {

    private List<User> userList;
    private List<User> suggestions;
    private int viewResourceId;
    
    public UserPhoneNumberInputAdapter(Context context, int viewResourceId, List<User> userList){
        super(context, viewResourceId, userList);
        this.userList = userList;
        this.suggestions = new ArrayList<>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        User user = userList.get(position);
        if (user != null) {
            TextView userPhoneNameLabel = (TextView) v.findViewById(R.id.userPhoneNameLabel);
            if (userPhoneNameLabel != null) {
                userPhoneNameLabel.setText(user.getPhoneNumber()+"  "+user.getName());
            }
        }
        return v;
    }


    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((User)(resultValue)).getPhoneNumber();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (User user : userList) {
                    if(user.getPhoneNumber().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(user);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<User> filteredList = (ArrayList<User>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (User c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}
