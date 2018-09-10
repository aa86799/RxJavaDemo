package com.stone.rx2.my.rxjava;

public abstract class Observer <T>{

    public abstract void onNext(T t);
    public abstract void onError(Throwable e);
    public abstract void onComplete();
}
