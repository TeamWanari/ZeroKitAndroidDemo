package com.wanari.zerokit.zerokitdemo.adapters;

import com.google.gson.Gson;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Todo todo = mValues.get(position);
        holder.setTodo(todo);
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
        mValues.clear();
        mValues.addAll(todoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mParentView;

        public final TextView mTitleText;

        public final ProgressBar mProgressBar;

        public Todo mItem;

        public ViewHolder(View view, IMain listener) {
            super(view);
            mParentView = view;
            mTitleText = (TextView) view.findViewById(R.id.title);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        }

        private void showProgress() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        private void hideProgress() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                    initLayout();
                }
            });
        }

        public void setTodo(Todo todo) {
            this.mItem = todo;
            if (mItem.getTitle() == null && mItem.getEncryptedTodo() != null) {
                showProgress();
                ZerokitManager.getInstance().getZerokit().decrypt(mItem.getEncryptedTodo()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String decryptedTodoString) {
                        try {
                            decryptedTodoString = decryptedTodoString.replace("\\", "\"");
                            Todo decryptedTodo = new Gson().fromJson(decryptedTodoString, Todo.class);

                            mItem.setTitle(decryptedTodo.getTitle());
                            mItem.setDescription(decryptedTodo.getDescription());
                            mItem.setDecrypted(true);
                        } catch (Exception e) {
                            mItem.setTitle("Unable to decrypt");
                        } finally {
                            hideProgress();
                        }
                    }
                }, new Action1<ResponseZerokitError>() {
                    @Override
                    public void call(ResponseZerokitError responseZerokitError) {
                        hideProgress();
                        mItem.setTitle("Unable to decrypt");
                    }
                });
            }
        }

        private void initLayout() {
            mTitleText.setText(mItem.getTitle());
            if (mItem.isDecrypted()) {
                mParentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.todoItemSelected(mItem);
                        }
                    }
                });

                mParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (null != mListener) {
                            mListener.todoItemDelete(mItem);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        }
    }
}
