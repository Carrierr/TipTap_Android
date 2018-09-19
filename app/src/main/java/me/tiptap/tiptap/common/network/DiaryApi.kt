package me.tiptap.tiptap.common.network

import com.google.gson.JsonObject
import io.reactivex.Observable
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DiaryApi {

    //Login, return token.
    @POST("auth/login")
    fun login(@Body user : User): Observable<JsonObject>


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
            @Part("content") content: RequestBody,
            @Part("location") location: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part diaryFile: MultipartBody.Part?)
            : Observable<JsonObject>


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
            @Part("content") content: String,
            @Part("location") location: String,
            @Part("latitude") latitude: String,
            @Part("longitude") longitude: String,
            @Part("id") id: String,
            @Part diaryFile: MultipartBody.Part)
}