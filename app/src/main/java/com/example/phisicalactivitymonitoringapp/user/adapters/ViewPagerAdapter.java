package com.example.phisicalactivitymonitoringapp.user.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.phisicalactivitymonitoringapp.user.fragments.SubscribedUsersTabsFragments.WatchedUsers;
import com.example.phisicalactivitymonitoringapp.user.fragments.SubscribedUsersTabsFragments.WatchingUsers;

import java.util.List;
import java.util.Set;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Set<String> watchedUsers;
    Set<String> watchingUsers;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Set<String> watchedUsers, Set<String> watchingUsers)
    {
        super(fragmentActivity);
        this.watchedUsers=watchedUsers;
        this.watchingUsers=watchingUsers;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return WatchedUsers.newInstance(watchedUsers);
            default:
                return WatchingUsers.newInstance(watchingUsers);
        }
    }
    @Override
    public int getItemCount() {return 2; }
}
