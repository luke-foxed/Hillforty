package org.wit.hillfortapp.views.main

import org.wit.hillfortapp.R


enum class CustomPagerEnum(val titleResId: Int, val layoutResId: Int) {

    MAIN(1, R.layout.activity_main),
    HILLFORT(2, R.layout.activity_hillfort_list),
    MAP(3, R.layout.activity_hillfort_maps),
    ACCOUNT(4, R.layout.fragment_about),

}