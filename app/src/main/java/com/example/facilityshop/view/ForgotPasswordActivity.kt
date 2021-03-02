package com.example.facilityshop.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facilityshop.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        textView_continue2.setOnClickListener {
            if (radioButton_method_1.isChecked) {
                startActivity(Intent(this@ForgotPasswordActivity, PhoneForgotActivity::class.java))
            }

            if (radioButton_method_2.isChecked) {
                startActivity(Intent(this@ForgotPasswordActivity, EmailForgotActivity::class.java))
            }
        }
    }


}
