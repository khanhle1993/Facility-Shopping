package com.example.facilityshop.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facilityshop.view.ProductByCategoryActivity
import com.example.facilityshop.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ItemHolder>{

    var context: Context
    var categorys: List<Category>

    constructor(context: Context, categorys: List<Category>) {
        this.context = context
        this.categorys = categorys
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_category, parent,
            false)
        var itemHolder = ItemHolder(v)
        return itemHolder
    }

    override fun getItemCount(): Int {
        return categorys.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var category = categorys.get(position)
        Picasso.get().load(category.image).placeholder(R.drawable.empty_image).into(holder.img_category)
        holder.text_name_category.text = category.name

        holder.img_category.setOnClickListener {
            var cateName = category.name
            var intent = Intent(context, ProductByCategoryActivity::class.java)
            intent.putExtra("cateName", cateName)
            context.startActivity(intent)
        }

        holder.text_name_category.setOnClickListener {
            var cateName = category.name
            var intent = Intent(context, ProductByCategoryActivity::class.java)
            intent.putExtra("cateName", cateName)
            context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_category = itemView.img_category
        var text_name_category = itemView.text_name_category
    }


}