package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.view.DetailProductActivity
import com.example.facilityshop.view.FavouriteActivity
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.item_favourite.view.*
import java.lang.Double
import java.text.DecimalFormat

class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.ItemHolder> {

    var context: Context
    var list: ArrayList<Favourite>
    var textView_remove_all: TextView

    constructor(context: Context, list: ArrayList<Favourite>, textView_remove_all: TextView) : super() {
        this.context = context
        this.list = list
        this.textView_remove_all = textView_remove_all
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent,
            false)
        var itemHolder = ItemHolder(v)

        Paper.init(context)

        return itemHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var favourite = list.get(position)

        Picasso.get().load(favourite.image).into(holder.img_favourite)

        holder.textView_fav_pname.text = favourite.pname
        holder.textView_fav_pname.maxLines = 2
        holder.textView_fav_pname.ellipsize = TextUtils.TruncateAt.END

        var decimalFormat = DecimalFormat("###,###,###")
        var price = favourite.price.toString()
        holder.textView_fav_price.text = decimalFormat.format(Double.valueOf(price)) + " $"
        holder.textView_fav_price.maxLines = 1
        holder.textView_fav_price.ellipsize = TextUtils.TruncateAt.END

        holder.img_close_fav.setOnClickListener {
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
                                list.remove(favourite)
                                FavouriteActivity.adapter.notifyDataSetChanged()

                                if (list.size == 0) {
                                    textView_remove_all.visibility = View.GONE
                                }
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
            var idUser = Paper.book().read("idUser", "")
            var idProduct = favourite.idProduct
            fav.deleteFavourite(idProduct.toString(), idUser)
        }

        holder.img_favourite.setOnClickListener {
            var intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("id", favourite.idProduct)
            context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_favourite = itemView.img_favourite
        var textView_fav_pname = itemView.textView_fav_pname
        var textView_fav_price = itemView.textView_fav_price
        var img_close_fav = itemView.img_close_fav
    }
}