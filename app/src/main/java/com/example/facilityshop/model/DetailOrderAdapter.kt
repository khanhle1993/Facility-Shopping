package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facilityshop.R
import com.example.facilityshop.view.DetailProductActivity
import kotlinx.android.synthetic.main.item_detail_order.view.*

class DetailOrderAdapter : RecyclerView.Adapter<DetailOrderAdapter.ItemHolder>{

    var context: Context
    var list: ArrayList<DetailOrder>

    constructor(context: Context, list: ArrayList<DetailOrder>) {
        this.context = context
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_detail_order, parent,
            false)
        var itemHolder = ItemHolder(v)
        return itemHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var detailOrder = list.get(position)
        holder.textView_pname_detailOrder.text = detailOrder.pname
        holder.textView_pname_detailOrder.maxLines = 1
        holder.textView_pname_detailOrder.ellipsize = TextUtils.TruncateAt.END
        holder.textView_quantity_detailOrder.text = detailOrder.quantity
        holder.textView_price_detailOrder.text = detailOrder.price + " $"

        holder.textView_pname_detailOrder.setOnClickListener {
            var intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra("isDetailOrder", true)
            intent.putExtra("id3", detailOrder.idProduct)
            context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView_pname_detailOrder = itemView.textView_pname_detailOrder
        var textView_quantity_detailOrder = itemView.textView_quantity_detailOrder
        var textView_price_detailOrder = itemView.textView_price_detailOrder
    }


}