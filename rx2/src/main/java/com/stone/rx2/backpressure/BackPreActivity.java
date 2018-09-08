package com.stone.rx2.backpressure;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 25/04/2017 13 11
 */
public class BackPreActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        testPressureToCrash();

    }

    private void testPressureToCrash() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(aLong -> {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    Log.w("TAG", "---->" + aLong);
                });

        /*
        在2.0 中，Observable 不再支持背压，而Flowable 支持非阻塞式的背压。
        Flowable是RxJava2.0中专门用于应对背压（Backpressure）问题。
        所谓背压，即生产者的速度大于消费者的速度带来的问题，比如在Android中常见的点击事件，
        点击过快则经常会造成点击两次的效果。其中，Flowable默认队列大小为128。并且规范要求，所有的操作符强制支持背压
         */
    }
}
