package me.tiptap.tiptap.diaries

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import me.tiptap.tiptap.R
import me.tiptap.tiptap.TipTapApplication
import me.tiptap.tiptap.common.network.DiaryApi
import me.tiptap.tiptap.common.network.ServerGenerator
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.view.DatePickerDialogFragment
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.data.InvalidDiary
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private val adapter = DiariesAdapter()

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val bus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

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

                //Dummy data
                for (i in 1..15) {
                    addItem(Diary(i, Date(), "내용 $i", "서울대입구 $i 번 출구 앞", Uri.parse("none")))
                }
                binding.date = getItem(0).date

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
                deleteDiary(adapter.checkedDataSet) //call deleteDiary api
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
    private fun deleteDiary(dataSet: MutableList<Int>) {
        disposables.add(
                service.deleteDiary(TipTapApplication.getAccessToken(), InvalidDiary(dataSet))
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


    override fun onDestroy() {
        super.onDestroy()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}