package com.example.dell.sleepcare.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.sleepcare.R;

import java.util.ArrayList;

public class TestResultAdapter extends Adapter<TestResultAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Integer> scores;

    public TestResultAdapter(Context context ,ArrayList<Integer> scores){
        this.mContext = context;
        this.scores = scores;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chip.setText(String.format("항목%s", String.valueOf(position + 1)));
        if(position==7){
            holder.chip.setText("총점");
        }
        holder.scoreText.setText(String.valueOf(scores.get(position)));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public Chip chip;
        public TextView scoreText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip_componenet_number);
            scoreText = itemView.findViewById(R.id.text_psqi_score);
        }

        public ViewHolder(Context context, ViewGroup parent){
            super(LayoutInflater.from(context).inflate(R.layout.item_result_card, parent, false));
            chip = parent.findViewById(R.id.chip_componenet_number);
            scoreText = parent.findViewById(R.id.text_psqi_score);
        }
    }
}
