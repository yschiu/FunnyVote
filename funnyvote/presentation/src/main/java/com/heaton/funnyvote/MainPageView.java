package com.heaton.funnyvote;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.heaton.funnyvote.database.User;

public interface MainPageView extends MvpView{
    void showHomePage();
    void showMyBoxPage();
    void showAccountPage();
    void showAboutPage();
    void showSearchPage(String search);
    void showCreateVotePage();
    void updateUserProfile(User user);
}
