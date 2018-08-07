package me.tiptap.tiptap.common.rx

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class RxBus {

    private val subject = BehaviorSubject.create<Any>()


    companion object {
        private val rxBus = RxBus()

        fun getInstance(): RxBus = rxBus
    }


    fun takeBus(task: Any) = subject.onNext(task)


    fun toObservable(): Observable<Any> = subject

}