package com.wanari.zerokit.zerokitdemo.database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.entities.Todo;

import android.support.annotation.Nullable;

public final class FireBaseHelper {

    private static FireBaseHelper instance;

    private static final String DATABASE_TODO_LISTS = "tableslist";

    private static final String DATABASE_TODOS = "todos";

    private final DatabaseReference databaseRef;

    private FireBaseHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
    }

    public static FireBaseHelper getInstance() {
        if (instance == null) {
            instance = new FireBaseHelper();
        }

        return instance;
    }

    public void getTableLists(ValueEventListener valueEventListener) {
        databaseRef.child(DATABASE_TODO_LISTS).addValueEventListener(valueEventListener);
    }

    public void getTodos(String tableId, ValueEventListener valueEventListener) {
        databaseRef.child(DATABASE_TODO_LISTS).child(tableId).child(DATABASE_TODOS).addValueEventListener(valueEventListener);
    }

    public void saveTodo(@Nullable String key, String encryptedTodo, String tableName, OnSuccessListener onSuccessListener) {
        if (key == null) {
            key = databaseRef.push().getKey();
        }
        databaseRef.child(DATABASE_TODO_LISTS).child(tableName).child(DATABASE_TODOS).child(key).setValue(encryptedTodo)
                .addOnSuccessListener(onSuccessListener);
    }

    public void deleteTodo(Todo todo, String tableName, DatabaseReference.CompletionListener completionListener) {
        String key = todo.getId();
        if (key != null) {
            databaseRef.child(DATABASE_TODO_LISTS).child(tableName).child(DATABASE_TODOS).child(key).removeValue(completionListener);
        }
    }

    public void saveTable(String userId, Table table) {
        String key = databaseRef.push().getKey();
        table.setId(key);
        AppConf.putTable(userId, table);
        databaseRef.child(DATABASE_TODO_LISTS).child(key).setValue(table);
    }
}
