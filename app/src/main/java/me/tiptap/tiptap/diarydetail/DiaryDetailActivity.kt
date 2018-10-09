package me.tiptap.tiptap.diarydetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
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
import me.tiptap.tiptap.data.DiaryResponse
import me.tiptap.tiptap.data.InvalidDiaries
import me.tiptap.tiptap.databinding.ActivityDiaryDetailBinding
import java.util.*

class DiaryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryDetailBinding

    private var lastDiary = Diary()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val disposable = CompositeDisposable()

    private val adapter = DetailAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diary_detail)

        binding.apply {
            activity = this@DiaryDetailActivity
            date = Date()
        }

        initToolbar()
        initRecyclerView()

        checkBus()
        requestDetailDiary()
    }


    private fun checkBus() {
        RxBus.getInstance().toObservable()
                .subscribe {
                    if (it is Diary) {
                        lastDiary = it
                    }
                }.dispose()

    }

    private fun initRecyclerView() {
        binding.recyclerDetail.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@DiaryDetailActivity)
            adapter = this@DiaryDetailActivity.adapter
        }
    }


    private fun requestDetailDiary() {
        disposable.add(
                service.getDiaryDetail(
                        TipTapApplication.getAccessToken(), lastDiary?.createdAt.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<DiaryResponse>() {
                            override fun onNext(task: DiaryResponse) {
                                if (task.code == "1000") {
                                    adapter.addItems(task.data.diaries)
                                }
                            }

                            override fun onComplete() {
                                adapter.notifyDataSetChanged()
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        }))
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
        val invalidDiaries = InvalidDiaries().apply {
            convertDateToString(mutableListOf(lastDiary.createdAt))
        }
        disposable.add(
                service.deleteDiaryByDay(TipTapApplication.getAccessToken(), invalidDiaries)
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

        disposable.dispose()
    }
}
