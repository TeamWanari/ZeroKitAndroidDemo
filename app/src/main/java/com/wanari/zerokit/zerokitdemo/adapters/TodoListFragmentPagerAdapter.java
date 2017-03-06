package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.fragments.TodoListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class TodoListFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Table> tabList;

    private TodoListFragment mCurrentFragment;

    public TodoListFragmentPagerAdapter(FragmentManager fm, List<Table> tabList) {
        super(fm);
        this.tabList = tabList;
    }

    @Override
    public Fragment getItem(int position) {
        return TodoListFragment.newInstance(tabList.get(position));
    }

    @Override
    public int getCount() {
        if (tabList != null) {
            return tabList.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        int position = tabList.indexOf(object);
        return position == -1 ? POSITION_NONE : position;
    }

    public Table getTable(int position) {
        return tabList.get(position);
    }

    public void setItems(List<Table> addedTables) {
        this.tabList.clear();
        this.tabList.addAll(addedTables);
        notifyDataSetChanged();
    }

    public void deleteTable(Table table) {
        this.tabList.remove(table);
        notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((TodoListFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public TodoListFragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
