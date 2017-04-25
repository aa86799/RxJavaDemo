package com.stone.rx2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.stone.rx2.rxbus.RxBus;
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

        /*
        rxbus
         */
        Flowable<String> flowable = RxBus.getInstance().register(String.class);
        flowable.map(s -> {//使用lambda，正常返回Integer，异常返回String
            try {
                int v = Integer.valueOf(s);
                System.out.println("map变换成功, source = " + s);
                return v;
            } catch (Exception e) {
                System.out.println("map变换失败, source = " + s);
                return s;
            }
        }).doOnComplete(() -> System.out.println("flowable doOnComplete")).subscribe(
                /*new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("aaaaa");
            }

            @Override
            public void onNext(Integer o) {
                System.out.println("onNext " + o);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");
            }
         }*/ //在这里通过subscriber 订阅无效...

//                new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        System.out.println("中华人民共和国" + integer);
//                    }
//                } //Subscriber和Subscriber ，这样写明对象，它的泛型参数，必须是之前的链式变换时，最终返回的类型

                value -> {//直接使用lambda，对应上面可能返回的两种类型
                    System.out.println("订阅 " + value);
                })/*.dispose()*/;

        Flowable<Integer> f1 = RxBus.getInstance().register(Integer.class);
        f1.doOnComplete(() -> System.out.println("f1 doOnComplete"))
                .subscribe(value -> System.out.println("订阅f1消息 .. " + value));

        new Handler().postDelayed(() -> {
            System.out.println("延迟发两种类型消息");
            RxBus.getInstance().post("888");
            RxBus.getInstance().post("发发发");

            RxBus.getInstance().post(999);

        }, 2000);

        //极限测试，产生1万个 Flowable对象，内存 增加10m 以内，  在小米2手机上
//        startActivity(new Intent(this, SecondActi.class));

    }
}
