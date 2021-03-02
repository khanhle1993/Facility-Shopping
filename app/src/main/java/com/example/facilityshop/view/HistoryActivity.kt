package com.example.facilityshop.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.model.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_history.*
import org.json.JSONArray

class HistoryActivity : AppCompatActivity() {

    lateinit var arrayHistory: ArrayList<History>
    companion object {
        lateinit var adapter: HistoryAdapter
    }
    var idUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setToolbar()
        Paper.init(this@HistoryActivity)

        idUser = Paper.book().read("idUser", "")

        textView_remove_all_his.visibility = View.GONE

        arrayHistory = ArrayList()
        setRecycler(arrayHistory)
        getOrder(idUser)

        textView_remove_all_his.setOnClickListener {
            removeAllHistory(idUser)
        }

        refreshData()
    }

    private fun refreshData() {
        swipeRefresh_layout_4.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefresh_layout_4.isRefreshing = true
                arrayHistory = ArrayList()
                setRecycler(arrayHistory)
                getOrder(idUser)
                swipeRefresh_layout_4.isRefreshing = false
            }

        })
    }

    private fun removeAllHistory(idUser: String) {
        var alertDialog = AlertDialog.Builder(this@HistoryActivity, R.style.AlertDialogTheme)
        alertDialog.setTitle("Notification")
        alertDialog.setMessage("Do you want to remove all ?")
        alertDialog.setPositiveButton("OK") {dialog, which ->

            var requestQueue = Volley.newRequestQueue(this@HistoryActivity)
            var stringRequest = object : StringRequest(Method.POST, Server.removeAllHistory,
                object : Response.Listener<String> {
                    override fun onResponse(response: String?) {
                        if (response == "Success") {
                            alertDialog.setCancelable(true)
                            arrayHistory.removeAll(arrayHistory)
                            adapter.notifyDataSetChanged()
                            textView_remove_all_his.visibility = View.GONE
                            Toast.makeText(this@HistoryActivity, "Remove all success.", Toast.LENGTH_SHORT).show()
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
                    hashMap.put("idUser", idUser)
                    return hashMap
                }
            }
            requestQueue.add(stringRequest)
        }
        alertDialog.setNegativeButton("Cancel") {dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setToolbar() {
        setSupportActionBar(toolBar_history)
        toolBar_history.setNavigationIcon(R.drawable.ic_arrow_back)
        toolBar_history.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setRecycler(list: ArrayList<History>) {
        recycler_history.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
        recycler_history.layoutManager = layoutManager
        adapter = HistoryAdapter(this@HistoryActivity, list, textView_remove_all_his)
        recycler_history.adapter = adapter
    }

    private fun getOrder(idUser: String) {
        var requestQueue = Volley.newRequestQueue(this@HistoryActivity)
        var stringRequest = object : StringRequest(Method.POST, Server.getOrder, object : Response.Listener<String>{
            override fun onResponse(response: String?) {
                if (response != null) {
                    var jsonArray = JSONArray(response)

                    var id: Int
                    var buyer: String
                    var dateOrder: String
                    var timeOrder: String
                    var state: String
                    var totalAmount: String

                    for (i in 0 until jsonArray.length()) {
                        var jsonObject = jsonArray.getJSONObject(i)
                        id = jsonObject.getInt("id")
                        buyer = jsonObject.getString("buyer")
                        dateOrder = jsonObject.getString("dateOrder")
                        timeOrder = jsonObject.getString("timeOrder")
                        state = jsonObject.getString("state")
                        totalAmount = jsonObject.getString("totalAmount")

                        arrayHistory.add(History(id, buyer, dateOrder, timeOrder, state, totalAmount))
                        adapter.notifyDataSetChanged()
                    }

                    if (arrayHistory.size > 0) {
                        textView_remove_all_his.visibility = View.VISIBLE
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
