package me.tiptap.tiptap.common.util

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class TipTapGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
       val calculator = MemorySizeCalculator.Builder(context)
               .setMemoryCacheScreens(1F)
               .build()

        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
    }
}