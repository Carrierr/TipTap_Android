package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import me.tiptap.tiptap.common.util.GlideApp
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("writeDate")
fun convertWriteDateFormat(view: TextView, date: Date) {
    view.text = SimpleDateFormat("yyyy MMM dd - HH:mm", Locale.US).format(date)
}

@BindingAdapter("imgUrl")
fun loadUpdateImg(imgView: ImageView, url: String?) {
    GlideApp
            .with(imgView)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail(0.1f)
            .into(imgView)
}