package com.sam.gogozoo.facilityDialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes

class FacilityAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val list: List<String>):
    ArrayAdapter<String>(context, layoutResource, list) {

    private var mPois: List<String> = list

    override fun getCount(): Int {
        return mPois.size
    }

    override fun getItem(p0: Int): String? {
        return mPois.get(p0)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = mPois[position]
        return view
    }

}