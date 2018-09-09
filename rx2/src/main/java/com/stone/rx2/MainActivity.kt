package com.stone.rx2

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
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
}