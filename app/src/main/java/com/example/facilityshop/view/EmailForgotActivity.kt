package com.example.facilityshop.view

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
import com.example.sendmail.GMailSender
import kotlinx.android.synthetic.main.activity_email_forgot.*

class EmailForgotActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_forgot)

        progress = CallProgressDialog()

        button_agree2.setOnClickListener {
            progress.showProgress(this@EmailForgotActivity,
                R.style.AppCompatAlertDialogStyle, "Sendding email",
                "Please wait...")
            var email = editText_email2.text.toString()
            if (TextUtils.isEmpty(email)) {
                progress.dismissProgress()
                editText_email2.setError("Please fill this field !")
            } else {
                checkIsNormalEmail(email)
            }
        }

        setToolbar()
    }

    private fun checkIsNormalEmail(email: String) {
        //region handle
        var requestQueue = Volley.newRequestQueue(this@EmailForgotActivity)
        var stringRequest = object : StringRequest(
            Method.POST,
            Server.checkIsNormal,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response == "Normal email user") {
                        sendMailToUser(email)
                    } else {
                        Toast.makeText(this@EmailForgotActivity, "Sorry, your email is social email address. " +
                                "Please type another an email address !", Toast.LENGTH_LONG).show()
                        progress.dismissProgress()
                    }
                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {

                }

            }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("email", email)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
        //endregion
    }

    private fun sendMailToUser(email: String) {
        // Send email setup new password to user
        var textBody = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<title>Index 2</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<h2>Hi, User</h2>\n" +
                "\t<p>We have received your password change request from you. Click on the link below to set a new password.</p>\n" +
                "\t<a href=\"http://facilityshop.000webhostapp.com/facilityshop/user/formChangePassword.php?email="+email+"\">" +
                "Setup a new password</a>\n" +
                "</body>\n" +
                "</html>"

        var sender = GMailSender("goodsolved99@gmail.com", "conchocon2020")
        sender.sendMail("[Facility Shopping] Mail setup new password", textBody,"goodsolved99@gmail.com",
            email)
        if (sender != null) {
            // When success
            val handler = Handler()
            handler.postDelayed(Runnable {
                progress.dismissProgress()
                Toast.makeText(this@EmailForgotActivity, "Mail has been sended.", Toast.LENGTH_SHORT).show()
            }, 3000)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_email_forgot)
        toolBar_email_forgot.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_email_forgot.setNavigationOnClickListener {
            finish()
        }
    }
}
