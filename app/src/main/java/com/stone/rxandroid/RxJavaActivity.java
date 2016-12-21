package com.stone.rxandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rxandroid.rxjava.MyObservable;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 */

public class RxJavaActivity extends Activity {

    private String tag = this.getClass().getSimpleName();

    MyObservable test;

    Observer<String> mObserver1 = new Observer<String>() {

        @Override
        public void onCompleted() {
            Log.d(tag, "Completed!");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(tag, "Error!");
        }

        @Override
        public void onNext(String s) {
            Log.d(tag, "Item: " + s);
            System.out.println("onNext" + Thread.currentThread().getId());
        }
    };

    Subscriber<String> mSubscriber1 = new Subscriber<String>() {
        @Override
        public void onNext(String s) {//必须实现的抽象方法
            Log.d(tag, "Item: " + s);
        }

        @Override
        public void onCompleted() {//必须实现的抽象方法
            Log.d(tag, "Completed!");
        }

        @Override
        public void onError(Throwable e) {//必须实现的抽象方法
            Log.d(tag, "Error!");
        }

        @Override
        public void onStart() {//事件还未发送之前被调用   非必须重写方法
            super.onStart();
            Log.d(tag, "onStart!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test = new MyObservable();

//        Observable observable1 = test.testNormal();
//        Observable observable1 = test.testJust();
        Observable observable1 = test.testFrom();
        test.testSubscribe(observable1);
        observable1.subscribeOn(Schedulers.immediate());
//        Subscription subscription = observable1.subscribe(mObserver1);//订阅并执行call
//        subscription.unsubscribe(); //取消订阅

        setContentView(R.layout.acti_main);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        test.setImage(iv, R.drawable.a222);

        test.testScheduler();

        test.testMap(this);

        test.testFlatMap();

        test.testLift();

        test.testCompose();

        test.testScheduleMethod();
    }
}
