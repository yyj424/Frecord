package com.yyj.frecord

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_message.*

class ViewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_message)

        if (intent.getSerializableExtra("message") != null) {
            val message = intent.getSerializableExtra("message") as MessageData
            etViewMsg.setText(message.content)
        }
    }
}