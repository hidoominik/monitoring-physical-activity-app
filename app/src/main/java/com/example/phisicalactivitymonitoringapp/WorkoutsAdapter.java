package com.example.phisicalactivitymonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.workouts.Workout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {

    private List<Workout> mWorkout;
    private List<String> mKey;

    private String key;

    public WorkoutsAdapter(List<Workout> workouts, List<String> keys) {
        mWorkout = workouts;
        mKey = keys;
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
        TextView placeTextView = holder.placeTextView;
        TextView dateTextView = holder.dateTextView;
        TextView startTimeTextView = holder.startTimeTextView;
        TextView endTimeTextView = holder.endTimeTextView;

        nameTextView.setText(workout.getName());
        placeTextView.setText(workout.getPlace());
        dateTextView.setText(workout.getDate());
        startTimeTextView.setText(workout.getStartTime());
        endTimeTextView.setText(workout.getEndTime());
    }

    @Override
    public int getItemCount() {
        return mWorkout.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTextView;
        public TextView placeTextView;
        public TextView dateTextView;
        public TextView startTimeTextView;
        public TextView endTimeTextView;

        public Button editButton;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.workout_name);
            placeTextView = (TextView) itemView.findViewById(R.id.workout_place);
            dateTextView = (TextView) itemView.findViewById(R.id.workout_date);
            startTimeTextView = (TextView) itemView.findViewById(R.id.workout_startTime);
            endTimeTextView = (TextView) itemView.findViewById(R.id.workout_endTime);

            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(editButton)) {
                editAt(v, getAdapterPosition());
            }
            else if (v.equals(deleteButton)) {
                removeAt(getAdapterPosition());
            }
        }
    }

    public void setWorkoutListAndKeyList(List<Workout> workoutList, List<String> keyList) {
        this.mWorkout = workoutList;
        this.mKey = keyList;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        String key = mKey.get(position);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Workouts");
        ref.child(key).removeValue();
        mKey.remove(position);
        mWorkout.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mWorkout.size());
    }

    public void editAt(View v, int position) {
        key = mKey.get(position);

        Intent intent = new Intent(v.getContext(), AddWorkoutActivity.class);
        intent.putExtra("key", key);
        v.getContext().startActivity(intent);
    }
}
