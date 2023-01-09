package com.example.phisicalactivitymonitoringapp.user.fragments.SubscribedUsersTabsFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.adapters.UserDataAdapter;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

public class WatchedUsers extends Fragment {

    private RecyclerView userListRecyclerView;
    private TextView emptyView;
    private ArrayList<User> userList;
    UserDataAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;

    private static final String WATCHED_USERS_PARAM = "watchedUsers";

    private ArrayList<String> mWatchedUsersParam;

    public WatchedUsers() {
    }

    public static WatchedUsers newInstance(Set<String> watchedUsers) {
        WatchedUsers fragment = new WatchedUsers();
        Bundle args = new Bundle();
        ArrayList<String> watchedUsersList = new ArrayList<>(watchedUsers);
        args.putStringArrayList(WATCHED_USERS_PARAM, watchedUsersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWatchedUsersParam = getArguments().getStringArrayList(WATCHED_USERS_PARAM);
        }

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        buildRecyclerView();

    }

    private void buildRecyclerView() {

        userList = new ArrayList<>();

        Query usernameQuery = users.orderByChild("username");
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null && mWatchedUsersParam.contains(user.getUsername())) {
                        userList.add(user);
                    }
                }
                if(userList.size()==0){
                    userListRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    userListRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter = new UserDataAdapter(userList, getActivity());
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());

                    userListRecyclerView.setHasFixedSize(true);
                    userListRecyclerView.setLayoutManager(manager);
                    userListRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Reading users list failed", error.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watched_users, container, false);
        userListRecyclerView = view.findViewById(R.id.watchedUsersRecyclerView);
        emptyView = view.findViewById(R.id.WatchedUsersEmptyView);
        return view;
    }
}