package com.stone.rx2.op.creator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.stone.rx2.R
import com.stone.rx2.SLog
import com.stone.rx2.op.BaseOpActivity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/8 20 30
 */
class CreatorActivity : BaseOpActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        test_create()
        test_just()
        test_fromArray()
        test_empty()
        test_interval()
        test_timer()
    }

    override fun init() {
        val sb = StringBuilder()
        sb.append("创建操作符: \n")
        sb.append("create \n")
        sb.append("just \n")
        sb.append("fromArray \n")
        sb.append("empty \n")
        sb.append("interval \n")
        sb.append("timer \n")
        mTv?.text = sb.toString()
    }

    private fun test_create() {
        /*
         * kotlin lambda 形式，与 java 中不一样；
         * java 中是 省略接口名，使用 参数{} 这样的形式；
         * kotlin 中是 接口名{} ，it 指代方法
         */

        /* ObservableOnSubscribe 当订阅时，内部唯一函数的参数ObservableEmitter 发送事件 */
//        val ob = Observable.create(ObservableOnSubscribe<String> {
        val ob = Observable.create(ObservableOnSubscribe<String> {
            it.onNext("stone")
            it.onComplete()
        })
        ob.subscribe {
            SLog.i(it)
        }

    }

    private fun test_just() {
        /* just 接收一到10个参数 */
        Observable.just(1, true, "3").subscribe {
            SLog.i(it.toString())
        }

        /* 泛型限定参数类型 */
        Observable.just<String>("a", "b").subscribe {
            SLog.i(it.toString())
        }
    }

    private fun test_fromArray() {
        var ary = arrayOf(1, 2, 3)
        /* 接收数组类型，不会自动展开并循环 */
        Observable.fromArray(ary).subscribe {
            it.forEach { SLog.i(it.toString()) }
        }
    }

    private fun test_empty() {
        /* 空的Observable，没有事件被发送，并立即调用onComplete(), 订阅不到事件 */
        Observable.empty<String>()
                .doOnComplete {
                    SLog.i("调用了 onComplete")
                }
                .subscribe {
                    SLog.i("Observable.empty")
                }
    }

    private fun test_interval() {
        /* 有多个重载函数；默认从0开始发送事件 */
//        var dis = Observable.interval( 200, 500, TimeUnit.MILLISECONDS)
        /* 间隔范围，可以指定开始值和数量 */
        var dis:Disposable = Observable.intervalRange(101, 5, 200, 500, TimeUnit.MILLISECONDS)
                .subscribe { SLog.i(it.toString()) }
    }

    private fun test_timer() {
        //类似 java.util.Timer
//        Observable.timer(100, TimeUnit.MILLISECONDS).subscribe()
    }

    fun testKtLambda() {
        xxx { it.tttt(1, 2) }
    }

    //    fun xxx(aa: (x:AA) -> Unit) {
    fun xxx(aa: (AA) -> Int) {
        aa.apply { }
    }

    interface AA {
        fun tttt(a: Int, b: Int): Int
    }
}