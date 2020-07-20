package com.sam.gogozoo.route

interface ItemMoveSwipeListener {

    /**
     * 設置1個監聽的interface
     *
     * onItemMove : 當item移動完的時候
     * onItemSwipe : 當item滑動完的時候
     */
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemSwipe(position: Int)

}