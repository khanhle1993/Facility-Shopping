package com.example.facilityshop.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_reset_password.*

/*This activity use for reset password from verify button in PhoneForgotActivity*/

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        progress = CallProgressDialog()

        editText_newPass_resetPass.requestFocus()

        setToolbar()

        var intent = intent
        var phone = intent.getStringExtra("phone")

        button_update_resetPass.setOnClickListener {
            progress.showProgress(this@ResetPasswordActivity,
                R.style.AppCompatAlertDialogStyle, "Updating your password",
                "Please wait...")

            var newPassword = editText_newPass_resetPass.text.toString()
            var retypeNewpassword = editText_retype_pass_resetPass.text.toString()

            if (!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(retypeNewpassword)) {
                if (newPassword == retypeNewpassword) {
                    resetPassword(phone, newPassword)
                } else {
                    progress.dismissProgress()
                    Toast.makeText(this@ResetPasswordActivity, "Retype new password not correct !", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                progress.dismissProgress()
                Toast.makeText(this@ResetPasswordActivity, "Please fill full information !", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun resetPassword(phoneNumber: String, newPassword: String) {
        var requestQueue = Volley.newRequestQueue(this@ResetPasswordActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.resetPassword, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        progress.dismissProgress()
                        var alertDialog = AlertDialog.Builder(this@ResetPasswordActivity,
                            R.style.AlertDialogTheme
                        )
                        alertDialog.setTitle("Notification")
                        alertDialog.setMessage("Update your password success. You can login now !")
                        alertDialog.setPositiveButton("OK") {dialog, which ->
                            alertDialog.setCancelable(true)
                            var intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        alertDialog.show()
                    }, 1500)
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Update error !", Toast.LENGTH_SHORT).show()
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
                hashMap.put("newPassword", newPassword)
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_reset_pass)
        toolBar_reset_pass.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_reset_pass.setNavigationOnClickListener {
            finish()
        }
    }
}
