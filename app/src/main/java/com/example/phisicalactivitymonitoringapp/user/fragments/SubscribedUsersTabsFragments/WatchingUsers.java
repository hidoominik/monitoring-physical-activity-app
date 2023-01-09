package com.example.phisicalactivitymonitoringapp.user.fragments.SubscribedUsersTabsFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WatchingUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchingUsers extends Fragment {

    private RecyclerView userListRecyclerView;
    private TextView emptyView;
    private ArrayList<User> userList;
    UserDataAdapter adapter;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;

    private static final String WATCHING_USERS_PARAM = "watchingUsers";

    private ArrayList<String> mWatchingUsersParam;

    public WatchingUsers() {}

    public static WatchingUsers newInstance(Set<String> watchingUsers) {
        WatchingUsers fragment = new WatchingUsers();
        Bundle args = new Bundle();
        ArrayList<String> watchingUsersList = new ArrayList<>(watchingUsers);
        args.putStringArrayList(WATCHING_USERS_PARAM, watchingUsersList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWatchingUsersParam = getArguments().getStringArrayList(WATCHING_USERS_PARAM);
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
                    if (user != null && mWatchingUsersParam.contains(user.getUsername())) {
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
        View view = inflater.inflate(R.layout.fragment_watching_users, container, false);
        userListRecyclerView = view.findViewById(R.id.watchingUsersRecyclerView);
        emptyView = view.findViewById(R.id.WatchingUsersEmptyView);
        return view;
    }
}