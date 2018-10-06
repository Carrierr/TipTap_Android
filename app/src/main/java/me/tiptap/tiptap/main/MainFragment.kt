package me.tiptap.tiptap.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableInt
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.data.DiaryResponse
import me.tiptap.tiptap.databinding.FragmentMainBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity
import me.tiptap.tiptap.setting.SettingActivity
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val rxBus = RxBus.getInstance()
    private val disposables = CompositeDisposable()

    var todayDiaries: MutableList<Diary> = mutableListOf()
    val postSize = ObservableInt(todayDiaries.size) //postSize

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.apply {
            fragment = this@MainFragment
            date = Date()
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
    }


    override fun onResume() {
        super.onResume()

        getTodayDiaries()
    }


    /**
     * Get today diaries
     */
    private fun getTodayDiaries() {
        disposables.add(
                service.getTodayDiaries(TipTapApplication.getAccessToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<DiaryResponse>() {
                            override fun onNext(t: DiaryResponse) {
                                todayDiaries = t.data.diaries
                                postSize.set(todayDiaries.size)

                                applyStamps(t.data.stamp)
                            }

                            override fun onComplete() {

                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        }))

    }

    /**
     * apply stamps
     */
    private fun applyStamps(stamps: MutableList<String>) {
        for (i in 1 until stamps.size - 1) {
            val stampId = resources.getIdentifier(stamps[i], "drawable", activity?.packageName)
            val viewId = resources.getIdentifier("img_post_${i + 1}", "id", activity?.packageName)

            val view = activity?.findViewById(viewId) as ImageView

            view.setImageResource(stampId)
        }
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar?.toolbarMain)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }


    fun onAddButtonClick() {
        Intent(context, DiaryWritingActivity::class.java).apply {
            startActivity(this)
        }
    }

    fun onSettingButtonClick() {
        startActivity(Intent(this@MainFragment.activity, SettingActivity::class.java))
    }


    override fun onPause() {
        super.onPause()

        disposables.clear()
    }


    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }
}
