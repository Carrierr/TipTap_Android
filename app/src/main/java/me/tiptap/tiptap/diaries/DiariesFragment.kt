package me.tiptap.tiptap.diaries

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.view.EndlessRecyclerViewScrollListener
import me.tiptap.tiptap.data.InvalidDiaries
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private val adapter = DiariesAdapter()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val rxBus = RxBus.getInstance()
    private val disposables = CompositeDisposable()

    private var totalPage = 1//total page
    private var curPage = 1
    private val limit = 2

    val isDeleteMode = ObservableBoolean(false)
    val isDateRangeMode = ObservableBoolean(false)
    val isDiaryExist = ObservableBoolean(true)

    private val isPreViewMode = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diaries, container, false)
        binding.fragment = this

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        initRecyclerView()
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarDiaries.toolbarDiaries)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }


    private fun initRecyclerView() {
        binding.recyclerDiaries.apply {
            layoutManager = LinearLayoutManager(this@DiariesFragment.context)

            adapter = this@DiariesFragment.adapter.apply {
                setHasStableIds(true)
            }

            setHasFixedSize(true)

            addOnScrollListener(object : EndlessRecyclerViewScrollListener(this.layoutManager as LinearLayoutManager, limit) {
                override fun onLoadMore(rv: RecyclerView, page: Int, totalItemCnt: Int) {
                    if (curPage <= totalPage && !isDateRangeMode.get()) {
                        getDiaries(curPage, limit)
                    }
                }
            })

            initRecyclerViewAdapterEvent() //initialize recyclerView's adapter event
        }
    }

    private fun initRecyclerViewAdapterEvent() {
        adapter.apply {
            disposables.addAll(
                    clickSubject.subscribe {
                        if (!isDeleteMode.get()) {   //Go to detail page if delete mode is not running.
                            rxBus.takeBus(it) //send Diary's date to DiaryDetailActivity.
                            isPreViewMode.set(true)

                            startActivity(Intent(this@DiariesFragment.activity, DiaryDetailActivity::class.java))
                        }
                    },

                    longClickSubject.subscribe {
                        changeDeleteModeState(it)
                        isDeleteMode.set(it) //change bottom dialog visibility
                    },

                    checkSubject.subscribe {
                        updateCheckedItems(it) //checkEvent is published.
                    }
            )
        }
    }

    private fun checkBus() {
        rxBus.toObservable()
                .subscribe {
                    if (it is ArrayList<*>) { //from the CalendarActivity.
                        val startDate = it[0].toString()
                        val endDate = it[1].toString()

                        isDateRangeAvailable.set(true)
                        binding.layoutBotRange.textBotRange.text = getString(R.string.date_range, startDate, endDate)

                        getDiariesByDate(startDate, endDate)

                    } else if (it is Boolean) { //from the MainFragment.
                        getDiaries(curPage, 2)
                    }
                }
                .dispose()
    }

    /**
     * Get diaries by date range.
     */
    private fun getDiariesByDate(startDate: String, endDate: String) {
        disposables.add(
                service.getDiariesByDate(TipTapApplication.getAccessToken(), startDate, endDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { adapter.deleteAllItems() }
                        .doOnError { e -> e.printStackTrace() }
                        .filter { task -> task.code == "1000" } //if successful
                        .subscribe { t ->
                            val list = t.data.list

                            if (list.size > 0) {
                                isDiaryExist.set(true)

                                for (monthDiary in list.iterator()) {
                                    if (monthDiary.diariesOfDay != null) {
                                        adapter.addItems(monthDiary.diariesOfDay) //update items
                                    }
                                }
                            } else { //there's no diary.
                                isDiaryExist.set(false)
                                adapter.deleteAllItems()
                            }
                        }
        )
    }


    private fun getDiaries(page: Int, limit: Int) {
        disposables.add(
                service.getDiaries(TipTapApplication.getAccessToken(), page, limit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            curPage++
                        }
                        .doOnComplete {
                            if (binding.swipeDiaries.isRefreshing) { //refresh is done.
                                binding.swipeDiaries.isRefreshing = false
                            }
                        }
                        .doOnError { e -> e.printStackTrace() }
                        .filter { task -> task.code == "1000" }
                        .subscribe { t ->
                            if (page == 1) { //If call this api first time,
                                totalPage = t.data.total
                            }

                            val list = t.data.list

                            if (list.size > 0) {
                                isDiaryExist.set(true)

                                for (monthDiary in list.iterator()) {
                                    if (monthDiary.diariesOfDay != null) {
                                        adapter.addItems(monthDiary.diariesOfDay) //add items
                                    }
                                }
                            } else {
                                isDiaryExist.set(false)
                            }
                        })
    }


    fun onDateFindButtonClick() {
        adapter.changeDeleteModeState(false)
        isDeleteMode.set(false)

        //Only start CalendarActivity if you have a diary.
        if (!isDiaryExist.get()) return else startActivity(Intent(this@DiariesFragment.activity, CalendarActivity::class.java))
    }


    fun onDateClearButtonClick() {
        isDateRangeMode.set(false) //dateRange mode set false
        isDeleteMode.set(false)

        adapter.deleteAllItems()

        curPage = 1
        getDiaries(curPage, limit)
    }

    /**
     * SwipeRefresh
     */
    fun onRefresh() {
        resetAllMode()

        getDiaries(curPage, limit)
    }

    /**
     *  When click bottom dialog button
     */
    fun onBottomDialogButtonClick(view: View) {
        when (view.id) {
            R.id.text_bot_dial_cancel -> {
                //clicked cancel
            }
            R.id.text_bot_dial_delete -> {
                adapter.apply {
                    deleteCheckedItems() //delete items from adapter's dataSet
                    this@DiariesFragment.deleteDiaries(this.checkedDataSet) //call deleteDiary api
                }
            }
        }

        adapter.changeDeleteModeState(false)

        isDeleteMode.set(false) //hide bottom dialog
    }


    /**
     * call delete diary api
     */
    private fun deleteDiaries(dataSet: MutableList<Date>) {
        val invalidDiaries = InvalidDiaries().apply {
            convertDateToString(dataSet) //change Date type to String.
        }

        disposables.add(
                service.deleteDiaryByDay(TipTapApplication.getAccessToken(), invalidDiaries)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete { if (adapter.itemCount == 0) isDiaryExist.set(false) }
                        .doOnError { e -> e.printStackTrace() }

                        .filter { task -> task.get(getString(R.string.code)).asString != "1000" } //if success,
                        .subscribe { t ->
                            Log.d(getString(R.string.desc), t.getAsJsonObject(getString(R.string.data)).get(getString(R.string.desc)).asString)
                        })
    }

    /**
     * This method is called when a fragment is visible or not.
     * if ViewPagerAdapter's getItem return this fragment, value is true.
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            if (!isDateRangeAvailable.get()) { //not date range mode,
                if (disposables.size() == 0) {
                    initRecyclerViewAdapterEvent()
                }

                checkBus()
            }

        } else {
            resetAllMode() //resources will be no longer used.
            disposables.clear()
        }
    }


    private fun resetAllMode() {
        curPage = 1 //set current page 1.

        adapter.also {
            if (isDeleteMode.get()) { //if checkBox is shown -> gone.
                it.changeDeleteModeState(false)
            }

            it.deleteAllItems()
        }

        isDeleteMode.set(false)  //bottom delete dialog
        isDateRangeMode.set(false)  //date range mode
    }


    override fun onResume() {
        super.onResume()

        userVisibleHint = true

        if (isPreViewMode.get()) { //preView dialog was opened.
            isPreViewMode.set(false)
        }

        if (disposables.size() == 0) { //disposable is cleared.
            initRecyclerViewAdapterEvent() //re initialize recyclerView's adapter event.
        }
    }

    override fun onPause() {
        super.onPause()

        if (!isPreViewMode.get()) { //fragment is paused,
            resetAllMode()

            disposables.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}