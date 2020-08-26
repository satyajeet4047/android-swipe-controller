package com.example.swipecontrollerlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private List<String> mValues;


    public CustomRecyclerViewAdapter(List<String> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String name = mValues.get(position);
        holder.mTitle.setText(name);

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    void deleteItem(int position){

        this.mValues.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem( int position,String item) {

        this.mValues.add(position, item);
        notifyItemInserted(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mTitle;


        ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = view.findViewById(R.id.tv_title);
        }

    }

}