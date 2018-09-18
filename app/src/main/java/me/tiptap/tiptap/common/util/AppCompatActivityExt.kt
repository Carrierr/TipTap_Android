package me.tiptap.tiptap.common.util

import android.content.Intent
import android.support.annotation.IdRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import me.tiptap.tiptap.login.LoginActivity


fun AppCompatActivity.setupActionBar(@IdRes id: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(id))

    supportActionBar?.run {
        action()
    }
}

fun AppCompatActivity.redirectLoginActivity() {
    startActivity(Intent(this, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    })
}
