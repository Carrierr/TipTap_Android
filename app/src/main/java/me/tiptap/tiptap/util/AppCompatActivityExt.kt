package me.tiptap.tiptap.util

import android.support.annotation.IdRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity


fun AppCompatActivity.setupActionBar(@IdRes id : Int, action : ActionBar.() -> Unit){
    setSupportActionBar(findViewById(id))

    supportActionBar?.run {
        action()
    }
}
