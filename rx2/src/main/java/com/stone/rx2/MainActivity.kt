package com.stone.rx2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.stone.rx2.my.MyRxJavaActivity
import com.stone.rx2.op.OperatorActivity
import com.stone.rx2.role.RoleActivity

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/8 20 01
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun op(view: View) {
        startActivity(Intent(this, OperatorActivity::class.java))
    }

    fun role(view: View) {
        startActivity(Intent(this, RoleActivity::class.java))
    }

    fun my(view: View) {
        startActivity(Intent(this, MyRxJavaActivity::class.java))
    }
}