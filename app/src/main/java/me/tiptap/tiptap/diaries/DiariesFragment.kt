package me.tiptap.tiptap.diaries

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentDiariesBinding

class DiariesFragment : Fragment() {

    private lateinit var binding: FragmentDiariesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diaries, container, false)

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

                clickSubject.subscribe {
                    //go to detail page.
                }
            }
        }
    }


}