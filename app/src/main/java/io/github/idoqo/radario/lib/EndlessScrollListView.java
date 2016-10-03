package io.github.idoqo.radario.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.Collection;

public class EndlessScrollListView extends ListView {

    private EndlessScrollListener listener;

    public EndlessScrollListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setListener(EndlessScrollListener l){
        this.listener = l;
    }

    public void setAdapter(EndlessScrollAdapter adapter){
        super.setAdapter(adapter);
    }

    public void setSelection(int position){super.setSelection(position);}

    public void appendItems(Collection<?> items){
        if(getAdapter() == null){
            throw new NullPointerException("Cannot append items to a null adapter");
        }
        ((EndlessScrollAdapter) getAdapter()).addItems(items);
        if (items.size() == 0) {setOnScrollListener(null);}
        else {
            setOnScrollListener(listener);
            listener.checkForMore(this);
        }
    }

    public int getRealCount(){
        return ((EndlessScrollAdapter) getAdapter()).getItems().size();
    }
}
