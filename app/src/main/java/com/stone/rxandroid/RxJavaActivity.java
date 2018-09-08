package com.stone.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rxandroid.rxjava.MyObservable;


import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 */

public class RxJavaActivity extends AppCompatActivity {

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

        setContentView(R.layout.acti_main);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        test = new MyObservable();
        test.setImage(iv, R.drawable.a222);

        test.test();

        Observable observable1 = test.testNormal();
        Observable observable2 = test.testJust();
        Observable observable3 = test.testFrom();
        test.testSubscribe(observable2);
        observable1.subscribeOn(Schedulers.immediate());
        Subscription subscription = observable1.subscribe(mObserver1);//订阅并执行call
        subscription.unsubscribe(); //取消订阅

        test.testDefer();

        test.testScheduler(); //线程调度

        test.testMap(this);

        test.testConcatMap();

        test.testFlatMap();

        test.testSwitchMap();

        test.testFilter();

        test.testFirst();

        test.testLift();

        test.testCompose();

        test.testDoOnMethod();

        test.testScheduleMethod();

        test.testGroupBy();

        test.testConcatWith();

        test.testCast();

        test.testBlockingObservable();

        test.testRetry();

        test.testRetryWhen();

        test.testOnErrorReturn();
//
//        /*
//        rxbus
//         */
//        Observable<String> observable = RxBus.getInstance().register(String.class);
//        observable.map(s -> {
//            try {
//                int v = Integer.valueOf(s);
//                System.out.println("map变换成功, source = " + s);
//                return v;
//            } catch (Exception e) {
//                System.out.println("map变换失败, source = " + s);
//                return s;
//            }
//        }).subscribe(value -> {
//            System.out.println("订阅 " + value);
//        });
//
//        RxBus.getInstance().post("888");
//        RxBus.getInstance().post("发发发");
//        RxBus.getInstance().unregister(String.class, observable);
//
//        /*
//        Subject
//         */
//        MySubject testSubject = new MySubject();
//        testSubject.testPublishSubject();
//
//        testSubject.testBehaviorSubject();
//
//        testSubject.testReplaySubject();
//
//        testSubject.testAsyncSubject();
//
//        testSubject.testUnicastSubject();
//
//        testSubject.testSerializedSubject();
//
//        /*
//        Math
//         */
//        MyMath testMath = new MyMath();
//        testMath.testAverage();
//
//        testMath.testMaxAndMin();
//
//        testMath.testSum();
    }
}
