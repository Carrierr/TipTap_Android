package me.tiptap.tiptap.common.util.login

import me.tiptap.tiptap.data.User


/**
 * interface for Login
 */
interface LoginNavigator {

    fun getAccessToken(user: User)

    fun startActivity()

    fun redirectLoginActivity()
}