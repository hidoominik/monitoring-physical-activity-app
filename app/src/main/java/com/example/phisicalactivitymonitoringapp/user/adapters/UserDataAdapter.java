package com.example.phisicalactivitymonitoringapp.user.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.example.phisicalactivitymonitoringapp.user.model.User;

import java.util.ArrayList;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder> {

    private ArrayList<User> userModelArrayList;
    private Context context;

    public UserDataAdapter(ArrayList<User> userModelArrayList, Context context) {
        this.userModelArrayList = userModelArrayList;
        this.context = context;
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
        holder.viewDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("username", model.getUsername());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView userDescriptionTextView;
        private final Button viewDetailsButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userListUsernameTextView);
            userDescriptionTextView = itemView.findViewById(R.id.userListDescriptionTextView);
            viewDetailsButton = itemView.findViewById(R.id.userListViewDetailsButton);
        }
    }
}

