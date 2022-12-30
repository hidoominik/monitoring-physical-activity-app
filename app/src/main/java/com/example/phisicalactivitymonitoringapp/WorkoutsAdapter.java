package com.example.phisicalactivitymonitoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.workouts.Workout;

import java.util.List;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {

    private List<Workout> mWorkout;

    public WorkoutsAdapter(List<Workout> workouts) {
        mWorkout = workouts;
    }

    @NonNull
    @Override
    public WorkoutsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View workoutView = inflater.inflate(R.layout.item_workout, parent, false);

        ViewHolder viewHolder = new ViewHolder(workoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WorkoutsAdapter.ViewHolder holder, int position) {
        Workout workout = mWorkout.get(position);

        TextView nameTextView = holder.nameTextView;
        TextView dateTextView = holder.dateTextView;
        TextView startTimeTextView = holder.startTimeTextView;
        TextView endTimeTextView = holder.endTimeTextView;

        nameTextView.setText(workout.getName());
        dateTextView.setText(workout.getDate());
        startTimeTextView.setText(workout.getStartTime());
        endTimeTextView.setText(workout.getEndTime());
    }

    @Override
    public int getItemCount() {
        return mWorkout.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.workout_name);
            dateTextView = (TextView) itemView.findViewById(R.id.workout_date);
            startTimeTextView = (TextView) itemView.findViewById(R.id.workout_startTime);
            endTimeTextView = (TextView) itemView.findViewById(R.id.workout_endTime);
        }
    }

    public void setWorkoutList(List<Workout> workoutList) {
        this.mWorkout = workoutList;
        notifyDataSetChanged();
    }
}
