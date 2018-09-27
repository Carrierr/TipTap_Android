package me.tiptap.tiptap.common.network

import com.google.gson.JsonObject
import io.reactivex.Observable
import me.tiptap.tiptap.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DiaryApi {

    //Login, return token.
    @POST("auth/login")
    fun login(@Body user: User): Observable<JsonObject>


    //Get DiaryList
    @GET("diary/list")
    fun diaryList(
            @Header("tiptap-token") token: String,
            @Query("page") page: Int,
            @Query("limit") limit: Int):
            Observable<DiariesResponse>


    //Get today diaries
    @GET("diary/today")
    fun getTodayDiaries(
            @Header("tiptap-token") token: String)
            : Observable<TodayResponse>


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
            @Body id: InvalidDiary): Observable<JsonObject>


    //delete Diaries
    @POST("diary/delete/day")
    fun deleteDiary(
            @Header("tiptap-token") token: String,
            @Body id: InvalidDiaries): Observable<JsonObject>


    //update Diary
    @Multipart
    @POST("diary/update")
    fun updateDiary(
            @Header("tiptap-token") token: String,
            @Part("content") content: RequestBody,
            @Part("location") location: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part("id") id: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part diaryFile: MultipartBody.Part?)
            : Observable<JsonObject>


    //Get share diaries
    @GET("diary/random")
    fun shareDiaries(
            @Header("tiptap-token") token: String
    ): Observable<ShareResponse>
}