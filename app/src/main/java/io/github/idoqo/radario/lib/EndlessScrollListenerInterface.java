package io.github.idoqo.radario.lib;


public interface EndlessScrollListenerInterface {
    public void onListEnd();

    public void onScrollCalled(int firstVisibleItem, int visibleItemcount, int totalItemCount);
}
