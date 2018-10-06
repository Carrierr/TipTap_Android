package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import me.tiptap.tiptap.databinding.ItemDiaryDetailBinding

class DetailViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    val binding : ItemDiaryDetailBinding? = DataBindingUtil.bind(itemView)

    init {
        //아이템 내부에서 스크롤이 가능하도록 함.
        binding?.scrollDetail?.setOnTouchListener { view, _->
            view.parent.requestDisallowInterceptTouchEvent(true)

            false
        }
    }

}