package com.example.facilityshop.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_register.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        button_login.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        Paper.init(this@MainActivity)

        progress = CallProgressDialog()

        getUserInfo()
    }

    private fun getUserInfo() {
        var phone = Paper.book().read("sessionPhone", "")
        var password = Paper.book().read("sessionPassword", "")
        var idFacebook = Paper.book().read("sessionIdFacebook", "")
        var idGoogle = Paper.book().read("sessionIdGoogle", "")

        if (phone != "" && password != "") {
            progress.showProgress(this@MainActivity,
                R.style.AppCompatAlertDialogStyle, "Login account",
                "Please wait when we login your account...")
            val handler = Handler()
            handler.postDelayed(Runnable {
                progress.dismissProgress()
                android.util.Log.d("sessionPhone", phone)
                var intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.putExtra("type", "normal")
                intent.putExtra("phoneNumber", phone)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            }, 2000)
        } else if (idFacebook != "") {
            progress.showProgress(this@MainActivity,
                R.style.AppCompatAlertDialogStyle, "Login account",
                "Please wait when we login your account...")
            val handler = Handler()
            handler.postDelayed(Runnable {
                progress.dismissProgress()
                android.util.Log.d("sessionIdFB", idFacebook)

                var intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.putExtra("type", "facebook")
                intent.putExtra("idFb", idFacebook)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            }, 2000)
        } else if (idGoogle != "") {
            progress.showProgress(this@MainActivity,
                R.style.AppCompatAlertDialogStyle, "Login account",
                "Please wait when we login your account...")
            val handler = Handler()
            handler.postDelayed(Runnable {
                progress.dismissProgress()
                android.util.Log.d("sessionIdGG", idGoogle)

                var intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.putExtra("type", "google")
                intent.putExtra("idGoogle", idGoogle)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            }, 2000)
        }
    }
}
