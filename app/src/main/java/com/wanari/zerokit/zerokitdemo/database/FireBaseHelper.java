package com.wanari.zerokit.zerokitdemo.database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.wanari.zerokit.zerokitdemo.entities.Todo;

public final class FireBaseHelper {

    private static FireBaseHelper instance;

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

    public void getTodos(ValueEventListener valueEventListener) {
        databaseRef.child(DATABASE_TODOS).addValueEventListener(valueEventListener);
    }

    public void saveTodo(Todo todo, OnSuccessListener onSuccessListener) {
        String key = todo.getId();
        if (key == null) {
            key = databaseRef.push().getKey();
            todo.setId(key);
            databaseRef.child(DATABASE_TODOS).child(key).setValue(todo).addOnSuccessListener(onSuccessListener);
        } else {
            databaseRef.child(DATABASE_TODOS).child(key).updateChildren(todo.toMap()).addOnSuccessListener(onSuccessListener);
        }
    }

    public void deleteTodo(Todo todo, DatabaseReference.CompletionListener completionListener) {
        String key = todo.getId();
        if (key != null) {
            databaseRef.child(DATABASE_TODOS).child(key).removeValue(completionListener);
        }
    }
}
