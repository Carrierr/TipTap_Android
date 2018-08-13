package me.tiptap.tiptap.scratch

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentScratchBinding




class ScratchFragment : Fragment() {

    private lateinit var binding : FragmentScratchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)

        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchCard?, percent: Float) {
                Log.d("ScratchPer", percent.toString())
                if(percent >= 0.7f) {
                    Log.d("ScratchPer", "Done!")
                    val anim = AlphaAnimation(1.0f, 0.0f)
                    anim.duration = 1000
                    anim.fillAfter = true
                    binding.scratch.startAnimation(anim)

                }
            }

            override fun onRevealed(tv: ScratchCard) {
                Log.d("ScratchPer", "Hello SCratch")
            }

        })
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

