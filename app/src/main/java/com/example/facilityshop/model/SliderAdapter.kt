package com.example.facilityshop.model

import android.content.Context
import android.view.ViewGroup
import com.smarteist.autoimageslider.SliderViewAdapter
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.facilityshop.R

class SliderAdapter(private val context: Context) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {

        when (position) {
            0 -> Glide.with(viewHolder.itemView)
                .load("https://cdn.pixabay.com/photo/2015/10/12/15/18/store-984393__340.jpg")
                .into(viewHolder.imageViewBackground)
            1 -> Glide.with(viewHolder.itemView)
                .load("https://cdn.pixabay.com/photo/2014/03/08/22/32/escalator-283448__340.jpg")
                .into(viewHolder.imageViewBackground)
            2 -> Glide.with(viewHolder.itemView)
                .load("https://cdn.pixabay.com/photo/2017/12/26/09/15/woman-3040029__340.jpg")
                .into(viewHolder.imageViewBackground)
            else -> Glide.with(viewHolder.itemView)
                .load("https://cdn.pixabay.com/photo/2016/01/27/04/32/books-1163695__340.jpg")
                .into(viewHolder.imageViewBackground)
        }

    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return 4
    }

    inner class SliderAdapterVH(var itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {
        var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
        }
    }
}