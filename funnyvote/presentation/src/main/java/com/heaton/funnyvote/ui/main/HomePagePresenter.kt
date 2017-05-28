package com.heaton.funnyvote.ui.main

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.heaton.funnyvote.FirstTimePref
import com.heaton.funnyvote.data.promotion.PromotionManager
import com.heaton.funnyvote.data.user.UserManager
import com.heaton.funnyvote.database.Promotion
import com.heaton.funnyvote.database.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by chiu_mac on 2017/5/28.
 */
class HomePagePresenter(val userManager: UserManager, val firstTimePref: FirstTimePref,
                        val promotionManager: PromotionManager) : MvpBasePresenter<HomePageView>() {

    fun load(pullToRefresh: Boolean) {
        view?.showLoading(pullToRefresh)

        val preference = firstTimePref.preferences
        if (preference.getBoolean(FirstTimePref.SP_FIRST_INTRODUTCION_QUICK_POLL, true)) {
            preference.edit().putBoolean(FirstTimePref.SP_FIRST_INTRODUTCION_QUICK_POLL, false).apply()
            view?.showIntroDialog()
        }

        userManager.getUser(object: UserManager.GetUserCallback {
            override fun onResponse(user: User?) {
                promotionManager.getPromotions(user!!)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.map({transform(it, user!!)})
                        ?.subscribe({model ->
                            view?.setData(model)
                            view?.showContent()
                        })
            }

            override fun onFailure() {
                view?.showError(UnknownError(), pullToRefresh)
            }

        }, true)
    }

    private fun transform(promotions: List<Promotion>, user: User) : HomePagePresentationModel {
        val model = HomePagePresentationModel()
        model.user = user
        promotions.forEach {
            val promo: HeaderItem.PromoItem = HeaderItem.PromoItem(it.imageURL, it.actionURL)
            model.promotions.add(promo)
        }
        for (i in 0..promotions.size step 4) {
            model.promotions.add(i, HeaderItem.AdMobItem())
        }
        return model
    }
}