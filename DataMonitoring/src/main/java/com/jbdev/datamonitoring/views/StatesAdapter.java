package com.jbdev.datamonitoring.views;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.jbdev.datamonitoring.R;
import com.jbdev.datamonitoring.database.model.State;

public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.MyViewHolder> {
    private List<State> statesList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView state;
        TextView subtype;
        TextView dot;
        TextView timestamp;
        TextView latitude;
        TextView reason;

        MyViewHolder(View view) {
            super(view);
            state = view.findViewById(R.id.state);
            subtype = view.findViewById(R.id.subtype);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            latitude = view.findViewById(R.id.latitude);
            reason = view.findViewById(R.id.reason);
        }
    }

    StatesAdapter(Context context, List<State> stateList) {
        //Context context1 = context;
        this.statesList = stateList;
    }

    public void updateData(List<State> stateList) {
        Log.v("TEXT", "updateData " + stateList.size());
        this.statesList = stateList;

        MainActivity.getInstance().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                notifyDataSetChanged();

            }
        });



    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.state_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        State state = statesList.get(position);

        holder.state.setText(state.getState());
        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.subtype.setText(String.format("%s %s", state.getSubtype(), state.getOperator()));

        // Formatting and displaying timestamp
        holder.timestamp.setText(state.getTimestamp());

        holder.latitude.setText(String.format("long: %s, lat: %s", state.getLongitude(), state.getLatitude()));

        holder.reason.setText(state.getReason());
    }

    @Override
    public int getItemCount() {
        return statesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt;
            fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
            return fmtOut.format(date);
        } catch (ParseException ignored) {
        }
        return "";
    }
}

