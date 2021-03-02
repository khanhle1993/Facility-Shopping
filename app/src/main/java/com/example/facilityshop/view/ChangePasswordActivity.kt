package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_change_password.*

/*Change password when edit user profile*/

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        setToolbar()

        var phoneNumber = intent.getStringExtra("phoneNumber") // Get from ProfileActivity
        var password = intent.getStringExtra("password") // Get from ProfileActivity

        Paper.init(this@ChangePasswordActivity)

        button_save_password.setOnClickListener {
            if (TextUtils.isEmpty(editText_oldPassWord.text.toString()) || TextUtils.isEmpty(editText_newPassWord.text.toString())
                || TextUtils.isEmpty(editText_retype_newPassWord.text.toString())) {
                Toast.makeText(this@ChangePasswordActivity, "Please fill full information !", Toast.LENGTH_SHORT).show()
            } else {
                var oldPassWord = editText_oldPassWord.text.toString()
                var newPassWord = editText_newPassWord.text.toString()
                var retypeNewPassWord = editText_retype_newPassWord.text.toString()

                if (newPassWord.equals(retypeNewPassWord)) {
                    if (oldPassWord.equals(password)) {
                        updatePassword(Server.updateOnlyPassword, phoneNumber, newPassWord)
                        Paper.book().write("newPassWord", newPassWord)
                    }
                    else
                        Toast.makeText(this@ChangePasswordActivity, "Old password is not correct !", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "Retype password is not correct !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePassword(url: String, phoneNumber: String, newPassWord: String) {
        var requestQueue = Volley.newRequestQueue(this@ChangePasswordActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success update password")) {
                    Toast.makeText(this@ChangePasswordActivity, "Success update your password.", Toast.LENGTH_SHORT).show()
                    finish()
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
                hashMap.put("newPassWord", newPassWord)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_change_password)
        toolBar_change_password.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_change_password.setNavigationOnClickListener {
            finish()
        }
    }
}
