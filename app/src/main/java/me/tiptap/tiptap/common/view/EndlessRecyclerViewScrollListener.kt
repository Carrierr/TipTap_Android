package me.tiptap.tiptap.common.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerViewScrollListener() : RecyclerView.OnScrollListener() {

    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int) : this() {
        this.mLayoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: StaggeredGridLayoutManager, visibleThreshold: Int) : this() {
        this.mLayoutManager = layoutManager
        this.visibleThreshold = visibleThreshold * layoutManager.spanCount
    }

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 0 // The current offset index of data you have loaded
    private var curPage = 0  // The total number of items in the dataSet after the last load
    private var prevTotalItemCnt = 0 //The total number of items in the dataSet after the last load
    private var isLoading = true  // True if we are still waiting for the last set of data to load.
    private var startPageIdx = 0   // Sets the starting page index


    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCnt = mLayoutManager.itemCount

        when (mLayoutManager) {
            is LinearLayoutManager -> {
                lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            }
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCnt < prevTotalItemCnt) {
            curPage = startPageIdx
            prevTotalItemCnt = totalItemCnt

            if (totalItemCnt == 0) {
                isLoading = true
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (isLoading && (totalItemCnt > prevTotalItemCnt)) {
            isLoading = false
            prevTotalItemCnt = totalItemCnt
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!isLoading && (lastVisibleItemPosition + visibleThreshold) > totalItemCnt) {
            curPage++
            onLoadMore(rv, curPage, totalItemCnt)
            isLoading = true
        }
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(rv: RecyclerView, page: Int, totalItemCnt: Int)

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (pos in lastVisibleItemPositions.iterator()) {
            if (pos == 0) maxSize = lastVisibleItemPositions[0]
            else if (pos > maxSize) maxSize = lastVisibleItemPositions[pos]
        }
        return maxSize
    }

    // Call this method whenever performing new searches
    fun resetAllState() {
        isLoading = true
        curPage = startPageIdx
        prevTotalItemCnt = 0
    }


}