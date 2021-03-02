package com.example.facilityshop.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_verify_register.*
import java.util.concurrent.TimeUnit

/*Verify register when user register via phoneNumber, send verify code via Firebase message*/

class VerifyRegisterActivity : AppCompatActivity() {

    var verificationId = ""
    lateinit var mAuth: FirebaseAuth
    lateinit var progress: CallProgressDialog
    lateinit var fullName: String
    lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_register)

        progress = CallProgressDialog()

        var bundle = intent.getBundleExtra("bundleRegisterInfo")
        fullName = bundle.getString("fullName")!!
        phoneNumber = bundle.getString("phoneNumber")!!
        email = bundle.getString("email")!!
        password = bundle.getString("password")!!

        mAuth = FirebaseAuth.getInstance()
        sendVerificationCode(phoneNumber!!)

        button_confirm_register.setOnClickListener {
            progress.showProgress(this@VerifyRegisterActivity,
                R.style.AppCompatAlertDialogStyle, "Verify account",
                "Please wait when we verify your account...")

            var code = editText_verify_code.text.toString()
            if (code.isEmpty() || code.length < 6) {
                editText_verify_code.setError("Code require 6 digit !")
                editText_verify_code.requestFocus()
                progress.dismissProgress()
                return@setOnClickListener
            }
            verifyCode(code)
        }
    }

    private fun verifyCode(code: String) {
        var credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(object :
            OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    addAccountToDB(fullName, phoneNumber, email, password)
                    progress.dismissProgress()
                    var alertDialog = AlertDialog.Builder(this@VerifyRegisterActivity, R.style.AlertDialogTheme)
                    alertDialog.setMessage("Congratulations, your account has been successfully created. You can login now !")
                    alertDialog.setPositiveButton("OK") { dialog, which ->
                        alertDialog.setCancelable(true)
                        var intent =
                            Intent(this@VerifyRegisterActivity, LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    alertDialog.show()
                } else {
                    progress.dismissProgress()
                    Toast.makeText(this@VerifyRegisterActivity, "Code is invalid !", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,
            mCallBack)
    }

    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(p0, p1)
            verificationId = p0!!
        }

        override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
            var code = p0!!.smsCode
            if (code != null) {
                return
            }
        }

        override fun onVerificationFailed(p0: FirebaseException?) {
            Toast.makeText(this@VerifyRegisterActivity, p0!!.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAccountToDB(fullName: String, phoneNumber: String, email: String, password: String) {
        //region handle
        var requestQueue = Volley.newRequestQueue(this@VerifyRegisterActivity)
        var stringRequest = object : StringRequest(
            Method.POST,
            Server.userInfo,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {

                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error", error.toString())
                }

            }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("fullName", fullName)
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("email", email)
                hashMap.put("password", password)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
        //endregion
    }
}
