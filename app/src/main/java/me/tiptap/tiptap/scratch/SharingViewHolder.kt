package me.tiptap.tiptap.scratch

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Observable
import me.tiptap.tiptap.data.Sharing
import me.tiptap.tiptap.databinding.ItemSharingBinding

class SharingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: ItemSharingBinding? = DataBindingUtil.bind(itemView)


    fun getClickObservable(item: Sharing): Observable<Sharing> =
            Observable.create { emitter ->
                itemView.setOnClickListener {
                    emitter.onNext(item)
                }
            }

    fun getLongClickObservable(): Observable<View> =
            Observable.create { emitter ->
                itemView.setOnLongClickListener {
                    emitter.onNext(it)
                    true
                }
            }

    fun getCheckObservable(item: Sharing): Observable<Sharing> =
            Observable.create { emitter ->
                binding?.checkDiary?.setOnCheckedChangeListener { _, state ->
                    item.isSelected = state
                    emitter.onNext(item)
                }
            }
}