package com.notfound.makeamericafitagain;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ResultAdapter extends
        RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private List<Food> foods;
    private Activity activity;

    public ResultAdapter(List<Food> foods, Activity activity){
        this.foods = foods;
        this.activity = activity;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View resultView = inflater.inflate(R.layout.item_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(resultView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ResultAdapter.ViewHolder viewHolder, int position) {

        Food food = foods.get(position);

        // Set item views based on your views and data model
        TextView tv_name = viewHolder.tv_name;
        Button btn_delete = viewHolder.btn_thing;

        tv_name.setText(food.getName());
    }
    
    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return foods.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tv_name;
        public Button btn_thing;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}