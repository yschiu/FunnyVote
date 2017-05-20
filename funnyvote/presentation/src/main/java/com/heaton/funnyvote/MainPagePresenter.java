package com.heaton.funnyvote;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

public class MainPagePresenter extends MvpBasePresenter<MainPageView> {

    public void refreshUserProfile() {

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
