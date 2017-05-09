package com.stone.rxandroid.rxmath;


import rx.Observable;
import rx.observables.MathObservable;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 05/05/2017 12 12
 */
public class MyMath {
    /*
    MathObservable方法，都有静态与非静态方法。
    静态方法要求传入一个Observable<T>；非静态方法可通过from(Observable<T> observable)返回一个MathObservable，来进行操作。

     */

    public void testAverage() {
        /*
        求平均值,
        averageDouble averageFloat averageInteger averageLong
         */
        MathObservable.averageDouble(Observable.just(1.2, 1.3)).subscribe(aDouble -> {
            System.out.println("math. Average value " + aDouble);
        });
    }

    public void testMaxAndMin() {
        MathObservable.max(Observable.just(1.2, 1.3)).subscribe(aDouble ->
                System.out.println("math. max value is " + aDouble));
        MathObservable.max(Observable.just(1.2, 1.3)).subscribe(aDouble ->
                System.out.println("math. max value is " + aDouble));
    }

    public void testSum() {
        /*
         求和
         sumDouble sumFloat sumInteger sumLong
         */
        MathObservable
                .from(Observable.just(99, 1, 2))
                .max((o1, o2) -> o1.compareTo(o2))
                .subscribe(integer ->
                System.out.println("math. sum value is " + integer));

    }
}
