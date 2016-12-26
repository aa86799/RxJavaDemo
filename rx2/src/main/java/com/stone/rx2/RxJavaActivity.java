package com.stone.rx2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rx2.rxjava.MyObservable;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 */

public class RxJavaActivity extends Activity {

    private String tag = this.getClass().getSimpleName();

    MyObservable test;

    // create a flowable
    Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
        @Override
        public void subscribe(FlowableEmitter<String> e) throws Exception {
            e.onNext("hello RxJava 2");
            e.onComplete();
        }
    }, BackpressureStrategy.BUFFER);

    Subscriber<String> mSubscriber1 = new Subscriber<String>() {
        @Override
        public void onSubscribe(Subscription s) {
            Log.d(tag, "onSubscribe!");
        }

        @Override
        public void onNext(String s) {
            Log.d(tag, "Item: " + s);
        }

        @Override
        public void onError(Throwable e) {
            Log.d(tag, "Error!");
        }

        @Override
        public void onComplete() {
            Log.d(tag, "onComplete!");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test = new MyObservable();

        test.testNormal();
        test.testJust();
        test.testFrom();


        setContentView(R.layout.acti_main);
        ImageView iv = (ImageView) findViewById(R.id.iv);
        test.setImage(iv, R.mipmap.stone);

        test.testScheduler();

        test.testMap(this);

        test.testFromIterable();

        test.testFlatMap();

        test.testSwitchMap();

        test.testFilterAndTake();

        test.testContact();

        test.testFirst();

        test.testLift();

        test.testCompose();

        test.testScheduleMethod();

        test.testPublishSubject();

        test.testSingle();
    }
}
