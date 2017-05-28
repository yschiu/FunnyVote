package com.heaton.funnyvote.ui.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment;
import com.heaton.funnyvote.FirstTimePref;
import com.heaton.funnyvote.FunnyVoteApplication;
import com.heaton.funnyvote.R;
import com.heaton.funnyvote.Util;
import com.heaton.funnyvote.analytics.AnalyzticsTag;
import com.heaton.funnyvote.data.promotion.PromotionManager;
import com.heaton.funnyvote.data.user.UserManager;
import com.heaton.funnyvote.database.User;
import com.heaton.funnyvote.database.VoteData;
import com.heaton.funnyvote.eventbus.EventBusManager;
import com.heaton.funnyvote.ui.CirclePageIndicator;
import com.jakewharton.rxbinding2.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by heaton on 16/4/1.
 */
public class MainPageFragment extends MvpLceFragment<ViewPager, HomePagePresentationModel,
        HomePageView, HomePagePresenter> implements HomePageView {

    private static String TAG = MainPageFragment.class.getSimpleName();
    public static boolean ENABLE_PROMOTION_ADMOB = true;

    private AutoScrollViewPager vpHeader;
    private AppBarLayout appBarMain;
    private View promotionADMOB;
    private TabsAdapter tabsAdapter;
    private ViewPager vpMainPage;
    private CircleProgressView circleLoad;

    private Tracker tracker;

    private HomePagePresentationModel model;

    final private CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    UserManager userManager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData(false);

        vpHeader.startAutoScroll();

        setupTracker();

        appBarMain.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    vpHeader.startAutoScroll();
                } else if (state == State.COLLAPSED) {
                    vpHeader.stopAutoScroll();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page_top, null);

        FunnyVoteApplication application = (FunnyVoteApplication) getActivity().getApplication();

        application.getBaseComponent().inject(this);

        circleLoad = (CircleProgressView) view.findViewById(R.id.loadingView);
        circleLoad.setTextMode(TextMode.TEXT);
        circleLoad.setShowTextWhileSpinning(true);
        circleLoad.setFillCircleColor(
                ContextCompat.getColor(getActivity().getApplication(), R.color.md_amber_50));
        circleLoad.setText(getString(R.string.vote_detail_circle_loading));

        vpHeader = (AutoScrollViewPager) view.findViewById(R.id.vpHeader);
        vpHeader.setAdapter(new HeaderAdapter());
        vpHeader.setCurrentItem(0);

        appBarMain = (AppBarLayout) view.findViewById(R.id.appBarMain);

        vpMainPage = (ViewPager) view.findViewById(R.id.contentView);

        TabLayout tabMainPage = (TabLayout) view.findViewById(R.id.tabLayoutMainPage);
        tabMainPage.setupWithViewPager(vpMainPage);

        CirclePageIndicator titleIndicator = (CirclePageIndicator) view.findViewById(R.id.vpIndicator);
        titleIndicator.setViewPager(vpHeader);
        vpHeader.setInterval(100000);
        vpHeader.setScrollDurationFactor(5);

        ENABLE_PROMOTION_ADMOB = getResources().getBoolean(R.bool.enable_promotion_admob);

        return view;
    }

    @Override
    public void setData(HomePagePresentationModel data) {
        model = data;
        setupTabs();
        vpHeader.getAdapter().notifyDataSetChanged();
        Log.d(TAG, "getUserCallback user:" + model.user.getType());
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        getPresenter().load(pullToRefresh);
    }

    @Override
    public HomePagePresenter createPresenter() {
        FirstTimePref pref = FirstTimePref.getInstance(getActivity().getApplication());
        return new HomePagePresenter(userManager, pref, PromotionManager.getInstance(getActivity().getApplication()));
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        showLoadingCircle();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        //super.showError(e, pullToRefresh);
        setupTabs();
        showContent();
        hideLoadingCircle();
        Log.d(TAG, "getUserCallback user failure:" + model.user);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return "Try again later!";
    }

    @Override
    public void onStop() {
        super.onStop();
        vpHeader.stopAutoScroll();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        vpHeader.startAutoScroll();
        EventBus.getDefault().register(this);
    }

    @Override
    public void showIntroDialog() {
        final Dialog introductionDialog = new Dialog(getActivity());
        introductionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        introductionDialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        introductionDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        introductionDialog.setCanceledOnTouchOutside(false);

        final VoteData data = new VoteData();
        data.setAuthorName(getString(R.string.intro_vote_item_author_name));
        data.setTitle(getString(R.string.intro_vote_item_title));
        data.setOption1Title(getString(R.string.intro_vote_item_option1));
        data.setOption2Title(getString(R.string.intro_vote_item_option2));
        data.setPollCount(30);
        data.setOption1Count(15);
        data.setOption2Count(15);
        data.setStartTime(System.currentTimeMillis() - 86400000);
        data.setEndTime(System.currentTimeMillis() + 864000000);

        View content = LayoutInflater.from(getActivity()).inflate(R.layout.card_view_wall_item_intro, null);
        TextView txtAuthorName = (TextView) content.findViewById(R.id.txtAuthorName);
        TextView txtTitle = (TextView) content.findViewById(R.id.txtTitle);
        TextView txtOption1 = (TextView) content.findViewById(R.id.txtFirstOptionTitle);
        TextView txtOption2 = (TextView) content.findViewById(R.id.txtSecondOptionTitle);
        TextView txtPubTime = (TextView) content.findViewById(R.id.txtPubTime);
        final TextView txtPollCount = (TextView) content.findViewById(R.id.txtBarPollCount);
        final TextView txtFirstPollCountPercent = (TextView) content.findViewById(R.id.txtFirstPollCountPercent);
        final TextView txtSecondPollCountPercent = (TextView) content.findViewById(R.id.txtSecondPollCountPercent);
        final RoundCornerProgressBar progressFirstOption = (RoundCornerProgressBar) content.findViewById(R.id.progressFirstOption);
        final RoundCornerProgressBar progressSecondOption = (RoundCornerProgressBar) content.findViewById(R.id.progressSecondOption);
        CardView btnThirdOption = (CardView) content.findViewById(R.id.btnThirdOption);
        final CardView btnSecondOption = (CardView) content.findViewById(R.id.btnSecondOption);
        final CardView btnFirstOption = (CardView) content.findViewById(R.id.btnFirstOption);
        final ImageView imgChampion1 = (ImageView) content.findViewById(R.id.imgChampion1);
        final ImageView imgChampion2 = (ImageView) content.findViewById(R.id.imgChampion2);

        ImageView imgAuthorIcon = (ImageView) content.findViewById(R.id.imgAuthorIcon);

        TextDrawable drawable = TextDrawable.builder().beginConfig().width(36).height(36).endConfig()
                .buildRound(data.getAuthorName().substring(0, 1), R.color.primary_light);
        imgAuthorIcon.setImageDrawable(drawable);

        btnFirstOption.setCardBackgroundColor(getResources().getColor(R.color.md_blue_100));
        btnSecondOption.setCardBackgroundColor(getResources().getColor(R.color.md_blue_100));
        btnThirdOption.setVisibility(View.GONE);

        txtFirstPollCountPercent.setVisibility(View.GONE);
        txtSecondPollCountPercent.setVisibility(View.GONE);

        progressFirstOption.setVisibility(View.GONE);
        progressSecondOption.setVisibility(View.GONE);

        imgChampion1.setVisibility(View.GONE);
        imgChampion2.setVisibility(View.GONE);

        txtAuthorName.setText(data.getAuthorName());
        txtTitle.setText(data.getTitle());
        txtOption1.setText(data.getOption1Title());
        txtOption2.setText(data.getOption2Title());
        txtPubTime.setText(Util.getDate(data.getStartTime(), "yyyy/MM/dd hh:mm")
                + " ~ " + Util.getDate(data.getEndTime(), "yyyy/MM/dd hh:mm"));
        txtPollCount.setText(Integer.toString(data.getPollCount()));
        progressFirstOption.setProgressColor(getResources().getColor(R.color.md_blue_600));
        progressFirstOption.setProgressBackgroundColor(getResources().getColor(R.color.md_blue_200));
        btnFirstOption.setCardBackgroundColor(getResources().getColor(R.color.md_blue_100));
        progressSecondOption.setProgressColor(getResources().getColor(R.color.md_blue_600));
        progressSecondOption.setProgressBackgroundColor(getResources().getColor(R.color.md_blue_200));
        btnSecondOption.setCardBackgroundColor(getResources().getColor(R.color.md_blue_100));

        View.OnLongClickListener dialogLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View optionButton) {
                if (optionButton.getId() == R.id.btnFirstOption) {
                    progressFirstOption.setProgressColor(getResources().getColor(R.color.md_red_600));
                    progressFirstOption.setProgressBackgroundColor(getResources().getColor(R.color.md_red_200));
                    btnFirstOption.setCardBackgroundColor(getResources().getColor(R.color.md_red_100));
                    imgChampion1.setVisibility(View.VISIBLE);
                    imgChampion2.setVisibility(View.INVISIBLE);
                    data.setOption1Count(data.getOption1Count() + 1);
                } else {
                    progressSecondOption.setProgressColor(getResources().getColor(R.color.md_red_600));
                    progressSecondOption.setProgressBackgroundColor(getResources().getColor(R.color.md_red_200));
                    btnSecondOption.setCardBackgroundColor(getResources().getColor(R.color.md_red_100));
                    imgChampion2.setVisibility(View.VISIBLE);
                    imgChampion1.setVisibility(View.INVISIBLE);
                    data.setOption2Count(data.getOption2Count() + 1);
                }

                progressFirstOption.setVisibility(View.VISIBLE);
                progressFirstOption.setProgress(data.getOption1Count());

                progressSecondOption.setVisibility(View.VISIBLE);
                progressSecondOption.setProgress(data.getOption2Count());

                txtFirstPollCountPercent.setVisibility(View.VISIBLE);
                txtSecondPollCountPercent.setVisibility(View.VISIBLE);
                data.setPollCount(data.getPollCount() + 1);
                progressFirstOption.setMax(data.getPollCount());
                progressSecondOption.setMax(data.getPollCount());
                txtPollCount.setText(Integer.toString(data.getPollCount()));

                double percent1 = data.getPollCount() == 0 ? 0
                        : (double) data.getOption1Count() / data.getPollCount() * 100;
                double percent2 = data.getPollCount() == 0 ? 0
                        : (double) data.getOption2Count() / data.getPollCount() * 100;
                txtFirstPollCountPercent.setText(String.format("%3.1f%%", percent1));
                txtSecondPollCountPercent.setText(String.format("%3.1f%%", percent2));
                Toast.makeText(getActivity(), R.string.toast_network_connect_success_poll
                        , Toast.LENGTH_SHORT).show();
                btnFirstOption.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        introductionDialog.dismiss();
                    }
                }, 3000);
                return false;
            }
        };
        introductionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventBus.getDefault().post(new EventBusManager.UIControlEvent(EventBusManager.UIControlEvent.INTRO_TO_ACCOUNT));
            }
        });
        btnFirstOption.setOnLongClickListener(dialogLongClick);
        btnSecondOption.setOnLongClickListener(dialogLongClick);

        introductionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        introductionDialog.setContentView(content, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        introductionDialog.setCancelable(false);
        introductionDialog.show();
    }

    @Override
    public void onDestroyView() {
        disposables.dispose();
        super.onDestroyView();
    }

    private void setupTracker() {
        FunnyVoteApplication application = (FunnyVoteApplication) getActivity().getApplication();
        tracker = application.getDefaultTracker();
        tracker.setScreenName(AnalyzticsTag.SCREEN_MAIN_HOT);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupTabs() {
        tabsAdapter = new TabsAdapter(getChildFragmentManager());
        int currentItem = vpMainPage.getCurrentItem();
        vpMainPage.setAdapter(tabsAdapter);
        vpMainPage.setCurrentItem(currentItem);

        Disposable disposable = RxViewPager.pageSelections(vpMainPage)
                .subscribe(position -> {
                    if (position == 0) {
                        tracker.setScreenName(AnalyzticsTag.SCREEN_MAIN_HOT);
                    } else if (position == 1) {
                        tracker.setScreenName(AnalyzticsTag.SCREEN_MAIN_NEW);
                    }
                    tracker.send(new HitBuilders.ScreenViewBuilder().build());
                });
        disposables.add(disposable);
    }

    private void showLoadingCircle() {
        circleLoad.setVisibility(View.VISIBLE);
        circleLoad.spin();
    }

    private void hideLoadingCircle() {
        circleLoad.stopSpinning();
        circleLoad.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUIChange(EventBusManager.UIControlEvent event) {
        if (event.message.equals(EventBusManager.UIControlEvent.SCROLL_TO_TOP)) {
            appBarMain.setExpanded(true);
        } else if (event.message.equals(EventBusManager.UIControlEvent.HIDE_CIRCLE)) {
            hideLoadingCircle();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChange(EventBusManager.NetworkEvent event) {
        if (event.message.equals(EventBusManager.NetworkEvent.RELOAD_USER)
                && (event.tab.equals(MainPageTabFragment.TAB_HOT)
                || event.tab.equals(MainPageTabFragment.TAB_NEW))) {
            loadData(false);
        }
    }

    private class HeaderAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            if (model.getPromotions().get(position) instanceof HeaderItem.PromoItem) {
                View headerItem = inflater.inflate(R.layout.item_promotion_funny_vote, null);
                HeaderItem.PromoItem item = (HeaderItem.PromoItem) model.getPromotions().get(position);
                ImageView promotion = (ImageView) headerItem.findViewById(R.id.headerImage);
                Glide.with(getContext())
                        .load(item.getImageUrl())
                        .override((int) getResources().getDimension(R.dimen.promotion_image_width)
                                , (int) getResources().getDimension(R.dimen.promotion_image_high))
                        .fitCenter()
                        .crossFade()
                        .into(promotion);
                final String actionURL = item.getActionUrl();
                promotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW
                                , Uri.parse(actionURL));
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory(AnalyzticsTag.CATEGORY_PROMOTION)
                                .setAction(AnalyzticsTag.ACTION_CLICK_PROMOTION)
                                .setLabel(actionURL)
                                .build());
                        startActivity(browserIntent);
                    }
                });
                container.addView(headerItem);
                return headerItem;
            } else if (model.getPromotions().get(position) instanceof HeaderItem.AdMobItem) {
                if (promotionADMOB == null) {
                    promotionADMOB = inflater.inflate(R.layout.item_promotion_admob, null);
                    NativeExpressAdView adview = (NativeExpressAdView) promotionADMOB.findViewById(R.id.adViewPromotion);
                    AdRequest adRequest = new AdRequest.Builder()
                            .setGender(model.user != null && User.GENDER_MALE.equals(model.getUser().getGender()) ?
                                    AdRequest.GENDER_MALE : AdRequest.GENDER_FEMALE)
                            .build();
                    adview.loadAd(adRequest);
                }
                container.addView(promotionADMOB);
                return promotionADMOB;
            } else {
                return null;
            }

        }

        @Override
        public int getCount() {
            if (model != null) {
                return model.getPromotions().size();
            } else {
                return 0;
            }
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            // todo: work around : NullPointerException in FragmentStatePagerAdapter
            //super.restoreState(state, loader);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return MainPageTabFragment.newInstance(MainPageTabFragment.TAB_HOT, model.user);
                case 1:
                    return MainPageTabFragment.newInstance(MainPageTabFragment.TAB_NEW, model.user);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getActivity().getString(R.string.main_page_tab_hot);
                case 1:
                    return getActivity().getString(R.string.main_page_tab_new);
            }
            return "";
        }

    }

    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        public enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }
}
