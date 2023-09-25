package com.cj1_project.googlesignin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

internal class GridRVAdapter(

    private val workoutList: List<GridViewModel>,
    private val context: Context
) :
    BaseAdapter() {
    // in base adapter class we are creating variables
    // for layout inflater, course image view and course text view.
    private var layoutInflater: LayoutInflater? = null
    private lateinit var workoutTV1: TextView
    private lateinit var workoutTV2: TextView
    private lateinit var workoutIV: ImageView

    // below method is use to return the count of course list
    override fun getCount(): Int {
        return workoutList.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): Any? {
        return null
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    // in below function we are getting individual item of grid view.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        // on blow line we are checking if layout inflater
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        // on the below line we are checking if convert view is null.
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = layoutInflater!!.inflate(R.layout.gridview_item1, null)
        }
        // on below line we are initializing our course image view
        // and course text view with their ids.
        workoutIV = convertView!!.findViewById(R.id.idIVWorkout)
        workoutTV1 = convertView!!.findViewById(R.id.gridText1)
        workoutTV2 = convertView!!.findViewById(R.id.gridText2)
        // on below line we are setting image for our course image view.
        workoutIV.setImageResource(workoutList[position].dataImg)
        workoutTV1.text = workoutList[position].dataName
        workoutTV2.text = workoutList[position].dataValue
        // at last we are returning our convert view.
        return convertView
    }
}
