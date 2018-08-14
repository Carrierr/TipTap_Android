package me.tiptap.tiptap.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.util.helper.Utility
import com.kakao.util.helper.Utility.getPackageInfo
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private val TAG = Utility::class.java.canonicalName

fun getKeyHash(context: Context): String? {
    getPackageInfo(context, PackageManager.GET_SIGNATURES)?.let {
        for (signature in it.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())

                return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                Log.w(TAG, "Unable to get MessageDigest. signature = $signature", e)
            }
        }
    }
    return null
}