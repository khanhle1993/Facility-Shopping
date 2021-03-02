package com.example.facilityshop.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.collections.HashMap
import android.os.Handler
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server


class RegisterActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progress = CallProgressDialog()

        button_register_regis.setOnClickListener {
            var fullName = editText_fullname_regis.text.toString()
            var phoneNumber = editText_phone_regis.text.toString()
            var email = editText_email_regis.text.toString()
            var password = editText_pass_regis.text.toString()
            var rePassword = editText_retypePass_regis.text.toString()

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(rePassword)) {
                Toast.makeText(this@RegisterActivity, "Please fill full information !", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (rePassword == password) {
                    progress.showProgress(this@RegisterActivity,
                        R.style.AppCompatAlertDialogStyle, "Register account",
                        "Please wait when we create your account...")
                    checkRegister(fullName, phoneNumber, email, password)
                } else {
                    Toast.makeText(this@RegisterActivity, "Retype password not correct, please fill again !",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkRegister(fullName: String, phoneNumber: String, email: String, password: String) {
        // Check email and phonenumber, if them is already exitst is notify "Fail"
        var requestQueue = Volley.newRequestQueue(this@RegisterActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.checkRegister, object : Response.Listener<String>{
            override fun onResponse(response: String) {
                if (response.equals("Phone number already exists !")) {
                    progress.dismissProgress()
                    Toast.makeText(this@RegisterActivity, "This phone number has been used by someone else. Please choose " +
                            "another phone !", Toast.LENGTH_LONG).show()
                } else if (response.equals("Email already exists !")) {
                    progress.dismissProgress()
                    Toast.makeText(this@RegisterActivity, "This email has been used by someone else. Please choose " +
                            "another email !", Toast.LENGTH_LONG).show()
                } else if (response.equals("Phone and email already exists !")) {
                    progress.dismissProgress()
                    Toast.makeText(this@RegisterActivity, "This email and phone has been used by someone else. Please choose " +
                            "another !", Toast.LENGTH_LONG).show()
                } else {
                    // When success
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        var intent = Intent(this@RegisterActivity, VerifyRegisterActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("fullName", fullName)
                        bundle.putString("phoneNumber", phoneNumber)
                        bundle.putString("email", email)
                        bundle.putString("password", password)
                        intent.putExtra("bundleRegisterInfo", bundle)
                        startActivity(intent)
                    }, 3000)
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("email", email)
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }
}
