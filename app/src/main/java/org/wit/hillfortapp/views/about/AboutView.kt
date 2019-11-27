package org.wit.hillfortapp.views.about

import android.os.Bundle
import kotlinx.android.synthetic.main.drawer_main.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.R
import org.wit.hillfortapp.views.main.MainView

class AboutView : MainView(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content_frame.removeAllViews()
        layoutInflater.inflate(R.layout.activity_about, content_frame)
    }
} 