package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UsersAutoCompleteAdapter extends ArrayAdapter<UserJson> {

    private List<UserJson> mUsers;

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((UserJson) resultValue).getUserName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<UserJson> suggestions = new ArrayList<>();
                for (UserJson customer : mUsers) {
                    if (customer.getUserName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                addAll((ArrayList<UserJson>) results.values);
            } else {
                addAll(mUsers);
            }
            notifyDataSetChanged();
        }
    };

    public UsersAutoCompleteAdapter(Context context, List<UserJson> users) {
        super(context, R.layout.item_user, users);
        this.mUsers = new ArrayList<>(users.size());
        this.mUsers.addAll(users);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserJson user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        TextView userId = (TextView) convertView.findViewById(R.id.userId);
        TextView userName = (TextView) convertView.findViewById(R.id.userName);
        userId.setText(user.getUserId());
        userName.setText(user.getUserName());
        return convertView;
    }
}
