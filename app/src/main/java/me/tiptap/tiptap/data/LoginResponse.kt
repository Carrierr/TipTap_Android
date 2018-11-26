package me.tiptap.tiptap.data

import com.google.gson.annotations.SerializedName

class LoginResponse  {

    @SerializedName("code")
    val code: String? = null

    @SerializedName("data")
    val data: LoginData = LoginData()


    class LoginData{

        @SerializedName("token")
        val token: String = ""

        @SerializedName("existed")
        var isUserExisted: Boolean = true
    }
}