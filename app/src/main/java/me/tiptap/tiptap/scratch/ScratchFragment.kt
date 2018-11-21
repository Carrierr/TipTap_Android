package me.tiptap.tiptap.scratch

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.databinding.ObservableInt
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
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
import me.tiptap.tiptap.common.view.ScratchCard
import me.tiptap.tiptap.data.DiaryResponse
import me.tiptap.tiptap.databinding.FragmentScratchBinding


class ScratchFragment : Fragment() {

    private lateinit var binding: FragmentScratchBinding

    private val service = ServerGenerator.createService(DiaryApi::class.java)
    private val disposables = CompositeDisposable()

    private val adapter = SharingAdapter()

    val postSize = ObservableInt(0)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scratch, container, false)
        binding.postSize = postSize

        initBind()

        setShareMainLayoutSize() // Change share main container's height

        return binding.root
    }


    private fun initBind() {
        binding.scratch.setRevealListener(object : ScratchCard.IRevealListener {
            override fun onRevealPercentChangedListener(siv: ScratchCard, percent: Float) {
                if (percent <= 0.2f && !siv.isRevealed) {
                    siv.mRevealPercent = 1.0f
                }
            }

            override fun onRevealed(iv: ScratchCard) {
                activity?.runOnUiThread {
                    iv.fadeOutAnimation(binding.scratch, 300)
                }
                iv.isRevealed = true

                getShareDiary() //get Share diary if scratch is revealed.
            }
        })
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


    private fun getShareDiary() {
        disposables.add(
                service.shareDiaries(TipTapApplication.getAccessToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .filter { t -> t.code == "1000" }
                        .subscribeWith(object : DisposableObserver<DiaryResponse>() {
                            override fun onNext(t: DiaryResponse) {
                                adapter.updateItems(t.data.diaries)
                            }

                            override fun onComplete() {
                                postSize.set(adapter.itemCount)

                                binding.layoutScratchMain.apply {
                                    textScratchMainNum?.text = getString(R.string.count_tiptap, adapter.itemCount)
                                    textScratchMainLocation?.text = adapter.getItem(0).location
                                }
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }
                        })
        )
    }


    private fun initRecyclerView() {
        binding.recyclerSharing.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(this@ScratchFragment.context)

            isNestedScrollingEnabled = false
            adapter = this@ScratchFragment.adapter
        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (!isVisibleToUser && this::binding.isInitialized) { //if share diary already loaded. and there's more to load
            binding.scratch.redrawCover()

            adapter.deleteAllItems()
            postSize.set(0)

        }
    }

    override fun onStop() {
        super.onStop()

        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }
}