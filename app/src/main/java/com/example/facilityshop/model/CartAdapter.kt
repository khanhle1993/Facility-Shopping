package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.view.*
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.item_cart.view.*
import org.json.JSONArray
import org.json.JSONException
import java.lang.Double
import java.text.DecimalFormat

class CartAdapter : RecyclerView.Adapter<CartAdapter.ItemHolder>{

    var context: Context
    var carts: ArrayList<Cart>
    var textView_total_price: TextView
    var textView_remove_all: TextView
    var button_payment: Button
    var totalPrice = 0
    var idUser = ""
    lateinit var arrayRemovedItem: ArrayList<String>

    constructor(context: Context, carts: ArrayList<Cart>, textView_total_price: TextView, textView_remove_all: TextView, button_payment: Button) {
        this.context = context
        this.carts = carts
        this.textView_total_price = textView_total_price
        this.textView_remove_all = textView_remove_all
        this.button_payment = button_payment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        var itemHolder = ItemHolder(v)
        Paper.init(context)
        idUser = Paper.book().read("idUser", "") // Get from HomeActivity
        arrayRemovedItem = ArrayList()

        return itemHolder
    }

    override fun getItemCount(): Int {
        return carts.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var cart = carts.get(position)

        Picasso.get().load(cart.image).into(holder.img_cart)

        holder.textView_cart_pname.text = cart.pname
        holder.textView_cart_pname.maxLines = 1
        holder.textView_cart_pname.ellipsize = TextUtils.TruncateAt.END

        var decimalFormat = DecimalFormat("###,###,###")
        holder.textView_cart_price.text = decimalFormat.format(Double.valueOf(cart.price.toString())) + " $"

        val quantitys = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        var spinnerAdapter = ArrayAdapter<Int>(context,
            R.layout.spinner_text, quantitys)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        holder.spinner_cart.adapter = spinnerAdapter
        holder.spinner_cart.setSelection(cart.quantity - 1)

        var quantity: Int = Integer.valueOf(holder.spinner_cart.selectedItem.toString())
        var price: Int = quantity * cart.price
        var iCurrentSelection = holder.spinner_cart.getSelectedItemPosition()
        totalPrice += price

        holder.spinner_cart.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var priceSelected = quantitys.get(p2) * cart.price
                if (iCurrentSelection != p2) {
                    if (p2 > iCurrentSelection) {
                        totalPrice = (totalPrice + priceSelected) - (quantitys.get(iCurrentSelection) * cart.price)
                        // var decimalFormat = DecimalFormat("###,###,###")
                        textView_total_price.text = totalPrice.toString()
                        // Save quantity in db
                        saveQuantity(cart.idProduct.toString(), quantitys.get(p2).toString())

                    } else if (p2 < iCurrentSelection){
                        totalPrice -= ((quantitys.get(iCurrentSelection) * cart.price) -
                                (quantitys.get(p2) * cart.price))
                        // var decimalFormat = DecimalFormat("###,###,###")
                        textView_total_price.text = totalPrice.toString()
                        saveQuantity(cart.idProduct.toString(), quantitys.get(p2).toString())
                    }
                }
                iCurrentSelection = p2

            }
        })

        holder.textView_remove.setOnClickListener {
            var requestQueue: RequestQueue = Volley.newRequestQueue(context)
            var stringRequest = object : StringRequest(Method.POST, Server.removeCart, object : Response.Listener<String>{
                override fun onResponse(response: String?) {
                    if (response.equals("Success remove 1 item")) {
                        carts.remove(cart)

                        CartActivity.adapter.notifyItemRemoved(position)
                        Toast.makeText(context, "Removed 1 item.", Toast.LENGTH_SHORT).show()

                        // Reload data when remove item, then set total again
                        var requestQueue: RequestQueue = Volley.newRequestQueue(context)
                        var stringRequest = object : StringRequest(Method.POST,
                            Server.getCart, object : Response.Listener<String>{
                            override fun onResponse(response: String?) {
                                if (response != null) {
                                    var jsonArray = JSONArray(response)

                                    var id: Int
                                    var idProduct: Int
                                    var pname: String
                                    var price: Int
                                    var quantity: Int
                                    var image: String

                                    var listReload: ArrayList<Cart> = ArrayList()

                                    for (i in 0 until jsonArray.length()) {
                                        try {
                                            var jsonObject = jsonArray.getJSONObject(i)
                                            id = jsonObject.getInt("id")
                                            idProduct = jsonObject.getInt("idProduct")
                                            pname = jsonObject.getString("pname")
                                            price = jsonObject.getInt("price")
                                            quantity = jsonObject.getInt("quantity")
                                            image = jsonObject.getString("image")

                                            listReload.add(Cart(id, idProduct, pname, price, quantity, image))

                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }

                                    var total = 0
                                    for (i in 0 until listReload.size) {
                                        total += listReload.get(i).price * listReload.get(i).quantity
                                    }
                                    var decimalFormat = DecimalFormat("###,###,###")
                                    textView_total_price.text = decimalFormat.format(total)

                                }
                            }


                        }, object: Response.ErrorListener{
                            override fun onErrorResponse(error: VolleyError?) {
                                Log.d("Error", error.toString())
                            }

                        }){
                            override fun getParams(): MutableMap<String, String> {
                                var hashMap: HashMap<String, String> = HashMap()
                                hashMap.put("idUser", idUser)
                                return hashMap
                            }
                        }

                        requestQueue.add(stringRequest)

                        if (carts.size == 0) {
                            textView_remove_all.visibility = View.GONE
                            button_payment.isEnabled = false
                            button_payment.setBackgroundColor(Color.parseColor("#FA9D96"))
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
                    hashMap.put("idProduct", cart.idProduct.toString())
                    hashMap.put("idUser", idUser)
                    return hashMap
                }
            }

            requestQueue.add(stringRequest)
        }

        holder.relative_cart.setOnClickListener {
            var intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("isCart", true)
            intent.putExtra("id2", cart.idProduct)
            context.startActivity(intent)
        }
    }


    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_cart = itemView.img_cart
        var textView_cart_pname = itemView.textView_cart_pname
        var textView_cart_price = itemView.textView_cart_price
        var spinner_cart = itemView.spinner_cart
        var textView_remove = itemView.textView_remove
        var relative_cart = itemView.relative_cart
    }

    private fun saveQuantity(idProduct: String, quantity: String) {
        var requestQueue = Volley.newRequestQueue(context)
        var stringRequest = object : StringRequest(Method.POST, Server.updateCart, object : Response.Listener<String>{
            override fun onResponse(response: String?) {

            }

        }, object: Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("Error", error.toString())
            }

        }){
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("idUser", idUser)
                hashMap.put("idProduct", idProduct)
                hashMap.put("quantity", quantity)
                return hashMap
            }
        }

        requestQueue.add(stringRequest)
    }

}