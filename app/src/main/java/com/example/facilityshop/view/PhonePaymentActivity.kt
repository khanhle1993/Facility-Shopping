package com.example.facilityshop.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.CallProgressDialog
import com.example.facilityshop.model.Server
import com.example.sendmail.GMailSender
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_phone_payment.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PhonePaymentActivity : AppCompatActivity() {

    lateinit var progress: CallProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_payment)

        progress = CallProgressDialog()
        Paper.init(this@PhonePaymentActivity)
        var idUser = Paper.book().read("idUser", "") // get from HomeActivity
        var total = Paper.book().read("total", "") // Get from CartActivity

        textView_total_payment.text = total + " $"

        button_confirm_payment.setOnClickListener {
            var fullName = editText_fullName_payment.text.toString()
            var phoneNumber = editText_phone_payment.text.toString()
            var address = editText_address_payment.text.toString()

            if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(phoneNumber) &&
                !TextUtils.isEmpty(address)) {
                progress.showProgress(this@PhonePaymentActivity,
                    R.style.AppCompatAlertDialogStyle, "Processing your information",
                    "Please wait...")
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progress.dismissProgress()
                    saveData(idUser, total, fullName, phoneNumber, address)
                }, 2000)
            } else {
                Toast.makeText(this@PhonePaymentActivity, "Please fill full information !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData(idUser: String, total: String, fullName: String, phoneNumber: String, address: String) {
        // Save data in orderform table
        var currentDate: String
        var currentTime: String

        var calForDate: Calendar = Calendar.getInstance()

        var date = SimpleDateFormat("dd/MM/yyyy")
        currentDate = date.format(calForDate.time)

        var time = SimpleDateFormat("HH:mm:ss")
        currentTime = time.format(calForDate.time)

        var requestQueue = Volley.newRequestQueue(this@PhonePaymentActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.saveOrder, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response.equals("Success")) {
                    // If success then save them in detailorder table (table show detail product has ordered)
                    var requestQueue = Volley.newRequestQueue(this@PhonePaymentActivity)
                    var stringRequest = object : StringRequest(Method.POST, Server.saveDetailOrder, object : Response.Listener<String>{

                        override fun onResponse(response: String?) {
                            if (response.equals("Success")) {
                                var intent = Intent(this@PhonePaymentActivity, CompletePaymentActivity::class.java)
                                intent.putExtra("type", "phone")
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                Toast.makeText(this@PhonePaymentActivity, "Order success.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@PhonePaymentActivity, "Order not success !", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }, object: Response.ErrorListener{
                        override fun onErrorResponse(error: VolleyError?) {
                        }

                    }){
                        override fun getParams(): MutableMap<String, String> {
                            var hashMap: HashMap<String, String> = HashMap()
                            hashMap.put("idUser", idUser)
                            return hashMap
                        }
                    }

                    requestQueue.add(stringRequest)

                } else {
                    Toast.makeText(this@PhonePaymentActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error pay with phone", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("idUser", idUser)
                hashMap.put("dateOrder", currentDate)
                hashMap.put("timeOrder", currentTime)
                hashMap.put("totalAmount", total)
                hashMap.put("fullName", fullName)
                hashMap.put("phoneNumber", phoneNumber)
                hashMap.put("address", address)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }
}
