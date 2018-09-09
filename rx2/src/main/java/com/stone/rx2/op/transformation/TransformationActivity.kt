package com.stone.rx2.op.transformation

import android.os.Bundle
import com.stone.rx2.SLog
import com.stone.rx2.op.BaseOpActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.flowables.GroupedFlowable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/8 20 43
 */
class TransformationActivity : BaseOpActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        test_map()
        test_flatMap()
        test_groupBy()
        test_buffer()
        test_scan()
        test_window()
        test_lift()
    }

    override fun init() {
        val sb = StringBuilder()
        sb.append("变换操作符: \n")
        sb.append("map \n")
        sb.append("flatMap \n")
        sb.append("groupBy \n")
        sb.append("buffer \n")
        sb.append("scan \n")
        sb.append("window \n")
        mTv?.text = sb.toString()
    }

    private fun test_map() {
        /* 类型转换 */
        Flowable.just<String>("110").map { it.toInt() }.subscribe { SLog.i((it - 10).toString()) }
    }

    private fun test_flatMap() {
//        Flowable.just(1,2).flatMap(object :Function< Int,  Publisher<out String>> {
//            override fun apply(t: Int): Publisher<out String> {
//
//            }
//        })

        /* 遍历 Observable中的一组数据 */
        /*Flowable.just(1, 2, 3, 4, 5)
                //t 表示 Function#apply()的参数
                .flatMap(Function<Int, Publisher<out String>> { t ->
                    Publisher {
                        if (t < 3)
                            Thread.sleep(1234)
                        it.onNext("$t-stone")
                    }
                })
                .subscribe { SLog.i(it) }*/

        Observable.just(1, 2, 3, 4, 5)
                //t 表示 Function#apply()的参数
                .flatMap(Function<Int, ObservableSource<out String>> { t ->
                    ObservableSource {
                        it.onNext("flatMapA$t")
                        if (t < 3)
                            Thread.sleep(333)
                        it.onNext("flatMapB$t")
                    }
                })
                .subscribe { SLog.i(it) }
    }

    private fun test_groupBy() {
        /* 将发射数据，进行一定处理/变换，注意其结果类型；返回 GroupedFlowable<K,T> ；
        *  K: groupBy 变换后类型，T 原始类型；
        *  订阅后，需要再调用 订阅方法，因一次订阅到的还是一个 Flowable
        * */
        Flowable.just(1,2, 3,4)
                .groupBy { it * 10L}
                .flatMap(Function<GroupedFlowable<Long, Int>, Publisher<out Flowable<String>>> {group ->
                    Publisher {
                        it.onNext(Flowable.just(group.key.toString() + "groupBy"))
                    }
                })
                .subscribe { it.subscribe { SLog.i(it) }  }
    }

    private fun test_buffer() {
        /* 指定缓存 一定数量的事件，返回 Flowable<List<T>> */
        Flowable.just(1,2, 3,4)
                .buffer(2)
                .subscribe { it.forEach { SLog.i("buffer " + it.toString()) } }
    }

    private fun test_scan() {
        /* 每项应用一个函数； 值是累积的；
         * 第一项返回自身，若有初始值，则 t2=初始值；
         * 之后，t1=前一个值，t2=当前元素
         */
        Flowable.just(1,2, 3,4)
//                .scan(BiFunction<Int, Int, Int> {
                .scan(10, BiFunction<Int, Int, Int> {
                    t1, t2 ->
                    t1 * t2
                })
                .subscribe { SLog.i("scan " + it.toString()) }
    }

    private fun test_window() {//不常用
        /* 间隔指定时间，在指定线程，处理指定数量的事件
         * 需要二次订阅，才能订阅到数据
         * 实测发现，上游流速快慢，对下游的处理时间有影响；下游的时间再长，也没什么影响
         */
        Observable.intervalRange(1, 10, 1, 100, TimeUnit.MILLISECONDS)
                .window(50, TimeUnit.MILLISECONDS, /*Schedulers.io()*/ AndroidSchedulers.mainThread(), 10, false, 2)
                .subscribe {
                    it.subscribe{ SLog.i("window " + it.toString()) }
                }
    }

    private fun test_lift() {
        /*
         * lift 变换是一种基础变换；
         * 需要 ObservableOperator 类型参数，参与；
         * ObservableOperator 需要 返回 Observer 类型，且有一个 Observer 类型的参数；
         *     即 将原来的 观察者，进行变换后，返回新的观察者，再订阅
         */
        Observable.intervalRange(1, 10, 1, 300, TimeUnit.MILLISECONDS)
                .lift(ObservableOperator<String, Long> { observer ->
                    object : Observer<Long> {
                        override fun onNext(t: Long) {
                            observer.onNext(t.toString() + " lift")
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }
                    }
                })
                .subscribe { SLog.i(it) }

    }
}