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


@BindingAdapter("year")
fun extractYearFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("yy`", Locale.KOREAN).format(date)
}

@BindingAdapter("dayOfMonth")
fun extractDayOfMonthFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("MMM \ndd", Locale.US).format(date)
}

@BindingAdapter("time")
fun extractTimeFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("HH:mm", Locale.KOREAN).format(date)
}

@BindingAdapter("month")
fun extractMonthFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("MMM", Locale.US).format(date)
}

@BindingAdapter("imgUrl", "error")
fun loadImage(imgView: ImageView, url: String?, errDrawable: Drawable) {
    GlideApp
            .with(imgView)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(errDrawable)
            .priority(Priority.HIGH)
            .into(imgView)
}


