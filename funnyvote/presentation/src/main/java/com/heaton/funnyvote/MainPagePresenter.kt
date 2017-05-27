package com.heaton.funnyvote

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.heaton.funnyvote.data.user.UserManager
import com.heaton.funnyvote.database.User

import javax.inject.Inject

class MainPagePresenter @Inject
constructor(val userManager: UserManager) : MvpBasePresenter<MainPageView>() {

    fun refreshUserProfile() {
        userManager.getUser(object : UserManager.GetUserCallback {
            override fun onResponse(user: User) {
                if (isViewAttached) {
                    view.updateUserProfile(user)
                }
            }

            override fun onFailure() {}
        }, false)
    }

    fun gotoHomePage() {
        if (isViewAttached) {
            view.showHomePage()
        }
    }

    fun gotoAccountPage() {
        if (isViewAttached) {
            view.showAccountPage()
        }
    }

    fun gotoMyBoxPage() {
        if (isViewAttached) {
            view.showMyBoxPage()
        }
    }

    fun gotoCreateVotePage() {
        if (isViewAttached) {
            view.showCreateVotePage()
        }
    }

    fun gotoSearchPage(searchText: String) {
        if (isViewAttached) {
            view.showSearchPage(searchText)
        }
    }

    fun gotoAboutPage() {
        if (isViewAttached) {
            view.showAboutPage()
        }
    }
}
