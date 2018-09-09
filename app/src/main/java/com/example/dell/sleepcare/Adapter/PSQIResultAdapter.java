package com.example.dell.sleepcare.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.sleepcare.Model.PSQIResult;
import com.example.dell.sleepcare.R;

import java.util.List;

public class PSQIResultAdapter extends Adapter<PSQIResultAdapter.ViewHolder> {

    private Context mContext;
    private List<PSQIResult> scores;

    public PSQIResultAdapter(Context context , List<PSQIResult> scores){
        this.mContext = context;
        this.scores = scores;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_psqi_result_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.scoreSum.setText(String.valueOf(scores.get(position).getCOMP_SUM()+"점"));
        String[] dateArray = String.valueOf(scores.get(position).getPSQI_DATE()).split("-");
        holder.date.setText(dateArray[0]+"년 " + dateArray[1] +"월");
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        public TextView scoreSum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_test_date);
            scoreSum = itemView.findViewById(R.id.text_test_score_sum);
        }


    }
}
