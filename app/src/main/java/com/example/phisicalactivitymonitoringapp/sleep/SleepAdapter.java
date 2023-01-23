package com.example.phisicalactivitymonitoringapp.sleep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;

import java.util.Comparator;
import java.util.List;

public class SleepAdapter extends RecyclerView.Adapter<SleepAdapter.ViewHolder> {

    private List<Sleep> mSleep;

    public SleepAdapter(List<Sleep> sleepList) {
        mSleep = sleepList;
    }

    @NonNull
    @Override
    public SleepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View sleepView = inflater.inflate(R.layout.item_sleep, parent, false);

        ViewHolder viewHolder = new ViewHolder(sleepView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SleepAdapter.ViewHolder holder, int position) {
        Sleep sleep = mSleep.get(position);

        TextView dateTextView = holder.dateTextView;
        TextView startTimeTextView = holder.startTimeTextView;
        TextView endTimeTextView = holder.endTimeTextView;

        dateTextView.setText(sleep.getDate());
        startTimeTextView.setText(sleep.getStartTime());
        endTimeTextView.setText(sleep.getEndTime());
    }

    @Override
    public int getItemCount() {
        return mSleep.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.sleep_date);
            startTimeTextView = itemView.findViewById(R.id.sleep_startTime);
            endTimeTextView = itemView.findViewById(R.id.sleep_endTime);
        }
    }

    public void setSleepListAndKeyList(List<Sleep> sleepList) {
        this.mSleep.sort(Comparator.comparing(Sleep::getDate));

        this.mSleep = sleepList;

        notifyDataSetChanged();
    }
}
