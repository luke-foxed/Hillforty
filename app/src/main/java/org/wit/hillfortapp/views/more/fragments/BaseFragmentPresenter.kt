package org.wit.hillfortapp.views.more.fragments

import android.content.Intent
import org.wit.hillfortapp.MainApp

open class BaseFragmentPresenter(fragment: BaseFragment) {
    open var app: MainApp = fragment.activity?.application as MainApp

    open fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    }

    open fun doRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResult: IntArray
    ) {

    }
}