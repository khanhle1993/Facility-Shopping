package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facilityshop.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_about)
        toolBar_about.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_about.setNavigationOnClickListener {
            finish()
        }
    }
}
