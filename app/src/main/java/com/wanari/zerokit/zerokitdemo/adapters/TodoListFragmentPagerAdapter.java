package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.fragments.TodoListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class TodoListFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> tabList;

    public TodoListFragmentPagerAdapter(FragmentManager fm, List<String> tabList) {
        super(fm);
        this.tabList = tabList;
    }

    @Override
    public Fragment getItem(int position) {
        return TodoListFragment.newInstance();
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
        return tabList.get(position);
    }
}
