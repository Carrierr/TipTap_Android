package me.tiptap.tiptap.common.network

import io.reactivex.Observable
import me.tiptap.tiptap.data.Diary
import okhttp3.MultipartBody
import retrofit2.http.*

interface DiaryApiInterface {

    //Get DiaryList
    @GET("diary/list")
    fun diaryList(
            @Header("tiptap-token") token: String):
            Observable<List<Diary>>


    //write Diary
    @Multipart
    @POST("diary/write")
    fun writeDiary(
            @Header("tiptap-token") token: String,
            @Part("content") content : String,
            @Part("location") location : String,
            @Part("latitude") latitude : String,
            @Part("longitude") longitude : String,
            @Part diaryFile : MultipartBody.Part )


    //delete Diary
    @POST("diary/delete")
    fun deleteDiary(
            @Header("tiptap-token") token: String,
            @Path("id") id: Int)


    //update Diary
    @Multipart
    @POST("diary/update")
    fun updateDiary(
            @Header("tiptap-token") token: String,
            @Part("content") content : String,
            @Part("location") location : String,
            @Part("latitude") latitude : String,
            @Part("longitude") longitude : String,
            @Part("id") id : String,
            @Part diaryFile : MultipartBody.Part )
}