package me.tiptap.tiptap.main

import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
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
import me.tiptap.tiptap.setting.SettingActivity
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val rxBus = RxBus.getInstance()
    val postSize: ObservableField<Int> = ObservableField(1) //post size


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.apply {
            fragment = this@MainFragment
            date = Date()

            btnMainAdd.setOnClickListener {
                //dummy
                rxBus.takeBus(Diary(1, "하이룽용", "우리 집 내 방", "236.8067020990290", "126.979874")) //Send current diary count

                Intent(context, DiaryWritingActivity::class.java).apply {
                    startActivity(this)
                }
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

}
