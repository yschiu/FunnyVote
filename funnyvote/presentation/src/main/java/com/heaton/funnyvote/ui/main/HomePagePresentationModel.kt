package com.heaton.funnyvote.ui.main

import com.heaton.funnyvote.database.User

/**
 * Created by chiu_mac on 2017/5/28.
 */
class HomePagePresentationModel {
    lateinit var user: User
    val promotions: MutableList<HeaderItem> = ArrayList()
}