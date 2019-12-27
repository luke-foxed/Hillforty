package org.wit.hillfortapp.views.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import org.wit.hillfortapp.R

// source: https://www.bignerdranch.com/blog/viewpager-without-fragments/
class CustomPagerAdapter(context: Context) : PagerAdapter() {

    val layouts: Array<Int> = arrayOf(
        R.layout.activity_main, R.layout.activity_hillfort_list,
        R.layout.activity_hillfort_maps, R.layout.activity_account
    )
    private val mContext: Context = context

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any =
        LayoutInflater.from(viewGroup.context).inflate(layouts[position], viewGroup, false).also {
            viewGroup.addView(it)
        }

    override fun destroyItem(
        collection: ViewGroup,
        position: Int,
        view: Any
    ) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return CustomPagerEnum.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val customPagerEnum = CustomPagerEnum.values()[position]
        return mContext.getString(customPagerEnum.titleResId)
    }

}