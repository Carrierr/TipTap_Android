package me.tiptap.tiptap.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.FragmentMainBinding
import me.tiptap.tiptap.diarywriting.DiaryWritingActivity
import me.tiptap.tiptap.preview.PreviewDialogFragment
import me.tiptap.tiptap.setting.SettingActivity
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val rxBus = RxBus.getInstance()
    val postSize: ObservableField<Int> = ObservableField(0) //post size


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.apply {
            fragment = this@MainFragment
            date = Date()
            btnMainAdd.setOnClickListener {
                val intent = Intent(context, DiaryWritingActivity::class.java)
                startActivity(intent)
            }
        }

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar?.toolbarMain)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar?.apply {
            textToolbarMainTitle.text = getString(R.string.today)
            textToolbarMainSub.text = getString(R.string.post_count, postSize.get().toString())
        }
    }

    fun onSettingButtonClick() {
        startActivity(Intent(this@MainFragment.activity, SettingActivity::class.java))
    }


    fun onPostClick(view: View) {
        rxBus.takeBus(Diary(1, Date(), "하늘을 우러러 한점 부끄럼 없기를\n잎새이는 바람에도 나는 괴로워 했다.",
                "키오스크 망원동 카페", Uri.parse("hi")))

        PreviewDialogFragment().show(fragmentManager, "Preview")
    }


}
