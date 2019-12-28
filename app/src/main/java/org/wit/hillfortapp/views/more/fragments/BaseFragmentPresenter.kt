package org.wit.hillfortapp.views.more.fragments

import org.wit.hillfortapp.MainApp

open class BaseFragmentPresenter(fragment: BaseFragment) {
    open var app: MainApp = fragment.activity?.application as MainApp
}