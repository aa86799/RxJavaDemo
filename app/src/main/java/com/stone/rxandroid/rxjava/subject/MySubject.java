package com.stone.rxandroid.rxjava.subject;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Subscriber;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subjects.UnicastSubject;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 03/05/2017 10 32
 */
public class MySubject {

    public static void testPublishSubject() {
        PublishSubject<String> subject = PublishSubject.create();
        subject.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("subscribe --- " + s);
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                /*
                延迟发送，当发送后，才开始emit数据，并完成订阅
                 */
                subject.onNext("start subscribe");
            }
        }, 2000);
    }

    public static void testBehaviorSubject() {
        /*
        BehaviorSubject
        首先会向它的订阅者发送截止订阅前的最后一条数据流，然后才正常发送订阅后的数据流。
         */
        BehaviorSubject<String> subject = BehaviorSubject.create("default");
        subject.subscribe(s -> {
            System.out.println("s1 = " + s);
        });
        subject.onNext("one");
        subject.onNext("two");
        subject.onNext("three");//default、one、two、three

        subject = BehaviorSubject.create("default");
        subject.onNext("zero");
        subject.onNext("one");
        subject.subscribe(s -> System.out.println("s2 = " + s));
        subject.onNext("two");
        subject.onNext("three");//one、two、three

        subject = BehaviorSubject.create("default");
        subject.onNext("zero");
        subject.onNext("one");
        subject.onCompleted();
        subject.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("s3 onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("s3 = " + s);
            }
        });//only onCompleted


        subject = BehaviorSubject.create("default");
        subject.onNext("zero");
        subject.onNext("aa");
        subject.onError(new Exception("throw exception"));
        subject.doOnError(throwable -> {
            System.out.println("s4 检测发生了error");
        });
        subject.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("s4 " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.println("s4 = " + s);
            }
        });//only onError
    }

    public void testReplaySubject() {
        /*
        ReplaySubject 缓存订阅的数据，重发给订阅它的观察者
         */
        ReplaySubject<String> subject = ReplaySubject.create();
        subject.onNext("haha1");
        subject.onNext("haha2");
        subject.onNext("haha3");
        subject.subscribe(s -> System.out.println("rss1" + s));

    }

    public void testAsyncSubject() {
        /*
        AsyncSubject
         仅在Observable完成之后，发送最后一条数据给观察者。
         然而如果当Observable因为异常而终止，AsyncSubject将不会释放任何数据，
         但是会向Observer传递一个异常通知。


         */
        AsyncSubject<String> subject = AsyncSubject.create();
        subject.onNext("haha1");
        subject.onNext("haha2");
        subject.onNext("haha3");
        subject.onCompleted();
        subject.subscribe(s -> System.out.println("ass1" + s));

    }

    public void testUnicastSubject() {
        /*
        UnicastSubject (内部subscribeActual()，用到了AtomicBoolean 判断，第二次无就抛出异常)
        只允许有一个 Subscriber 订阅
         */
        UnicastSubject subject = UnicastSubject.create();
        subject.onNext("haha1");
        subject.onNext("haha2");
        subject.onNext("haha3");
        subject.onCompleted();
        subject.subscribe(s -> System.out.println("uss1" + s));
//
//        subject.subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                System.out.println("uss2" + s);
//            }
//        });

//        AtomicBoolean once = new AtomicBoolean();
//        System.out.println("stone" + once.get());
//        System.out.println("stone" + once.compareAndSet(false, true));
//        System.out.println("stone" + once.compareAndSet(true, false));

    }

    public void testSerializedSubject() {
        /*
        SerializedSubject
        当我们使用普通的Subject，必须要注意不要在多线程情况下调用onNext方法，
        而使用SerializedSubject封装原来的 Subject即可！！
        内部使用了SerializedObserver
        如果是多线程环境，即有多个线程发射通知时，它们将被按序列执行：
        1.允许仅有一个线程，执行一个emit
        2.如果另一线程，已经emit，将下一个添加到通知队列
        3.在循环emitting时，不持有任何锁或阻塞任何线程
         */
        ReplaySubject<String> subject = ReplaySubject.create();
        SerializedSubject<String, String> serializedSubject = new SerializedSubject<>(subject);
        serializedSubject.subscribe(s ->
                System.out.println("thread-" + Thread.currentThread().getName() + "--" + s));
        for (int i = 0; i < 10; i++) {
            int finalIndex = i;
            new Thread(() -> serializedSubject.onNext("haha" + finalIndex)).start();
        }
        serializedSubject.onNext("haha20");
        serializedSubject.onNext("haha21");
        serializedSubject.onNext("haha22");
    }
}
