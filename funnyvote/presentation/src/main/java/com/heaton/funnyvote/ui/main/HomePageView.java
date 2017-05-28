package com.heaton.funnyvote.ui.main;

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView;

public interface HomePageView extends MvpLceView<HomePagePresentationModel>{
    void showIntroDialog();
}
