package com.wanari.zerokit.zerokitdemo.interfaces;

import com.wanari.zerokit.zerokitdemo.entities.Todo;

public interface IMain {

    void saveFinished();

    void todoItemSelected(Todo item);

    void todoItemDelete(Todo item);
}
