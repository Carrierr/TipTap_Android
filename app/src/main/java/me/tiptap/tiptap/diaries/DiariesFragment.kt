package me.tiptap.tiptap.diaries

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import me.tiptap.tiptap.data.DiariesResponse
import me.tiptap.tiptap.data.InvalidDiaries
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private var adapter = DiariesAdapter()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val rxBus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private var totalPage = 0 //total page

    val isBotDialogVisible = ObservableBoolean(false)
    val isDateRangeAvailable = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diaries, container, false)
        binding.fragment = this

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()

    }

    private fun checkBus() {
        rxBus.toObservable()
                .subscribe {
                    if (it is Pair<*, *>) {
                        val startDate = it.first
                        val endDate = it.second

                        isDateRangeAvailable.set(true)
                        binding.layoutBotRange?.textBotRange?.text = getString(R.string.date_range, startDate, endDate)
                    }
                }.dispose()
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarDiaries?.toolbarDiaries)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerDiaries.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@DiariesFragment.context)

            this@DiariesFragment.adapter = DiariesAdapter().apply {
                adapter = this

                disposables.addAll(
                        clickSubject.subscribe {
                            //Go to detail page if actionMode is not running.
                            if (this.isCheckboxAvailable.get() == false) {
                                rxBus.takeBus(it) //send Diary's date to DiaryDetailActivity.
                                startActivity(Intent(this@DiariesFragment.activity, DiaryDetailActivity::class.java))
                            }
                        },
                        longClickSubject.subscribe {
                            this.startDeleteMode(it)
                            isBotDialogVisible.set(!it) //change bottom dialog visibility
                        },
                        checkSubject.subscribe {
                            onCheckedChangeEventPublished(it)
                        })
            }
        }
    }

    private fun getDiaries(page: Int, limit: Int) {
        disposables.add(
                service.diaryList(TipTapApplication.getAccessToken(), page, limit) //한번에 하나의 달만 불러올거.
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<DiariesResponse>() {
                            override fun onComplete() {
                                adapter.notifyDataSetChanged()
                            }

                            override fun onNext(t: DiariesResponse) {
                                if (t.code == "1000") {
                                    totalPage = t.data.total

                                    val list = t.data.list

                                    if (list.isNotEmpty()) {
                                        for (monthDiary in list.iterator()) {
                                            if (monthDiary.diariesOfDay != null)
                                                adapter.addItems(monthDiary.diariesOfDay) //add items
                                        }
                                    }
                                }
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
        )
    }


    fun onDateFindButtonClick() {
        startActivity(Intent(this@DiariesFragment.activity, CalendarActivity::class.java))
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
                adapter.run {
                    this.deleteCheckedItems() //delete items from adapter's dataSet
                    deleteDiaries(this.checkedDataSet) //call deleteDiary api
                }
            }
        }

        adapter.apply {
            isCheckboxAvailable.set(false) //change checkbox state
            changeCheckboxState(false)
        }

        isBotDialogVisible.set(false) //hide bottom dialog
    }


    /**
     * call delete diary api
     */
    private fun deleteDiaries(dataSet: MutableList<Date>) {
        val invalidDiaries = InvalidDiaries().apply {
            convertDateToString(dataSet)
        }

        disposables.add(
                service.deleteDiaryByDay(TipTapApplication.getAccessToken(), invalidDiaries)
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(object : DisposableObserver<JsonObject>() {
                            override fun onComplete() {
                                //
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

    override fun onResume() {
        super.onResume()

        checkBus() //사용자가 날짜 범위를 선택했는지 여부 확인함.
        initRecyclerView()

        getDiaries(1, 2)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}