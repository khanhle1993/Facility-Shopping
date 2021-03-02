package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.DetailOrder
import com.example.facilityshop.model.DetailOrderAdapter
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_detail_order.*
import org.json.JSONArray

/*Details order when click button viewDetail in HistoryActivity*/

class DetailOrderActivity : AppCompatActivity() {

    lateinit var arrayDetailOrder: ArrayList<DetailOrder>
    lateinit var adapter: DetailOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)

        setToolbar()
        setRecycler()

        var bundle = intent.getBundleExtra("bundleDetail")
        var idOrder = bundle.getString("idOrder")
        var buyer = bundle.getString("buyer")
        var dateOrder = bundle.getString("dateOrder")
        var timeOrder = bundle.getString("timeOrder")
        var numOrder = bundle.getString("numOrder")

        textView_buyer.text = buyer
        textView_date_time.text = dateOrder + " " + timeOrder
        textView_num_order_detail_order.text = "#" + numOrder

        getDetailOrder(idOrder!!)

        button_back_detailOrder.setOnClickListener {
            finish()
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_detail_order)
        toolBar_detail_order.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_detail_order.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setRecycler() {
        recycler_detail_order.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(this@DetailOrderActivity, LinearLayoutManager.VERTICAL, false)
        recycler_detail_order.layoutManager = layoutManager
        arrayDetailOrder = ArrayList()
        adapter = DetailOrderAdapter(this@DetailOrderActivity, arrayDetailOrder)
        recycler_detail_order.adapter = adapter
    }

    private fun getDetailOrder(idOrder: String) {
        var requestQueue = Volley.newRequestQueue(this@DetailOrderActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.getDetailOrder, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var idProduct: Int
                    var pname: String
                    var price: String
                    var quantity: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        idProduct = jsonObject.getInt("idProduct")
                        pname = jsonObject.getString("pname")
                        price = jsonObject.getInt("price").toString()
                        quantity = jsonObject.getInt("quantity").toString()

                        arrayDetailOrder.add(DetailOrder(idProduct, pname, quantity, price))
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                android.util.Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("idOrder", idOrder)
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }
}
