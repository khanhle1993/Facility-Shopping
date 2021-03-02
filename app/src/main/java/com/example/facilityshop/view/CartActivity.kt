package com.example.facilityshop.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONException
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.facilityshop.R
import com.example.facilityshop.model.Cart
import com.example.facilityshop.model.CartAdapter
import com.example.facilityshop.model.Server
import io.paperdb.Paper


class CartActivity : AppCompatActivity() {

    lateinit var carts: ArrayList<Cart>

    companion object {
        lateinit var adapter: CartAdapter
    }

    var idUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        Paper.init(this@CartActivity)
        idUser = Paper.book().read("idUser", "") // get from HomeAcivity

        setRecyclerProduct()
        setToolbar()

        textView_remove_all.visibility = View.GONE
        button_payment.isEnabled = false
        button_payment.setBackgroundColor(Color.parseColor("#FA9D96"))

        getProduct(Server.getCart, carts, adapter, idUser)

        textView_remove_all.setOnClickListener {
            removeAll(idUser)
        }

        Paper.init(this@CartActivity)

        button_payment.setOnClickListener {
            var total = textView_total_price.text.toString()
            Paper.book().write("total", total) // use for PaymentActivity and EmailPaymentActivity
            startActivity(Intent(this@CartActivity, PaymentActivity::class.java))
        }
    }

    private fun removeAll(idUser: String) {
        var alertDialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        alertDialog.setTitle("Notification")
        alertDialog.setMessage("Do you want to remove all ?")
        alertDialog.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            var requestQueue = Volley.newRequestQueue(this@CartActivity)
            var stringRequest = object : StringRequest(
                Method.POST,
                Server.removeAllCart,
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        if (response == "Success") {
                            carts.removeAll(carts)
                            adapter.notifyDataSetChanged()

                            textView_remove_all.visibility = View.GONE
                            button_payment.isEnabled = false
                            button_payment.setBackgroundColor(Color.parseColor("#FA9D96"))

                            textView_total_price.text = "0"

                            Toast.makeText(this@CartActivity, "Remove all success.", Toast.LENGTH_SHORT).show()
                        }
                    }

                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("Error", error.toString())
                    }

                }) {
                override fun getParams(): MutableMap<String, String> {
                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("idUser", idUser)
                    return hashMap
                }
            }
            requestQueue.add(stringRequest)
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getProduct(url: String, list: ArrayList<Cart>, adapter: CartAdapter, idUser: String) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@CartActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response != null) {
                        var jsonArray = JSONArray(response)
                        // id is id of product in cart
                        var id: Int
                        var idProduct: Int
                        var pname: String
                        var price: Int
                        var quantity: Int
                        var image: String

                        for (i in 0 until jsonArray.length()) {
                            try {
                                var jsonObject = jsonArray.getJSONObject(i)
                                id = jsonObject.getInt("id")
                                idProduct = jsonObject.getInt("idProduct")
                                pname = jsonObject.getString("pname")
                                price = jsonObject.getInt("price")
                                quantity = jsonObject.getInt("quantity")
                                image = jsonObject.getString("image")

                                list.add(Cart(id, idProduct, pname, price, quantity, image))
                                adapter.notifyDataSetChanged()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        if (carts.size > 0) {
                            textView_remove_all.visibility = View.VISIBLE
                            button_payment.isEnabled = true
                            button_payment.setBackgroundColor(Color.parseColor("#F44336"))
                        } else {
                            button_payment.isEnabled = false
                            button_payment.setBackgroundColor(Color.parseColor("#FA9D96"))
                        }

                        var total = 0
                        for (i in 0 until list.size) {
                            total += list.get(i).price * list.get(i).quantity
                        }
                        //var decimalFormat = DecimalFormat("###,###,###")
                        textView_total_price.text = total.toString()

                    }
                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Log.d("Error", error.toString())
                }

            }) {
                override fun getParams(): MutableMap<String, String> {
                    var hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("idUser", idUser)
                    return hashMap
                }
            }

        requestQueue.add(stringRequest)
    }

    private fun setRecyclerProduct() {
        recycler_cart.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
        recycler_cart.layoutManager = layoutManager
        carts = ArrayList()
        adapter = CartAdapter(this@CartActivity, carts, textView_total_price, textView_remove_all, button_payment)
        recycler_cart.adapter = adapter
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_cart)
        toolBar_cart.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_cart.setNavigationOnClickListener {
            finish()
        }
    }

}
