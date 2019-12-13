package org.wit.hillfortapp.views.hillfort

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import org.wit.hillfortapp.models.ImageModel

// credit: https://codinginflow.com/tutorials/android/picasso-image-slider
class HillfortImageAdapter(private var images: ArrayList<ImageModel>, val context: Context) :
    PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this.context).load(images[position].uri).into(imageView)
        container.addView(imageView)
        return imageView
    }

    override fun getCount(): Int = images.size

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}