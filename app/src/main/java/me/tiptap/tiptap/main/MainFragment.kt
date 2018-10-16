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
import me.tiptap.tiptap.common.util.preview.PreviewDialogNavigator
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.data.DiaryResponse
import me.tiptap.tiptap.databinding.FragmentMainBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity
import me.tiptap.tiptap.preview.PreviewDialogFragment
import me.tiptap.tiptap.setting.SettingActivity
import java.util.*

class MainFragment : Fragment() , PreviewDialogNavigator {

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
                                todayDiaries.sortWith(compareBy { it.todayIndex }) //today Index 로 오름차순 정렬

                                postSize.set(todayDiaries.size)

                                applyStamps(t.data.stamp)
                            }

                            override fun onComplete() {

                                rxBus.takeBus(false)
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
        for (i in 0 until stamps.size) {
            val stampNum = stamps[i].subSequence(5, stamps[i].length).toString().toInt() //stamp 넘버 추출

            val stampResId = resources.getIdentifier("stamp$stampNum", "drawable", activity?.packageName)
            val viewId = resources.getIdentifier("img_post_${i + 1}", "id", activity?.packageName)

            val view = activity?.findViewById(viewId) as ImageView

            view.setImageResource(stampResId)
        }
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar?.toolbarMain)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }


    /**
     * Post is clicked.
     */
    fun onPostClick(view: View) {
        val tag = view.tag.toString().toInt()

        //send diary's idx with diary
        rxBus.takeBus(Pair(tag, todayDiaries[tag-1] ))

         PreviewDialogFragment().apply {
            previewDialogNavi = this@MainFragment
            show(this@MainFragment.fragmentManager, "preview")  //Show preview dialog
        }
    }


    /**
     * Add TipTap button is clicked.
     */
    fun onAddButtonClick() {
        if (todayDiaries.size < 10) {
            rxBus.takeBus(todayDiaries.size) //현재 다이어리의 사이즈를 보냄
            Intent(context, DiaryWritingActivity::class.java).apply {
                startActivity(this)
            }
        } else {
            //더이상 다이어리를 적을 수 없음.
        }
    }


    /**
     * Setting button is clicked
     */
    fun onSettingButtonClick() {
        startActivity(Intent(this@MainFragment.activity, SettingActivity::class.java))
    }

    /**
     * User click delete button on Dialog
     */
    override fun onDialogDeleteStart() {
        clearResources()
    }

    /**
     * delete is done
     */
    override fun onDialogDeleteComplete() {
        getTodayDiaries()
    }


    private fun clearResources() {
        todayDiaries.clear()
        postSize.set(0)
        disposables.clear()
    }


    override fun onStart() {
        super.onStart()

        getTodayDiaries()
    }


    override fun onStop() {
        super.onStop()


        clearResources()
    }

    override fun onDestroy() {
        super.onDestroy()

        clearResources()
        disposables.dispose()
    }
}
