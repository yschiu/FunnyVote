package com.heaton.funnyvote;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.heaton.funnyvote.data.user.UserManager;
import com.heaton.funnyvote.database.User;

import javax.inject.Inject;

public class MainPagePresenter extends MvpBasePresenter<MainPageView> {

    UserManager userManager;

    @Inject
    public MainPagePresenter(UserManager userManager) {
        this.userManager = userManager;
    }

    public void refreshUserProfile() {
        userManager.getUser(new UserManager.GetUserCallback() {
            @Override
            public void onResponse(User user) {
                if (isViewAttached()) {
                    getView().updateUserProfile(user);
                }
            }

            @Override
            public void onFailure() {
            }
        }, false);
    }

    public void gotoHomePage() {
        if (isViewAttached()) {
            getView().showHomePage();
        }
    }

    public void gotoAccountPage() {
        if (isViewAttached()) {
            getView().showAccountPage();
        }
    }

    public void gotoMyBoxPage() {
        if (isViewAttached()) {
            getView().showMyBoxPage();
        }
    }

    public void gotoCreateVotePage() {
        if (isViewAttached()) {
            getView().showCreateVotePage();
        }
    }

    public void gotoSearchPage(String searchText) {
        if (isViewAttached()) {
            getView().showSearchPage(searchText);
        }
    }

    public void gotoAboutPage() {
        if (isViewAttached()) {
            getView().showAboutPage();
        }
    }
}
