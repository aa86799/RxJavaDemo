package com.stone.rx3.common

import android.app.Activity
import android.content.Context
import android.content.Intent

fun <T: Activity> Context.startActivity(clz: Class<T>) {
    this.startActivity(Intent(this, clz))
}