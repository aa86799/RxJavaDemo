package com.stone.rx2.my.rxjava;

public interface Operator<T, R> extends  Function<Observer<? super T>, Observer<? super R>> {
//public interface Operator<Bitmap, String> extends  Function<Observer<? super Bitmap>, Observer<? super String>> {
}
