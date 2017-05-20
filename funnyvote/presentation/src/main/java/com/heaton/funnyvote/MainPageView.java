package com.heaton.funnyvote;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface MainPageView extends MvpView{
    void showHomePage();
    void showMyBoxPage();
    void showAccountPage();
    void showAboutPage();
    void showSearchPage(String search);
    void showCreateVotePage();
    void updateUserProfile();
}
