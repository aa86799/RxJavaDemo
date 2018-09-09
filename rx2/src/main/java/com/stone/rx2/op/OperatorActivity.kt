package com.stone.rx2.op

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.stone.rx2.R
import com.stone.rx2.op.creator.CreatorActivity
import com.stone.rx2.op.transformation.TransformationActivity

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/8 20 21
 */
class OperatorActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_op)
    }

    fun creator(view: View) {
        startActivity(Intent(this, CreatorActivity::class.java))
    }

    fun trans(view: View) {
        startActivity(Intent(this, TransformationActivity::class.java))
    }
}