package io.github.idoqo.radario.lib;


import android.widget.AbsListView;

public class EndlessScrollListener implements AbsListView.OnScrollListener {
    private static final int SCROLL_OFFSET = 2;
    private EndlessScrollListenerInterface listenerInterface;

    public EndlessScrollListener(EndlessScrollListenerInterface listenerInterface){
        this.listenerInterface = listenerInterface;
    }

    public void onScrollStateChanged(AbsListView listView, int i){

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount){
        if (totalItemCount - (firstVisibleItem + 1 + visibleItemCount) < SCROLL_OFFSET &&
                visibleItemCount < totalItemCount){
            listenerInterface.onListEnd();
        }

        listenerInterface.onScrollCalled(firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public void checkForMore(final AbsListView listView){
        Runnable fetchMore = new Runnable() {
            @Override
            public void run() {
                int last = listView.getLastVisiblePosition();
                if (listView.getChildAt(last) != null) {
                    int bottom = listView.getChildAt(last).getBottom();
                    int count = listView.getCount();
                    int height = listView.getHeight();
                    if (last == count - 1 && bottom <= height) {
                        listenerInterface.onListEnd();
                    }
                }
            }
        };
        listView.post(fetchMore);
    }
}
