package me.tiptap.tiptap.scratch

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import me.tiptap.tiptap.R
import me.tiptap.tiptap.databinding.FragmentScratchBinding
import java.util.*


class ScratchFragment : Fragment() {

    private lateinit var binding : FragmentScratchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)

        takeScreenshot()
        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchCard?, percent: Float) {
                Log.d("ScratchPer", percent.toString())
                if(percent == 1.0f) {
                    Log.d("ScratchPer", "Done!")
                    fadeOutAnimation(binding.scratch, 500)
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.textLittlebit.visibility = View.INVISIBLE
                    binding.textScratch.visibility = View.INVISIBLE
                }
            }

            override fun onRevealed(tv: ScratchCard) {
                Log.d("ScratchPer", "Hello SCratch")
            }

        })

        return binding.root

    }

    fun fadeOutAnimation(view: View, animationDuration: Long) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = animationDuration
        fadeOut.duration = animationDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        view.startAnimation(fadeOut)
    }

    private fun takeScreenshot() {
        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {

            val v1 = activity?.window?.findViewById<ViewGroup>(R.id.layout)

            v1?.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(v1?.drawingCache)
            v1?.isDrawingCacheEnabled = false

            ScratchCard.scratchBitmap = bitmap

        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
    }
}



