package com.stone.rx2.origin;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.stone.rx2.R;
import com.stone.rx2.rxbus.RxBus;

import io.reactivex.Flowable;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 24/04/2017 22 03
 */
public class SecondActi extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acti_main);

        for (int i = 0; i < 10000; i++) {
            Flowable<Float> f2 = RxBus.getInstance().register(Float.class);
            f2.doOnComplete(() -> System.out.println("f2 doOnComplete"))
                .subscribe(value -> System.out.println("订阅f2消息 .. " + value));
            new Handler().postDelayed(()-> {
                finish();
            }, 1000);
        }
//
//        RxBus.getInstance().post(7777);



//        RxBus.getInstance().unregisterAll();
    }
}
