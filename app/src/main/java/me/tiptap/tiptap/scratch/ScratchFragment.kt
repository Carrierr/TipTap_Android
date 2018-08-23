package me.tiptap.tiptap.scratch

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import io.reactivex.disposables.CompositeDisposable
import me.tiptap.tiptap.R
import me.tiptap.tiptap.common.rx.RxBus
import me.tiptap.tiptap.data.Diary
import me.tiptap.tiptap.databinding.FragmentScratchBinding
import me.tiptap.tiptap.diaries.DiariesAdapter
import me.tiptap.tiptap.diarydetail.DiaryDetailActivity
import java.util.*


class ScratchFragment : Fragment() {

    private lateinit var binding: FragmentScratchBinding
    private val bus = RxBus.getInstance()
    private val disposables: CompositeDisposable = CompositeDisposable()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)

        setShareMainLayoutSize() // Change share main container's height

        binding.layoutScratchMain?.textScratchMainNum?.text = getString(R.string.count_tiptap, 0) //temp

        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(stv: ScratchCard?, percent: Float) {
                Log.d("ScratchPer", percent.toString())
                if (percent == 1.0f) {
                    Log.d("ScratchPer", "Done!")
                    fadeOutAnimation(binding.scratch, 300)
                }
            }

            override fun onRevealed(tv: ScratchCard) {
                Log.d("ScratchPer", "Hello SCratch")
            }

        })
        /*binding.listScratch.apply {
            divider = null
            dividerHeight = 0
            adapter = ListViewAdapter(context)
        }*/

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
    }

    /**
     * Change Share main layout container's height to screen size.
     */
    private fun setShareMainLayoutSize() {
        val point = Point()

        activity?.windowManager?.defaultDisplay?.getSize(point)

        binding.containerScratchMain.layoutParams.height = point.y //change height
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


    private fun initRecyclerView() {
        binding.recyclerDiaries.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@ScratchFragment.context)
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
                                startActivity(Intent(this@ScratchFragment.activity, DiaryDetailActivity::class.java))
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

}






