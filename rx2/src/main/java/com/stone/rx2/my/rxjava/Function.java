package com.stone.rx2.my.rxjava;

public interface Function<T, R> {
//    找到下一个观察者  为了去通知下一个观察者的   调用方法
    R apply(T t);
}