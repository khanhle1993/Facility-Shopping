package com.example.facilityshop.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Product
import com.example.facilityshop.model.ProductAdapter
import com.example.facilityshop.model.Server
import kotlinx.android.synthetic.main.activity_product_by_category.*
import org.json.JSONArray
import org.json.JSONException


class ProductByCategoryActivity : AppCompatActivity() {

    var isChecking = true
    lateinit var products: ArrayList<Product>
    companion object {
        lateinit var adapter: ProductAdapter
        lateinit var adapter2: ProductAdapter
    }
    lateinit var sellings: ArrayList<Product>
    lateinit var latests: ArrayList<Product>
    lateinit var promotions: ArrayList<Product>
    lateinit var installments: ArrayList<Product>
    lateinit var noneTypes: ArrayList<Product>
    lateinit var spinnerAdapter: ArrayAdapter<String>
    var priceRange = ""
    var cateName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_by_category)

        setToolbar()

        val prices = arrayOf("None", "1 - 300 $", "300 - 600 $", "600 - 900 $", "900 - 1200 $", "More than 1200 $")
        spinnerAdapter = ArrayAdapter(this@ProductByCategoryActivity,
            R.layout.spinner_text, prices)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        spinner_price_filter.adapter = spinnerAdapter
        spinner_price_filter.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                priceRange = p0!!.getItemAtPosition(p2).toString()

            }
        })

        toggle_dropdown_filter.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    rdg_filter.visibility = View.GONE
                    rdg_filter_2.visibility = View.GONE
                    text5.visibility = View.GONE
                    relative_spinner.visibility = View.GONE
                } else {
                    rdg_filter.visibility = View.VISIBLE
                    rdg_filter_2.visibility = View.VISIBLE
                    text5.visibility = View.VISIBLE
                    relative_spinner.visibility = View.VISIBLE
                }
            }

        })



        rdg_filter.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if (p1 != -1 && isChecking) {
                    isChecking = false
                    rdg_filter_2.clearCheck()
                }
                isChecking = true
            }

        })

        rdg_filter_2.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if (p1 != -1 && isChecking) {
                    isChecking = false
                    rdg_filter.clearCheck()
                }
                isChecking = true
            }

        })

        var intent = intent
        cateName = intent.getStringExtra("cateName") // Get from ProductByCategoryActivity
        products = ArrayList()
        setRecyclerProduct(products)
        getDataProduct(Server.getProductByCategory, cateName, products, adapter)

        text_pname.text = cateName

        setSearchIcon()

        img_refresh_filter.setOnClickListener {
            rdg_filter.clearCheck()
            rdg_filter_2.clearCheck()
            var position = spinnerAdapter.getPosition("None")
            spinner_price_filter.setSelection(position)
            recycler_product_category.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        refreshData()

    }

    private fun refreshData() {
        swipeRefresh_layout_2.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefresh_layout_2.isRefreshing = true

                rdg_filter.clearCheck()
                rdg_filter_2.clearCheck()
                var position = spinnerAdapter.getPosition("None")
                spinner_price_filter.setSelection(position)

                products = ArrayList()
                setRecyclerProduct(products)
                getDataProduct(Server.getProductByCategory, cateName, products, adapter)

                swipeRefresh_layout_2.isRefreshing = false
            }

        })

    }

    private fun setSearchIcon() {
        img_search_filter.setOnClickListener {
            progressBar2.visibility = View.VISIBLE
            if (rdButton_selling.isChecked) {
                sellings = ArrayList()
                chooseOption(sellings, "Selling", cateName, priceRange)
            }
            if (rdButton_latest.isChecked) {
                latests = ArrayList()
                chooseOption(latests, "Latest", cateName, priceRange)
            }
            if (rdButton_promotion.isChecked) {
                promotions = ArrayList()
                chooseOption(promotions, "Promotion", cateName, priceRange)
            }
            if (rdButton_installment.isChecked) {
                installments = ArrayList()
                chooseOption(installments, "Installment", cateName, priceRange)
            }
            if (!rdButton_selling.isChecked && !rdButton_latest.isChecked && !rdButton_promotion.isChecked
                && !rdButton_installment.isChecked) {
                noneTypes = ArrayList()
                chooseOption(noneTypes, "None type", cateName, priceRange)
            }
        }
    }

    private fun chooseOption(_list: ArrayList<Product>, _ptype: String, _category: String, _priceRange: String) {
        when (_priceRange) {
            "None" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
            "1 - 300 $" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
            "300 - 600 $" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
            "600 - 900 $" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
            "900 - 1200 $" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
            "More than 1200 $" -> {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressBar2.visibility = View.GONE
                    getProductBySearchAction(_list, _ptype, _category, _priceRange)
                }, 1500)
            }
        }
    }

    private fun getProductBySearchAction(_list: ArrayList<Product>, _ptype: String, _category: String, _priceRange: String) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@ProductByCategoryActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.filterProduct, object : Response.Listener<String>{
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
                            setRecyclerFilter(_list)
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
                hashMap.put("ptype", _ptype)
                hashMap.put("category", _category)
                hashMap.put("priceRange", _priceRange)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun getDataProduct(url: String, condition: String, products: ArrayList<Product>, adapter: ProductAdapter
    ) {
        var requestQueue: RequestQueue = Volley.newRequestQueue(this@ProductByCategoryActivity)
        var stringRequest = object : StringRequest(Method.POST, url, object : Response.Listener<String>{
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

                            products.add(Product(id, category, ptype, pname, price, description, image1, image2, image3))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("condition", condition)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)

    }

    private fun setRecyclerFilter(list: ArrayList<Product>) {
        recycler_product_category.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@ProductByCategoryActivity, 2)
        recycler_product_category.layoutManager = layoutManager
        adapter2 = ProductAdapter(this@ProductByCategoryActivity, list)
        recycler_product_category.adapter = adapter2
    }

    private fun setRecyclerProduct(list: ArrayList<Product>) {
        recycler_product_category.setHasFixedSize(true)
        var layoutManager = GridLayoutManager(this@ProductByCategoryActivity, 2)
        recycler_product_category.layoutManager = layoutManager
        adapter = ProductAdapter(this@ProductByCategoryActivity, list)
        recycler_product_category.adapter = adapter
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_product_by_category)
        toolBar_product_by_category.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_product_by_category.setNavigationOnClickListener {
            finish()
        }
    }

}
