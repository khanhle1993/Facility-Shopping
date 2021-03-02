package com.example.facilityshop.model

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.facilityshop.R


class SearchProductAdapter : ArrayAdapter<SearchItem> {

    var lists: ArrayList<SearchItem> = ArrayList()
    lateinit var suggestions: ArrayList<SearchItem>
    lateinit var itemsAll: ArrayList<SearchItem>
    var mContext: Context

    constructor(
        context: Context,
        _lists: ArrayList<SearchItem>
    ) : super(context, 0, _lists) {
        mContext = context
        lists = _lists

        this.itemsAll = lists.clone() as ArrayList<SearchItem>
        this.suggestions = ArrayList()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var _convertView = convertView
        if (_convertView == null) {
            _convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_product, parent, false)
        }
        var productItem = lists.get(position)

        var textView_name = _convertView!!.findViewById(R.id.textView_name) as TextView
        textView_name.text = productItem.productName
        textView_name.maxLines = 1
        textView_name.ellipsize = TextUtils.TruncateAt.END
        if (textView_name.text.length > 20)
            textView_name.text = textView_name.text.substring(0, 20) + "..."

        return _convertView
    }

    override fun getFilter(): Filter {
        return productFilter
    }

    private val productFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            return if (constraint != null) {
                suggestions.clear()
                for (product in itemsAll) {
                    if (product.productName.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(product)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults?
        ) {
            if (results != null && results.count > 0) {
                val filteredList: ArrayList<SearchItem> = (results!!.values as ArrayList<SearchItem>?)!!
                clear()
                for (c in filteredList) {
                    add(c)
                }
                notifyDataSetChanged()
            } else {
                return
            }
        }

        override fun convertResultToString(resultValue: Any): CharSequence? {
            return (resultValue as SearchItem).productName
        }
    }

}