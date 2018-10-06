package me.tiptap.tiptap.preview

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import io.reactivex.disposables.CompositeDisposable
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.FragmentDialogPreviewBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity

class PreviewDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogPreviewBinding

    private val rxBus = RxBus.getInstance()
    private val disposable = CompositeDisposable()

    private lateinit var data: Diary


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        checkBus()
    }

    private fun checkBus() {
        disposable.add(rxBus.toObservable().subscribe {
            if (it is Diary) {
                data = it
            }
        })
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //For full screen dialog.
        val root: ConstraintLayout = ConstraintLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        return Dialog(activity, R.style.PreviewDialogAnimation).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(root)

            window.run {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_preview, container, false)
        binding.apply {
            diary = data
            textPreviewCount.text = getString(R.string.my_diary_count, data.id) //temp
        }

        initToolbar()

        return binding.root
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbarPreview)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setHomeAsUpIndicator(R.drawable.ic_back_white)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        if (menu?.size() == 0) { //메뉴가 여러 번 생성되지 않도록 방지함.
            inflater?.inflate(R.menu.menu_preview, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_preview_edit -> {
                    rxBus.takeBus(data) //send be edited Diary.
                    startActivity(Intent(activity, DiaryWritingActivity::class.java))
                    true
                }
                R.id.menu_preview_delete -> {
                    this.dialog.dismiss()
                    true
                }
                else -> false
            }


    override fun onDestroy() {
        super.onDestroy()

        disposable.dispose()
    }
}