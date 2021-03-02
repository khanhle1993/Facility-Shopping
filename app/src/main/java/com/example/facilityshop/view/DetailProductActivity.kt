package com.example.facilityshop.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Server
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_detail_product.*
import org.json.JSONArray
import org.json.JSONException
import java.lang.Double
import java.text.DecimalFormat


class DetailProductActivity : AppCompatActivity() {

    lateinit var imgSaved1: String
    lateinit var imgSaved2: String
    lateinit var imgSaved3: String
    var count = 0
    var idUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        setToolbar()

        Paper.init(this@DetailProductActivity)
        idUser = Paper.book().read("idUser", "") // Get from HomeAcivity

        var intent = intent
        var idProduct = intent.getIntExtra("id", 0).toString() // idProduct get from ProductAdapter
        var id2 = intent.getIntExtra("id2", 0).toString() // Get from CartAdapter
        var isCart = intent.getBooleanExtra("isCart", false)
        var isDetailOrder = intent.getBooleanExtra("isDetailOrder", false)
        var id3 = intent.getIntExtra("id3", 0).toString() // Get from DetailOrderAdapter
        if (isCart == true) {
            getDataProduct(Server.detailProduct, id2)
            button_add_to_cart.visibility = View.GONE
        } else if (isDetailOrder == true){
            getDataProduct(Server.detailProduct, id3)
            button_add_to_cart.visibility = View.GONE
        } else {
            getDataProduct(Server.detailProduct, idProduct)
            button_add_to_cart.visibility = View.VISIBLE
        }

        setImage()

        button_add_to_cart.setOnClickListener {
            saveProduct(idProduct, idUser)
        }
    }

    private fun saveProduct(idProduct: String, idUser: String) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@DetailProductActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.saveCart, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                if (response == "Success") {
                    showSnackBar()
                } else {
                    showSnackBar()
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
            }

        }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("idProduct", idProduct)
                hashMap.put("idUser", idUser)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun showSnackBar() {
        var snackBar = Snackbar.make(constrain_detail, "Added product to cart", Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    startActivity(Intent(this@DetailProductActivity, CartActivity::class.java))
                }
            })
        var view = snackBar.view
        var textView = view.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)

        snackBar.show()
    }

    private fun getDataProduct(url: String, condition: String) {
        var requestQueue = Volley.newRequestQueue(this@DetailProductActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var pname: String
                    var price: Int
                    var description: String
                    var image1: String
                    var image2: String
                    var image3: String

                    for (i in 0 until jsonArray.length()) {
                        try {
                            var jsonObject = jsonArray.getJSONObject(i)
                            pname = jsonObject.getString("pname")
                            price = jsonObject.getInt("price")
                            description = jsonObject.getString("description")
                            image1 = jsonObject.getString("image1")
                            image2 = jsonObject.getString("image2")
                            image3 = jsonObject.getString("image3")
                            imgSaved1 = image1
                            imgSaved2 = image2
                            imgSaved3 = image3

                            // Set data for views detail product
                            textView_pname.text = pname

                            var decimalFormat = DecimalFormat("###,###,###")
                            var priceString = price.toString()
                            textView_price.text = decimalFormat.format(Double.valueOf(priceString)) + " $"

                            textView_description.text = description
                            Picasso.get().load(image1).into(imageView_detail_product)

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
            }

        }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("condition", condition)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun setImage() {
        textView_next.setOnClickListener {
            var anim = AnimationUtils.loadAnimation(this@DetailProductActivity, R.anim.slide)
            count++
            if (count == 1) {
                textView_next.isFocusable = true
                Picasso.get().load(imgSaved2).into(imageView_detail_product)
                imageView_detail_product.startAnimation(anim)
            } else if (count == 2) {
                textView_next.isFocusable = true
                Picasso.get().load(imgSaved3).into(imageView_detail_product)
                imageView_detail_product.startAnimation(anim)
            } else if (count > 2) {
                textView_next.isFocusable = false
                count = 2
            }
        }
        textView_prev.setOnClickListener {
            var anim = AnimationUtils.loadAnimation(this@DetailProductActivity, R.anim.slide2)
            count--
            if (count == 0) {
                Picasso.get().load(imgSaved1).into(imageView_detail_product)
                imageView_detail_product.startAnimation(anim)
            } else if (count == 1) {
                Picasso.get().load(imgSaved2).into(imageView_detail_product)
                imageView_detail_product.startAnimation(anim)
                textView_prev.isFocusable = true
            } else if (count < 0) {
                count = 0
                textView_prev.isFocusable = false
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_detail_product)
        toolBar_detail_product.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_detail_product.setNavigationOnClickListener {
            finish()
        }
    }
}
