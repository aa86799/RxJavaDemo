package com.stone.rx2.my.rxjava;

// rxjava:发射器  与发射器之间的引用    不是一个环 与另外一个环的直接引用
public class OnSubscribeLift<T, R> implements ObservableOnSubscribe<R> {
//public class OnSubscribeLift<String, Bitmap> implements ObservableOnSubscribe<Bitmap> {

    ObservableOnSubscribe<T> parent;
    Operator<? extends R, ? super T> operator;

    public OnSubscribeLift(ObservableOnSubscribe<T> onSubscribe, Function<? super T, ? extends R> function) {
        this.parent = onSubscribe;
        this.operator = new OperatorMap<T, R>(function);
    }

    @Override
    public void subscribe(Observer<? super R> observer) {
        Observer<? super T> st = operator.apply(observer);
        parent.subscribe(st);

    }
}
