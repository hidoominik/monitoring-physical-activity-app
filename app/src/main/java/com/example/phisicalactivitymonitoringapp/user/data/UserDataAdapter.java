package com.example.phisicalactivitymonitoringapp.user.data;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.model.User;

import java.util.ArrayList;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder> {

    private ArrayList<User> userModelArrayList;

    public UserDataAdapter(ArrayList<User> userModelArrayList, Context context) {
        this.userModelArrayList = userModelArrayList;
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<User> filterlist) {
        userModelArrayList = filterlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDataAdapter.ViewHolder holder, int position) {
        User model = userModelArrayList.get(position);
        holder.usernameTextView.setText(model.getUsername());
        holder.userDescriptionTextView.setText(model.getEmail());
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView userDescriptionTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userListUsernameTextView);
            userDescriptionTextView = itemView.findViewById(R.id.userListDescriptionTextView);
        }
    }
}

