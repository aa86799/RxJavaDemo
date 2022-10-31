package com.stone.rx3

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.stone.rx3.common.startActivity
import com.stone.rx3.flowable.FlowableActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Home"

        findViewById<Button>(R.id.btn_flowable).setOnClickListener {
            startActivity(FlowableActivity::class.java)
        }
    }
}