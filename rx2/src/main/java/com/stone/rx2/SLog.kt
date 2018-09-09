package com.stone.rx2

import android.util.Log

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/9 01 32
 */
class SLog {

    companion object MyStatic {//可以指定一个名字; 默认名Companion；调用时 SLog.Companion.i; 可省略成 SLog.i
//    companion object { //通常这样就可以    companion object包含类似的静态方法；
        //如果本类中全是静态方法式的调用；  class 声明 改成 object 声明

        fun i(msg: String) {
            Log.e("log-stone->", msg)
        }
    }


}