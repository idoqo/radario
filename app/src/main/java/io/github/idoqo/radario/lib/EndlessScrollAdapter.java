package io.github.idoqo.radario.lib;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collection;

public abstract class EndlessScrollAdapter extends BaseAdapter{
    private boolean doneLoading = false;
    private LayoutInflater inflater;

    public EndlessScrollAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public int getCount(){
        return getItems().size() + (!doneLoading ? 1 : 0);
    }

    public Object getItem(int position){
        if(!doneLoading && position == getCount()){
            throw new IllegalArgumentException("Cannot call getItem on loading placeholder");
        } else {
            return getRealItem(position);
        }
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(!doneLoading && position >= getItems().size()){
            return getLoadingView(inflater, parent);
        } else {
            return getRealView(inflater, position, convertView, parent);
        }
    }

    public void setDoneLoading(){this.doneLoading = true;}

    public abstract Collection<?> getItems();
    public abstract void addItems(Collection<?> items);
    public abstract Object getRealItem(int position);
    public abstract View getRealView(LayoutInflater layoutInflater, int position, View view, ViewGroup parent);
    public abstract View getLoadingView(LayoutInflater layoutInflater, ViewGroup parent);
}
