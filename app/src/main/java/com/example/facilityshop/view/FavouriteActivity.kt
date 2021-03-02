package com.example.facilityshop.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.Favourite
import com.example.facilityshop.model.FavouriteAdapter
import com.example.facilityshop.model.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_favourite.*
import org.json.JSONArray

class FavouriteActivity : AppCompatActivity() {

    lateinit var favourites: ArrayList<Favourite>
    companion object {
        lateinit var adapter: FavouriteAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        setToolbar()

        setRecycler()


        Paper.init(this@FavouriteActivity)
        var idUser = Paper.book().read("idUser", "")

        getFavourite(idUser)

        textView_remove_all_fav.visibility = View.GONE

        textView_remove_all_fav.setOnClickListener {
            var alertDialog = AlertDialog.Builder(this@FavouriteActivity, R.style.AlertDialogTheme)
            alertDialog.setTitle("Notification")
            alertDialog.setMessage("Do you want to remove all ?")
            alertDialog.setPositiveButton("OK") {dialog, which ->
                removeAllFavourite(idUser)
            }
            alertDialog.setNegativeButton("Cancel") {dialog, which ->
                dialog.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun removeAllFavourite(idUSer: String) {
        var requestQueue = Volley.newRequestQueue(this@FavouriteActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.removeAllFav, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    if (response == "Success") {
                        favourites.removeAll(favourites)
                        adapter.notifyDataSetChanged()
                        textView_remove_all_fav.visibility = View.GONE
                        Toast.makeText(this@FavouriteActivity, "Remove all success.", Toast.LENGTH_SHORT).show()
                    }
                }

            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    android.util.Log.d("Error", error.toString())
                }

            }) {
            override fun getParams(): MutableMap<String, String> {
                var hashMap: HashMap<String, String> = HashMap()
                hashMap.put("idUser", idUSer)
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_favourite)
        toolBar_favourite.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_favourite.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setRecycler() {
        recycler_favourite.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(this@FavouriteActivity, LinearLayoutManager.VERTICAL, false)
        recycler_favourite.layoutManager = layoutManager
        favourites = ArrayList()
        adapter = FavouriteAdapter(
            this@FavouriteActivity, favourites, textView_remove_all_fav)
        recycler_favourite.adapter = adapter
    }

    private fun getFavourite(idUser: String) {
        var requestQueue = Volley.newRequestQueue(this@FavouriteActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.getFavourite, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var id: Int
                    var idProduct: Int
                    var idUser: Int
                    var pname: String
                    var price: Int
                    var image: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        id = jsonObject.getInt("id")
                        idProduct = jsonObject.getInt("idProduct")
                        idUser = jsonObject.getInt("idUser")
                        pname = jsonObject.getString("pname")
                        price = jsonObject.getInt("price")
                        image = jsonObject.getString("image")

                        favourites.add(Favourite(id, idProduct, idUser, pname, price, image))
                        adapter.notifyDataSetChanged()
                    }

                    if (favourites.size > 0) {
                        textView_remove_all_fav.visibility = View.VISIBLE
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
                return hashMap
            }
        }
        requestQueue.add(stringRequest)
    }
}
