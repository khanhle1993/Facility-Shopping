package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.view.DetailProductActivity
import com.example.facilityshop.R
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.item_product.view.*
import org.json.JSONArray
import java.lang.Double
import java.text.DecimalFormat

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ItemHolder>{

    var context: Context
    var products: List<Product>
    var _position: Int = 0
    var idUser = ""

    constructor(context: Context, products: List<Product>) {
        this.context = context
        this.products = products
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        var itemHolder = ItemHolder(v)

        Paper.init(context)

        idUser = Paper.book().read("idUser", "null")

        return itemHolder
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        _position = position

        var product = products.get(position)

        Picasso.get().load(product.image1).placeholder(R.drawable.empty_image).into(holder.img_product)

        holder.text_product_name.text = product.pname
        holder.text_product_name.maxLines = 1
        holder.text_product_name.ellipsize = TextUtils.TruncateAt.END

        var decimalFormat = DecimalFormat("###,###,###")
        var price = product.price.toString()
        holder.text_product_price.text = decimalFormat.format(Double.valueOf(price)) + " $"
        holder.text_product_price.maxLines = 1
        holder.text_product_price.ellipsize = TextUtils.TruncateAt.END

        // Get state toggle_cart
        var requestQueue = Volley.newRequestQueue(context)
        var stringRequest = object : StringRequest(Method.POST, Server.getStateToggleCart, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var _idUser: String
                    var idProduct: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        _idUser = jsonObject.getInt("idUser").toString()
                        idProduct = jsonObject.getInt("idProduct").toString()

                        // If exist idUser and product in cart
                        if (idUser == _idUser && idProduct == product.id.toString()) {
                            holder.toggle_product_cart.isChecked = true
                        } else {
                            holder.toggle_product_cart.isChecked = false
                        }
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
                hashMap.put("idUser", idUser)
                hashMap.put("idProduct", product.id.toString())
                return hashMap
            }
        }
        requestQueue.add(stringRequest)

        // Get favourite state
        var requestQueue2 = Volley.newRequestQueue(context)
        var stringRequest2 = object : StringRequest(Method.POST, Server.getStateToggleFav, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var _idUser: String
                    var idProduct: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        _idUser = jsonObject.getInt("idUser").toString()
                        idProduct = jsonObject.getInt("idProduct").toString()

                        // If exist idUser and product in favourite
                        if (idUser == _idUser && idProduct == product.id.toString()) {
                            holder.toggle_product_fav.isChecked = true
                        } else {
                            holder.toggle_product_fav.isChecked = false
                        }
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
                hashMap.put("idUser", idUser)
                hashMap.put("idProduct", product.id.toString())
                return hashMap
            }
        }
        requestQueue2.add(stringRequest2)

        // Save and delete 1 item cart when click toggle
        holder.toggle_product_cart.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    // Change state toggle and save item into cart
                    holder.toggle_product_cart.isChecked = true

                    // Call CartInterface
                    var cartInterface = object : CartInterface {
                        override fun deleteProduct(idProduct: String, idUser: String) {
                            // Do nothing
                        }

                        override fun saveProduct(idProduct: String, idUser: String) {
                            var requestQueue: RequestQueue = Volley.newRequestQueue(context)
                            var stringRequest = object : StringRequest(Method.POST, Server.saveCart, object : Response.Listener<String> {
                                override fun onResponse(response: String?) {
                                    if (response == "Success") {
                                        Toast.makeText(context, "Added.", Toast.LENGTH_SHORT).show()
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
                    }
                    var idProduct = product.id.toString()
                    var idUser = Paper.book().read("idUser", "")
                    cartInterface.saveProduct(idProduct, idUser)
                } else {
                    // Change state toggle and delete item get out cart
                    holder.toggle_product_cart.isChecked = false

                    // Call CartInterface
                    var cartInterface = object : CartInterface {
                        override fun saveProduct(idProduct: String, idUser: String) {
                            // Do nothing
                        }

                        override fun deleteProduct(idProduct: String, idUser: String) {
                            var requestQueue: RequestQueue = Volley.newRequestQueue(context)
                            var stringRequest = object : StringRequest(Method.POST, Server.removeCart, object : Response.Listener<String>{
                                override fun onResponse(response: String?) {
                                    if (response.equals("Success remove 1 item")) {
                                        Toast.makeText(context, "Removed.", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }, object: Response.ErrorListener{
                                override fun onErrorResponse(error: VolleyError?) {
                                    android.util.Log.d("Error", error.toString())
                                }

                            }){
                                override fun getParams(): MutableMap<String, String> {
                                    var hashMap: HashMap<String, String> = HashMap()
                                    hashMap.put("idProduct", idProduct)
                                    hashMap.put("idUser", idUser)
                                    return hashMap
                                }
                            }

                            requestQueue.add(stringRequest)
                        }
                    }
                    var idProduct = product.id.toString()
                    var idUser = Paper.book().read("idUser", "")
                    cartInterface.deleteProduct(idProduct, idUser)
                }
            }

        })

        // Save and delete 1 item favourite when click toggle
        holder.toggle_product_fav.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1) {
                    holder.toggle_product_fav.isChecked = true
                    // Call FavouriteInterface
                    var fav = object : FavouriteInterface {
                        override fun saveFavourite(
                            idProduct: String,
                            idUser: String,
                            pname: String,
                            price: Int,
                            image: String
                        ) {
                            var requestQueue: RequestQueue = Volley.newRequestQueue(context)
                            var stringRequest = object : StringRequest(Method.POST, Server.saveFavourite, object : Response.Listener<String> {
                                override fun onResponse(response: String?) {
                                    if (response == "Success") {
                                        Toast.makeText(context, "Added.", Toast.LENGTH_SHORT).show()
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
                                    hashMap.put("pname", pname)
                                    hashMap.put("price", price.toString())
                                    hashMap.put("image", image)
                                    return hashMap
                                }
                            }
                            requestQueue.add(stringRequest)
                        }

                        override fun deleteFavourite(idProduct: String, idUser: String) {
                            // Do nothing
                        }
                    }
                    var idProduct = product.id
                    var idUser = Paper.book().read("idUser", "")
                    fav.saveFavourite(idProduct.toString(), idUser, product.pname, product.price, product.image1)
                } else {
                    holder.toggle_product_fav.isChecked = false
                    // Call FavouriteInterface
                    var fav = object : FavouriteInterface {
                        override fun saveFavourite(
                            idProduct: String,
                            idUser: String,
                            pname: String,
                            price: Int,
                            image: String
                        ) {
                            // Do nothing
                        }

                        override fun deleteFavourite(idProduct: String, idUser: String) {
                            var requestQueue: RequestQueue = Volley.newRequestQueue(context)
                            var stringRequest = object : StringRequest(Method.POST, Server.removeFavourite, object : Response.Listener<String> {
                                override fun onResponse(response: String?) {
                                    if (response == "Success") {
                                        Toast.makeText(context, "Removed.", Toast.LENGTH_SHORT).show()
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

                    }
                    var idProduct = product.id
                    var idUser = Paper.book().read("idUser", "")
                    fav.deleteFavourite(idProduct.toString(), idUser)
                }
            }

        })

        holder.img_product.setOnClickListener {
            var intent = Intent(context, DetailProductActivity::class.java)
            var id = product.id
            intent.putExtra("id", id) // send idProduct for DetailProductActivity
            context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_product = itemView.img_product
        var text_product_name = itemView.text_product_name
        var text_product_price = itemView.text_product_price
        var toggle_product_fav = itemView.toggle_product_fav
        var toggle_product_cart = itemView.toggle_product_cart
    }

}