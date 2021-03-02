package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_security_question.*
import org.json.JSONArray

class SecurityQuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_question)

        var phone = intent.getStringExtra("phoneNumber")
        getSecurityQuestion(phone)
        button_save_securityQuestion.setOnClickListener {
            var securityQuestion1 = editText_securityQuestion_1.text.toString()
            var securityQuestion2 = editText_securityQuestion_2.text.toString()

            if (TextUtils.isEmpty(securityQuestion1) || TextUtils.isEmpty(securityQuestion2)) {
                Toast.makeText(this@SecurityQuestionActivity, "Please fill full information !", Toast.LENGTH_SHORT).show()
            } else {
                if (securityQuestion1 == securityQuestion2) {
                    Toast.makeText(this@SecurityQuestionActivity, "The second answer must be different from the first !",
                        Toast.LENGTH_SHORT).show()
                } else {
                    updateSecurityQuestion(phone, securityQuestion1, securityQuestion2)
                }
            }
        }
        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_security_question)
        toolBar_security_question.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_security_question.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateSecurityQuestion(phoneNumber: String, securityQuestion1: String, securityQuestion2: String) {
        var requestQueue = Volley.newRequestQueue(this@SecurityQuestionActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.updateSecurityQuestion, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                   Toast.makeText(this@SecurityQuestionActivity, "Success !", Toast.LENGTH_SHORT).show()
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
                hashMap.put("securityQuestion1", securityQuestion1)
                hashMap.put("securityQuestion2", securityQuestion2)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun getSecurityQuestion(phoneNumber: String) {
        var requestQueue = Volley.newRequestQueue(this@SecurityQuestionActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.getUserInfo, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)
                    var securityQuestion1: String
                    var securityQuestion2: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        securityQuestion1 = jsonObject.getString("securityQuestion1")
                        securityQuestion2 = jsonObject.getString("securityQuestion2")

                        if (securityQuestion1.equals("null") && securityQuestion2.equals("null")) {
                            editText_securityQuestion_1.text = Editable.Factory.getInstance().newEditable("")
                            editText_securityQuestion_2.text = Editable.Factory.getInstance().newEditable("")
                        } else {
                            editText_securityQuestion_1.text = Editable.Factory.getInstance().newEditable(securityQuestion1)
                            editText_securityQuestion_2.text = Editable.Factory.getInstance().newEditable(securityQuestion2)
                        }

                    }
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("condition", phoneNumber)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }
}
