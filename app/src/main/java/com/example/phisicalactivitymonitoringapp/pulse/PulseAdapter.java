package com.example.phisicalactivitymonitoringapp.pulse;

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

public class PulseAdapter extends RecyclerView.Adapter<PulseAdapter.ViewHolder> {

    private List<Pulse> mPulse;

    public PulseAdapter(List<Pulse> pulseList) {
        mPulse = pulseList;
    }

    @NonNull
    @Override
    public PulseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View pulseView = inflater.inflate(R.layout.item_pulse, parent, false);

        PulseAdapter.ViewHolder viewHolder = new PulseAdapter.ViewHolder(pulseView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PulseAdapter.ViewHolder holder, int position) {
        Pulse pulse = mPulse.get(position);

        TextView valueTextView = holder.valueTextView;
        TextView dateTextView = holder.dateTextView;
        TextView timeTextView = holder.timeTextView;

        valueTextView.setText(pulse.getValue());
        dateTextView.setText(pulse.getDate());
        timeTextView.setText(pulse.getTime());
    }

    @Override
    public int getItemCount() {
        return mPulse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView valueTextView;
        public TextView dateTextView;
        public TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            valueTextView = itemView.findViewById(R.id.pulse_value);
            dateTextView = itemView.findViewById(R.id.pulse_date);
            timeTextView = itemView.findViewById(R.id.pulse_time);
        }
    }

    public void setPulseList(List<Pulse> pulseList) {
        this.mPulse.sort(Comparator.comparing(Pulse::getDate));
        this.mPulse = pulseList;

        notifyDataSetChanged();
    }
}
