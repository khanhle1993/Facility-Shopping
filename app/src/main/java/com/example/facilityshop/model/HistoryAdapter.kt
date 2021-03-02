package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.facilityshop.R
import com.example.facilityshop.view.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ItemHolder> {

    var context: Context
    var list: ArrayList<History>
    var textView_remove_all: TextView

    constructor(context: Context, list: ArrayList<History>, textView_remove_all: TextView) : super() {
        this.context = context
        this.list = list
        this.textView_remove_all = textView_remove_all
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent,
            false)
        var itemHolder = ItemHolder(v)

        Paper.init(context)

        return itemHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var history = list.get(position)

        holder.textView_dateOrder.text = history.dateOrder
        holder.textView_state.text = history.state
        holder.textView_num_order.text = "Number order: #" + (position + 1)
        holder.textView_total_amount_history.text =  history.totalAmount + " $"

        var idUser = Paper.book().read("idUser", "")

        if (holder.textView_state.text != "Cancelled") { // If state == "Cancelled" is only 1 context menu is "Delete"
            holder.itemView.setOnCreateContextMenuListener(object : View.OnCreateContextMenuListener{
                override fun onCreateContextMenu(
                    p0: ContextMenu?,
                    p1: View?,
                    p2: ContextMenu.ContextMenuInfo?
                ) {
                    p0!!.add("Delete").setOnMenuItemClickListener (object :  MenuItem.OnMenuItemClickListener{
                        override fun onMenuItemClick(p0: MenuItem?): Boolean {
                            if (p0!!.itemId == 0) {
                                var requestQueue = Volley.newRequestQueue(context)
                                var stringRequest = object : StringRequest(Method.POST, Server.removeOrder, object : Response.Listener<String>{
                                    override fun onResponse(response: String?) {
                                        if (response.equals("Success")) {
                                            Toast.makeText(context, "Remove success.", Toast.LENGTH_SHORT).show()
                                            list.remove(history)
                                            HistoryActivity.adapter.notifyItemRemoved(position)

                                            if (list.size == 0) {
                                                textView_remove_all.visibility = View.GONE
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
                                        hashMap.put("id", history.id.toString())
                                        hashMap.put("idUser", idUser)
                                        return hashMap
                                    }
                                }
                                requestQueue.add(stringRequest)
                            }
                            return true
                        }

                    })

                    p0.add("Cancel this order").setOnMenuItemClickListener (object :  MenuItem.OnMenuItemClickListener{
                        override fun onMenuItemClick(p0: MenuItem?): Boolean {
                            if (p0!!.itemId == 0) {
                                var alertDialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                                alertDialog.setTitle("Notification")
                                alertDialog.setMessage("Are you sure want to cancel this order ?")
                                alertDialog.setPositiveButton("OK") {dialog, which ->
                                    alertDialog.setCancelable(true)
                                    var requestQueue = Volley.newRequestQueue(context)
                                    var stringRequest = object : StringRequest(Method.POST, Server.cancelOrder, object : Response.Listener<String>{
                                        override fun onResponse(response: String?) {
                                            if (response.equals("Success")) {
                                                Toast.makeText(context, "Cancelled.", Toast.LENGTH_SHORT).show()
                                                holder.textView_state.text = "Cancelled"
                                                holder.textView_state.setTextColor(Color.parseColor("#808080"))
                                                holder.textView_state.setTypeface(holder.textView_state.typeface, Typeface.ITALIC)
                                                holder.button_view_detail_order.isEnabled = false
                                                holder.button_view_detail_order.setBackgroundColor(Color.parseColor("#80FFF2"))
                                            }
                                        }

                                    }, object: Response.ErrorListener{
                                        override fun onErrorResponse(error: VolleyError?) {
                                            android.util.Log.d("Error", error.toString())
                                        }

                                    }){
                                        override fun getParams(): MutableMap<String, String> {
                                            var hashMap: HashMap<String, String> = HashMap()
                                            hashMap.put("id", history.id.toString())
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
                            return true
                        }

                    })
                }

            })
        } else {
            holder.itemView.setOnCreateContextMenuListener(object : View.OnCreateContextMenuListener{
                override fun onCreateContextMenu(
                    p0: ContextMenu?,
                    p1: View?,
                    p2: ContextMenu.ContextMenuInfo?
                ) {
                    p0!!.add("Delete").setOnMenuItemClickListener (object :  MenuItem.OnMenuItemClickListener{
                        override fun onMenuItemClick(p0: MenuItem?): Boolean {
                            if (p0!!.itemId == 0) {
                                var requestQueue = Volley.newRequestQueue(context)
                                var stringRequest = object : StringRequest(Method.POST, Server.removeOrder, object : Response.Listener<String>{
                                    override fun onResponse(response: String?) {
                                        if (response.equals("Success")) {
                                            Toast.makeText(context, "Remove success.", Toast.LENGTH_SHORT).show()
                                            list.remove(history)
                                            HistoryActivity.adapter.notifyItemRemoved(position)

                                            if (list.size == 0) {
                                                textView_remove_all.visibility = View.GONE
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
                                        hashMap.put("id", history.id.toString())
                                        hashMap.put("idUser", idUser)
                                        return hashMap
                                    }
                                }
                                requestQueue.add(stringRequest)
                            }
                            return true
                        }

                    })
                }

            })
        }

        if (holder.textView_state.text == "Confirmmed") {
            holder.textView_state.setTextColor(Color.parseColor("#4363d8"))
            holder.textView_state.setTypeface(holder.textView_state.typeface, Typeface.BOLD_ITALIC)
        } else if (holder.textView_state.text == "Cancelled") {
            holder.textView_state.setTextColor(Color.parseColor("#808080"))
            holder.textView_state.setTypeface(holder.textView_state.typeface, Typeface.ITALIC)
            holder.button_view_detail_order.isEnabled = false
            holder.button_view_detail_order.setBackgroundColor(Color.parseColor("#80FFF2"))
        } else {
            holder.textView_state.setTextColor(Color.parseColor("#f58231"))
            holder.textView_state.setTypeface(holder.textView_state.typeface, Typeface.ITALIC)
        }

        holder.button_view_detail_order.setOnClickListener {
            var intent = Intent(context, DetailOrderActivity::class.java)
            var bundle = Bundle()
            bundle.putString("idOrder", history.id.toString())
            bundle.putString("buyer", history.buyer)
            bundle.putString("dateOrder", history.dateOrder)
            bundle.putString("timeOrder", history.timeOrder)
            bundle.putString("numOrder", (position + 1).toString())
            intent.putExtra("bundleDetail", bundle)
            context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView_dateOrder = itemView.textView_dateOrder
        var textView_state = itemView.textView_state
        var textView_num_order = itemView.textView_num_order
        var textView_total_amount_history = itemView.textView_total_amount_history
        var button_view_detail_order = itemView.button_view_detail_order
    }
}