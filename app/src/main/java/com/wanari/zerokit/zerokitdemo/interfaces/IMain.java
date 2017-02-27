package com.wanari.zerokit.zerokitdemo.interfaces;

import com.wanari.zerokit.zerokitdemo.entities.Todo;

public interface IMain {

    void saveSuccess();

    void todoItemSelected(Todo item);

    void todoItemDelete(Todo item);

    void closeTableList();
}
