package me.tiptap.tiptap.diaries

import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import io.reactivex.disposables.CompositeDisposable
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.common.view.DatePickerDialogFragment
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.FragmentDiariesBinding
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    private val bus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

    private lateinit var datePickerDialog : DatePickerDialogFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diaries, container, false)
        binding.fragment = this

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
    }


    private fun initRecyclerView() {
        binding.recyclerDiaries.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@DiariesFragment.context)
            adapter = DiariesAdapter().apply {

                //Dummy data
                for (i in 1..15) {
                    addItem(Diary(i, Date(), "내용 $i", "장소 $i", Uri.parse("none")))
                }

                disposables.addAll(
                        clickSubject.subscribe {
                            //Go to detail page if actionMode is not running.
                            if (this.actionModeCallback == null) {
                                bus.takeBus(it)
                                startActivity(Intent(this@DiariesFragment.activity, DiaryDetailActivity::class.java))
                            }
                        },
                        longClickSubject.subscribe {
                            onLongClickEventPublished(it)
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

    override fun onPause() {
        super.onPause()

        //Dispose subjects to prevent memory leak.
        disposables.dispose()
    }
}