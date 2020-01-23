package im.ehab.casesapp.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import im.ehab.casesapp.Contains;

/**
 * Pagination
 * Created by Suleiman19 on 10/15/16.
 * Copyright (c) 2016. Suleiman Ali Shakir. All rights reserved.
 */
public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;

    /**
     * Supporting only LinearLayoutManager for now.
     *
     * @param layoutManager
     */
    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            Log.d(Contains.LOG+"isL", "(!isLoading() && !isLastPage())");
            /*if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= getTotalPageCount()) {*/
                Log.d(Contains.LOG+"isL", "visibleItemCount = "+visibleItemCount+
                        "\nfirstVisibleItemPosition = "+firstVisibleItemPosition+
                        "\ntotalItemCount = "+totalItemCount+
                        "\nfirstVisibleItemPosition = "+firstVisibleItemPosition+
                        "\ntotalItemCount = "+totalItemCount+
                        "\ngetTotalPageCount() = "+getTotalPageCount());
                loadMoreItems();
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
