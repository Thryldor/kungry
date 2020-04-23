package ca.ulaval.ima.mp.tools

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener() :
    RecyclerView.OnScrollListener() {

    lateinit var layoutManager: LinearLayoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= currentPageCount
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    abstract val currentPageCount: Int

    abstract val isLastPage: Boolean

    abstract val isLoading: Boolean

}
