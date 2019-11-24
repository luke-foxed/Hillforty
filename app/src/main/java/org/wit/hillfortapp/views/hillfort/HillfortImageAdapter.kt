package org.wit.hillfortapp.views.hillfort

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import org.wit.hillfortapp.helpers.readImageFromPath

// credit: https://codinginflow.com/tutorials/android/picasso-image-slider
class HillfortImageAdapter constructor(private var images: ArrayList<String>, val context: Context) :
    PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageBitmap(readImageFromPath(context, images[position]))
        container.addView(imageView)
        return imageView
    }

    override fun getCount(): Int = images.size

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}