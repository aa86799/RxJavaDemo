package com.stone.rx2.my

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stone.rx2.SLog
import com.stone.rx2.my.rxjava.Observable
import com.stone.rx2.my.rxjava.ObservableOnSubscribe
import com.stone.rx2.my.rxjava.Observer

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/9 22 05
 */
class MyRxJavaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Observable.create(ObservableOnSubscribe<String> {
            it.onNext("http:www.dongnbao/head.png")
            SLog.i("subscribe: 1  ")
        })
                .subscribe(object : Observer<String>() {
                    override fun onNext(t: String?) {
                        SLog.i("onNext: $t")
                    }

                    override fun onError(e: Throwable?) {
                        SLog.i("onError")
                    }

                    override fun onComplete() {
                        SLog.i("onComplete")
                    }

                })
        /*
         * 上面，create 中构建一个 ObservableOnSubscribe，内部函数以 Observer<String>作为参数；
         * subscribe()，调用 ObservableOnSubscribe 实例 的内部函数，传入 object : Observer；
         *
         */


        Observable.create(ObservableOnSubscribe<String> {
            it.onNext("http:www.dongnbao/head.png")
            SLog.i("subscribe: 1  ")
        }).map {
            SLog.i("map $it")
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }.subscribe(object : Observer<Bitmap>() {
            override fun onNext(t: Bitmap?) {
                SLog.i("onNext: " + t)
            }

            override fun onError(e: Throwable?) {
                SLog.i("onError")
            }

            override fun onComplete() {
                SLog.i("onComplete")
            }

        })

        /*
         * 上面，create 中构建一个 ObservableOnSubscribe，内部函数以 Observer<String>作为参数；
         * map 变换，又重新构建一个Observable；
         * 参数 OnSubscribeLift ，它继承自 ObservableOnSubscribe；
         * OnSubscribeLift 又由 之前 ObservableOnSubscribe实例和 传入的函数 参与构造；
         * 当最后的subscribe执行，会调用 OnSubscribeLift#subscribe()；
         *      内部，调用 map 变换，该变换返回一个 Observer ob；
         *      同时调用 parent-ObservableOnSubscribe #subscribe(ob);
         *      parent 中subscribe()，触发 OnSubscribeLift#ob=MapSubscribe#onNext(){
         *          最终订阅的Observer#apply((map 函数结果))，返回变换类型实例；
         *          最后再调用，最终订阅的 Observer#onNext(变换类型实例)
         *
         *      }
         *
         * 特点：
         *      变换时，构造一个新的 Observable，其持有旧的 Observable 和 变换函数体；
         *      最终订阅，触发 旧Observable的订阅，传递 变换中产生的 Observer;
         *      触发变换中的 Observer#onNext(传递原始的变换类型实例)；
         *      执行变换函数，获得新类型实例；
         *      最后执行，最终的 Observer#onNext(新类型实例)
         * 总结：
         *      为什么说链式处理呢？
         *      每个变换都会构建一个新的 被观察者，其持有上一级 变换的 被观察者，且有自身的处理；
         *      所以总体是一个 责任链模式的实现；
         */
    }
}