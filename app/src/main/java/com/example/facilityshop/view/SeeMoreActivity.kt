package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Product
import com.example.facilityshop.model.ProductAdapter
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_see_more.*
import org.json.JSONArray
import org.json.JSONException

class SeeMoreActivity : AppCompatActivity() {

    lateinit var products: ArrayList<Product>
    companion object {
        lateinit var adapter: ProductAdapter
    }
    var text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more)

        setToolbar()
        products = ArrayList()
        setRecyclerProduct(products)

        var intent = intent
        text = intent.getStringExtra("Seemore") // Get from HomeActivity
        if (text == "Selling") {
            textView_seemore.text = "Selling"
            textView_title_seemore.text = "List products selling"
            getDataProduct(Server.getSeemoreProduct, "Selling", products, adapter)
        } else if (text == "Latest") {
            textView_seemore.text = "Latest"
            textView_title_seemore.text = "List products latest"
            getDataProduct(Server.getSeemoreProduct, "Latest", products, adapter)
        } else if (text == "Promotion") {
            textView_seemore.text = "Promotion"
            textView_title_seemore.text = "List products promotion"
            getDataProduct(Server.getSeemoreProduct, "Promotion", products, adapter)
        }

        refreshData()
    }

    private fun refreshData() {
        swipeRefresh_layout_3.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefresh_layout_3.isRefreshing = true
                if (text == "Selling") {
                    textView_seemore.text = "Selling"
                    textView_title_seemore.text = "List products selling"
                    products = ArrayList()
                    setRecyclerProduct(products)
                    getDataProduct(Server.getSeemoreProduct, "Selling", products, adapter)
                } else if (text == "Latest") {
                    textView_seemore.text = "Latest"
                    textView_title_seemore.text = "List products latest"
                    products = ArrayList()
                    setRecyclerProduct(products)
                    getDataProduct(Server.getSeemoreProduct, "Latest", products, adapter)
                } else if (text == "Promotion") {
                    textView_seemore.text = "Promotion"
                    textView_title_seemore.text = "List products promotion"
                    products = ArrayList()
                    setRecyclerProduct(products)
                    getDataProduct(Server.getSeemoreProduct, "Promotion", products, adapter)
                }
                swipeRefresh_layout_3.isRefreshing = false
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        HomeActivity.sellingAdapter.notifyDataSetChanged()
        HomeActivity.promotionAdapter.notifyDataSetChanged()
        HomeActivity.latestAdapter.notifyDataSetChanged()
    }

    private fun setRecyclerProduct(list: ArrayList<Product>) {
        recycler_seemore.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@SeeMoreActivity, 2)
        recycler_seemore.layoutManager = layoutManager
        adapter = ProductAdapter(this@SeeMoreActivity, list)
        recycler_seemore.adapter = adapter
    }

    private fun getDataProduct(url: String, condition: String, list: ArrayList<Product>, adapter: ProductAdapter) {
        var requestQueue = Volley.newRequestQueue(this@SeeMoreActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String> {
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var id: Int
                    var category: String
                    var ptype: String
                    var pname: String
                    var price: Int
                    var description: String
                    var image1: String
                    var image2: String
                    var image3: String

                    for (i in 0 until jsonArray.length()) {
                        try {
                            var jsonObject = jsonArray.getJSONObject(i)
                            id = jsonObject.getInt("id")
                            category = jsonObject.getString("category")
                            ptype = jsonObject.getString("ptype")
                            pname = jsonObject.getString("pname")
                            price = jsonObject.getInt("price")
                            description = jsonObject.getString("description")
                            image1 = jsonObject.getString("image1")
                            image2 = jsonObject.getString("image2")
                            image3 = jsonObject.getString("image3")

                            list.add(Product(id, category, ptype, pname, price, description, image1, image2, image3))
                            adapter.notifyDataSetChanged()
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

    private fun setToolbar() {
        setSupportActionBar(toolBar_seemore)
        toolBar_seemore.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_seemore.setNavigationOnClickListener {
            finish()
        }
    }
}
