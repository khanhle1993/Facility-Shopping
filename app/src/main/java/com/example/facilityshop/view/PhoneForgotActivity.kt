package com.example.facilityshop.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_phone_forgot.*

class PhoneForgotActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_forgot)

        progress = CallProgressDialog()

        editText_phone_forgotPass.requestFocus()

        button_verify_forgotPass.setOnClickListener {
            progress.showProgress(this@PhoneForgotActivity,
                R.style.AppCompatAlertDialogStyle, "Verify your profile",
                "Please wait...")

            var phone = editText_phone_forgotPass.text.toString()
            var answer1 = editText_answer_1.text.toString()
            var answer2 = editText_answer_2.text.toString()

            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(answer1) || TextUtils.isEmpty(answer2)) {
                progress.dismissProgress()
                Toast.makeText(this@PhoneForgotActivity, "Please fill full information !", Toast.LENGTH_SHORT).show()
            } else {
                getSecurityQuestion(phone, answer1, answer2)
            }
        }

        setToolbar()
    }

    private fun getSecurityQuestion(phoneNumber: String, answer1: String, answer2: String) {
        var requestQueue = Volley.newRequestQueue(this@PhoneForgotActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.getSecurityQuestion, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        var intent = Intent(this@PhoneForgotActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("phone", phoneNumber)
                        startActivity(intent)
                    }, 1500)

                } else {
                    progress.dismissProgress()
                    Toast.makeText(this@PhoneForgotActivity, "Phone number or answer not correct !",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                android.util.Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("answer1", answer1)
                hashMap.put("answer2", answer2)
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_phone_forgot_pass)
        toolBar_phone_forgot_pass.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_phone_forgot_pass.setNavigationOnClickListener {
            finish()
        }
    }
}
