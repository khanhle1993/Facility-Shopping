package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.*
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONArray
import org.json.JSONException

class SearchActivity : AppCompatActivity() {

    lateinit var products: ArrayList<SearchItem>
    lateinit var adapter: SearchProductAdapter
    lateinit var results: ArrayList<Product>
    lateinit var resultAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        getDataProduct()



        imageView_refresh_search.setOnClickListener {
            completeText_search.text = Editable.Factory.getInstance().newEditable("")
            completeText_search.hint = "Search for your product..."

            textView36.visibility = View.GONE
        }

        setToolbar()

    }

    private fun getDataProduct() {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@SearchActivity)
        var stringRequest = object : StringRequest(Method.GET, Server.searchProduct, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    products = ArrayList()

                    var jsonArray = JSONArray(response)
                    var pname: String
                    for (i in 0 until jsonArray.length()) {
                        try {
                            var jsonObject = jsonArray.getJSONObject(i)
                            pname = jsonObject.getString("pname")

                            products.add(SearchItem(pname))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    // Set adapter for AutoCompleteTextView and set event click
                    adapter = SearchProductAdapter(this@SearchActivity, products)
                    completeText_search.threshold = 1
                    completeText_search.setAdapter(adapter)
                    completeText_search.setOnItemClickListener(object : AdapterView.OnItemClickListener{
                        override fun onItemClick(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            progress_bar3.visibility = View.VISIBLE

                            var item = products.get(position).productName

                            if (item.length > 20)
                                completeText_search.text = Editable.Factory.getInstance().newEditable(item.substring(0, 20) + "...")

                            results = ArrayList()
                            val handler = Handler()
                            handler.postDelayed(Runnable {
                                progress_bar3.visibility = View.GONE
                                getDataSearchProduct(results, item)
                            }, 2000)

                            textView36.visibility = View.VISIBLE
                        }

                    })
                }
            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }
        })

        {}

        requestQueue.add(stringRequest)
    }

    private fun getDataSearchProduct(_list: ArrayList<Product>, pname: String) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@SearchActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.searchResult, object : Response.Listener<String>{
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

                            _list.add(Product(id, category, ptype, pname, price, description, image1, image2, image3))
                            setRecycler(_list)
                        } catch (e: JSONException) {
                            e.printStackTrace()
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
                hashMap.put("pname", pname)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun setRecycler(list: ArrayList<Product>) {
        recycler_result_search.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@SearchActivity, 2)
        recycler_result_search.layoutManager = layoutManager
        resultAdapter = ProductAdapter(this@SearchActivity, list)
        recycler_result_search.adapter = resultAdapter
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_search)
        toolBar_search.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_search.setNavigationOnClickListener {
            finish()
        }
    }
}


