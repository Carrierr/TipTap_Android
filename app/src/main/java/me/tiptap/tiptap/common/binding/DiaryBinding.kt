package me.tiptap.tiptap.common.binding

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.tiptap.tiptap.common.util.GlideApp
import java.text.SimpleDateFormat
import java.util.*

@BindingConversion
fun convertDateToString(date : Date) : String =
        SimpleDateFormat("yyyy.MM.dd", Locale.US).format(date)

@BindingAdapter("year")
fun extractYearFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("yy`", Locale.KOREAN).format(date)
}

@BindingAdapter("dayOfMonth")
fun extractDayOfMonthFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("MMM \ndd", Locale.US).format(date)
}

@BindingAdapter("time")
fun extractTimeFromDate(view: TextView, date: Date?) {
    view.text = SimpleDateFormat("HH:mm", Locale.KOREAN).format(date)
}

@BindingAdapter("month")
fun extractMonthFromDate(view: TextView, date: Date) {
    view.text = SimpleDateFormat("MMM", Locale.US).format(date)
}

@BindingAdapter("imgUrl", "error")
fun loadImage(imgView: ImageView, url: String?, errDrawable: Drawable?) {
    GlideApp
            .with(imgView)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(errDrawable)
            .into(imgView)
}


@BindingAdapter("colorSchemeResources")
fun setColorSchemeResources( view : SwipeRefreshLayout, colorResId : Int) {
    view.setColorSchemeColors(ContextCompat.getColor(view.context, colorResId))
}