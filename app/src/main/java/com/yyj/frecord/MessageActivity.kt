package com.yyj.frecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.layout_bottom.*

class MessageActivity : Fragment() {
    lateinit var ctx : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        icMenu1.setImageResource(R.drawable.ic_selected_view)
        icMenu2.setImageResource(R.drawable.ic_not_selected_view)

        textView3.setOnClickListener {
            val intent = Intent(ctx, AddMessageActivity::class.java)
            startActivity(intent)
        }
    }
}