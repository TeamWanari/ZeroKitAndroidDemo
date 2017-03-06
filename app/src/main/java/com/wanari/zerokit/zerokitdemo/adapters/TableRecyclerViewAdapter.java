package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.interfaces.ITableList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TableRecyclerViewAdapter extends RecyclerView.Adapter<TableRecyclerViewAdapter.ViewHolder> {

    private List<Table> mValues;

    private final ITableList mListener;

    public TableRecyclerViewAdapter(ITableList listener, List<Table> tableNames) {
        mListener = listener;
        mValues = tableNames;
    }

    @Override
    public TableRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table, parent, false);
        return new TableRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TableRecyclerViewAdapter.ViewHolder holder, int position) {
        final Table table = mValues.get(position);
        holder.mItem = table;
        holder.mIdView.setText(table.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.tableItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        } else {
            return mValues.size();
        }
    }

    public void setItems(List<Table> tableNames) {
        int oldNum = mValues.size();
        mValues = tableNames;
        notifyItemRangeChanged(0, oldNum);
    }

    public void removeItem(Table table) {
        int position = mValues.indexOf(table);
        mValues.remove(table);
        notifyItemRemoved(position);
        if (mValues.size() == 0) {
            mListener.closeTableList();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public final TextView mIdView;

        public Table mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.title);
        }
    }
}
