package com.stone.rx2.my.rxjava;

public class Observable<T> {

    private ObservableOnSubscribe onSubscribe;

    private Observable(ObservableOnSubscribe onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    //    创造操作符 create
    public static <T> Observable<T> create(ObservableOnSubscribe<T> onSubscribe) {
        return new Observable<T>(onSubscribe);
    }

//    Observer<? super T>    存   add     set   subscribe   变换操作符   区别 存
    public  void subscribe(Observer<? super T> subscribe) {
        onSubscribe.subscribe(subscribe);
    }

//    转换操作符
    public <R> Observable<R> map(Function<? super T,? extends  R> function) {
//        new Obserable<>  印纸厂  onSubscribe  1          没有 2   发  射器
        return new Observable<R>(new OnSubscribeLift<T, R>(onSubscribe, function));
    }
}
