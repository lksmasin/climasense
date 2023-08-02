package org.climasense.common.utils.helpers

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

object AsyncHelper {
    fun <T> runOnIO(
        task: (emitter: Emitter<T>) -> Unit,
        callback: (t: T, done: Boolean) -> Unit
    ): Controller {
        return Controller(
            Observable.create { emitter: ObservableEmitter<Data<T>> ->
                task(Emitter(emitter))
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { data: Data<T> -> callback(data.t, data.done) }
                .subscribe()
        )
    }

    fun runOnIO(runnable: Runnable): Controller {
        return Controller(
            Observable.create { emitter: ObservableEmitter<Any>? -> runnable.run() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun <T> runOnExecutor(
        task: (emitter: Emitter<T>) -> Unit,
        callback: (t: T, done: Boolean) -> Unit,
        executor: Executor
    ): Controller {
        return Controller(
            Observable.create { emitter: ObservableEmitter<Data<T>> ->
                task(Emitter(emitter))
            }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { data: Data<T> -> callback(data.t, data.done) }
                .subscribe()
        )
    }

    fun runOnExecutor(runnable: Runnable, executor: Executor): Controller {
        return Controller(
            Observable.create { emitter: ObservableEmitter<Any>? -> runnable.run() }
                .subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    fun delayRunOnIO(runnable: Runnable, milliSeconds: Long): Controller {
        return Controller(
            Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .doOnComplete { runnable.run() }
                .subscribe()
        )
    }

    fun delayRunOnUI(runnable: Runnable, milliSeconds: Long): Controller {
        return Controller(
            Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { runnable.run() }
                .subscribe()
        )
    }

    fun intervalRunOnUI(
        runnable: Runnable,
        intervalMilliSeconds: Long, initDelayMilliSeconds: Long
    ): Controller {
        return Controller(
            Observable.interval(initDelayMilliSeconds, intervalMilliSeconds, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { runnable.run() }
        )
    }

    class Controller internal constructor(val inner: Disposable) {
        fun cancel() {
            inner.dispose()
        }
    }

    class Data<T> internal constructor(val t: T, val done: Boolean)
    class Emitter<T> internal constructor(inner: ObservableEmitter<Data<T>>) {
        val inner: ObservableEmitter<Data<T>>

        init {
            this.inner = inner
        }

        fun send(t: T, done: Boolean) {
            inner.onNext(Data(t, done))
        }
    }
}
