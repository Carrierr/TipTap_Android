package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.util.setupActionBar
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.data.InvalidDiary
import me.tiptap.tiptap.databinding.ActivityDiaryDetailBinding
import java.util.*

class DiaryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryDetailBinding

    private var diary = Diary()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val disposalbe = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_detail)

        binding.apply {
            activity = this@DiaryDetailActivity
            date = Date()
        }


        initToolbar()

        checkBus()
    }


    private fun checkBus() {
        disposalbe.add(
                RxBus.getInstance().toObservable()
                        .subscribe {
                            if (it is Diary) {
                                binding.diary = it
                                diary = it
                            }
                        }
        )
    }


    private fun initToolbar() {
        setupActionBar(R.id.toolbar_detail) {
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_back_white)
            setDisplayHomeAsUpEnabled(true)
        }
    }


    /**
     * When click delete button.
     */
    fun onDeleteButtonClick() {
        disposalbe.add(
                service.deleteDiary(TipTapApplication.getAccessToken(), InvalidDiary(mutableListOf(diary.id)))
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<JsonObject>() {
                            override fun onComplete() {
                                finish()
                            }

                            override fun onNext(t: JsonObject) {
                                t.apply {
                                    if (get(getString(R.string.code)).asString != "1000") { //if not successful.
                                        Log.d(getString(R.string.desc),
                                                getAsJsonObject(getString(R.string.data)).get(getString(R.string.desc)).asString)
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
        )
    }


    override fun onDestroy() {
        super.onDestroy()

        disposalbe.dispose()
    }
}
