package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder> {

    private List<Todo> mValues;

    private final IMain mListener;

    public TodoRecyclerViewAdapter(IMain listener, List<Todo> todoList) {
        mListener = listener;
        mValues = todoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Todo todo = mValues.get(position);
        holder.mItem = todo;
        holder.mIdView.setText(todo.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.todoItemSelected(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mListener) {
                    mListener.todoItemDelete(holder.mItem);
                    return true;
                } else {
                    return false;
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

    public void setItems(List<Todo> todoList) {
        mValues = todoList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public final TextView mIdView;

        public Todo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.title);
        }
    }
}
