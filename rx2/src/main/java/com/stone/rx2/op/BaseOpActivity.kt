package com.stone.rx2.op

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.stone.rx2.R

/**
 * desc     :
 * author   : stone
 * homepage : http://stone86.top
 * email    : aa86799@163.com
 * time     : 2018/9/9 02 34
 */
abstract class BaseOpActivity : AppCompatActivity() {

    protected var mTv: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_op_concrete)
        mTv = findViewById(R.id.activity_op_creator_desc_tv)

        init()
    }

    abstract fun init()
}