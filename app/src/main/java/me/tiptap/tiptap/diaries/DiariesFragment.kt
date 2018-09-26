package me.tiptap.tiptap.diaries

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.view.DatePickerDialogFragment
import me.tiptap.tiptap.data.DiariesResponse
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private val adapter = DiariesAdapter()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val bus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private var totalPage = 0 //total page

    private lateinit var datePickerDialog: DatePickerDialogFragment

    val isBotDialogVisible = ObservableField<Boolean>(false)


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
            adapter = this@DiariesFragment.adapter.apply {

                getDiaries(1, 2)

                disposables.addAll(
                        clickSubject.subscribe {
                            //Go to detail page if actionMode is not running.
                            if (this.isCheckboxAvailable.get() == false) {
                                bus.takeBus(it) //send Diary to DiaryDetailActivity.
                                startActivity(Intent(this@DiariesFragment.activity, DiaryDetailActivity::class.java))
                            }
                        },
                        checkSubject.subscribe {
                            onCheckedChangeEventPublished(it)
                        })
            }
        }
    }

    private fun getDiaries(page: Int, limit: Int) {
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

                            t.data.list?.let {
                                if (page == 1) { //페이지가 1일 때만 날짜 바꾸기.
                                    applyDateOnToolbar(it[0].year, it[0].month)
                                }

                                for (monthDiary in it.iterator()) {
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
    }


    private fun applyDateOnToolbar(year: String, month: String) {
        val oldMonth = SimpleDateFormat("MM", Locale.US).parse(month)
        val newMonth = SimpleDateFormat("MMM", Locale.US).format(oldMonth)

        binding.toolbarDiaries?.textToolbarDiariesSub?.text =
                getString(R.string.last_year_month, year.substring(2, 4), newMonth)
    }


    fun onDateFindButtonClick() {
        if (!::datePickerDialog.isInitialized) {
            datePickerDialog = DatePickerDialogFragment()
        }

        activity?.let {
            datePickerDialog.show(it.supportFragmentManager, it.getString(R.string.app_name))
        }
    }

    /**
     * when click delete Icon
     */
    fun onDeleteMenuItemClick() {
        //checkbox mode on/off
        adapter.isCheckboxAvailable.get()?.let {
            isBotDialogVisible.set(!it) //change bottom dialog visibility
            adapter.isCheckboxAvailable.set(!it) //change checkbox available or not

            if (it) {
                adapter.changeCheckboxState(false)
            }
        }
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
                adapter.deleteCheckedItems() //delete items from adapter's dataSet
//                deleteDiary(adapter.checkedDataSet) //call deleteDiary api
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
//    private fun deleteDiary(dataSet: MutableList<String>) {
//        disposables.add(
//                service.deleteDiary(TipTapApplication.getAccessToken(), InvalidDiary(dataSet))
//                        .subscribeOn(Schedulers.io())
//                        .subscribeWith(object : DisposableObserver<JsonObject>() {
//                            override fun onComplete() {
//                                //
//                            }
//
//                            override fun onNext(t: JsonObject) {
//                                t.apply {
//                                    if (get(getString(R.string.code)).asString != "1000") { //if not successful.
//                                        Log.d(getString(R.string.desc),
//                                                getAsJsonObject(getString(R.string.data)).get(getString(R.string.desc)).asString)
//                                    }
//                                }
//                            }
//
//                            override fun onError(e: Throwable) {
//                                e.printStackTrace()
//                            }
//                        })
//        )
//    }


    override fun onDestroy() {
        super.onDestroy()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}