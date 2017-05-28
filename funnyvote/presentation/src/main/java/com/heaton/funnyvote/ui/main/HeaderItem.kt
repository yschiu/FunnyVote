package com.heaton.funnyvote.ui.main

/**
 * Created by chiu_mac on 2017/5/28.
 */
sealed class HeaderItem {
    class AdMobItem : HeaderItem()
    class PromoItem(val imageUrl: String, val actionUrl: String) : HeaderItem()
}