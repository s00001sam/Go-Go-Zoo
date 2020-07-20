package com.sam.gogozoo.route

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(itemMoveSwipeListener: ItemMoveSwipeListener?): ItemTouchHelper.Callback() {

    val moveSwipeListener: ItemMoveSwipeListener? = itemMoveSwipeListener
    // 設定1個帶 ItemMoveSwipeListener 的參數建構式
//    fun ItemTouchHelperCallback(itemMoveSwipeListener: ItemMoveSwipeListener?) {
//        this.itemMoveSwipeListener = itemMoveSwipeListener
//    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.ACTION_STATE_IDLE
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 透過itemMoveSwipeListener的onItemMove，讓adapter實作該方法
        moveSwipeListener?.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition())
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 透過itemMoveSwipeListener的onItemSwipe，讓adapter實作該方法
        moveSwipeListener?.onItemSwipe(viewHolder.getAdapterPosition())
    }
}