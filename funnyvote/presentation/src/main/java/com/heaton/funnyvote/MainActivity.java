package com.heaton.funnyvote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.heaton.funnyvote.analytics.AnalyzticsTag;
import com.heaton.funnyvote.database.User;
import com.heaton.funnyvote.eventbus.EventBusManager;
import com.heaton.funnyvote.notification.VoteNotificationManager;
import com.heaton.funnyvote.ui.about.AboutFragment;
import com.heaton.funnyvote.ui.account.AccountFragment;
import com.heaton.funnyvote.ui.createvote.CreateVoteActivity;
import com.heaton.funnyvote.ui.main.MainPageFragment;
import com.heaton.funnyvote.ui.main.MainPageTabFragment;
import com.heaton.funnyvote.ui.personal.UserActivity;
import com.heaton.funnyvote.ui.search.SearchFragment;
import com.jakewharton.rxbinding2.support.design.widget.RxNavigationView;
import com.jakewharton.rxbinding2.support.v4.widget.RxDrawerLayout;
import com.jakewharton.rxbinding2.view.RxView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends MvpActivity<MainPageView, MainPagePresenter> implements MainPageView {
    private static final String TAG = MainPageTabFragment.class.getSimpleName();
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int SWITCH_PAGE_ANIMATION_DURATION = 400;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private int currentPage;
    private SearchView searchView;
    private String searchKeyword;
    private AdView adView;
    private Tracker tracker;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private long backPressedTimestamp;

    @Inject MainPagePresenter mainPagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((FunnyVoteApplication)getApplication())
                .getBaseComponent()
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        adView = (AdView) findViewById(R.id.adView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        FunnyVoteApplication application = (FunnyVoteApplication) getApplication();
        tracker = application.getDefaultTracker();

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        setupDrawer();
        setUpAdmob();
        setupHomeFragment();

        VoteNotificationManager.getInstance(getApplicationContext()).startNotificationAlarm();
    }

    private void setupHomeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content
                , new MainPageFragment()).commit();
        setupToolBar(R.string.drawer_home, R.color.primary);
        currentPage = R.id.navigation_item_main;
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        getPresenter().refreshUserProfile();
        Disposable drawerOpen = RxDrawerLayout.drawerOpen(drawerLayout, GravityCompat.START)
                .filter(open -> open == true)
                .subscribe(open -> getPresenter().refreshUserProfile());

        Disposable menuItemSelection = RxNavigationView.itemSelections(navigationView)
                .subscribe(menuItem -> {
                    if (currentPage != menuItem.getItemId()) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_item_main:
                                getPresenter().gotoHomePage();
                                break;
                            case R.id.navigation_item_create_vote:
                                getPresenter().gotoCreateVotePage();
                                break;
                            case R.id.navigation_item_list_my_box:
                                getPresenter().gotoMyBoxPage();
                                break;
                            case R.id.navigation_item_search:
                                getPresenter().gotoSearchPage(searchKeyword);
                                break;
                            case R.id.navigation_item_account:
                                getPresenter().gotoAccountPage();
                                break;
                            case R.id.navigation_item_about:
                                getPresenter().gotoAboutPage();
                                break;
                            default:

                        }
                    }
                    drawerLayout.closeDrawers();
                });

        Disposable userProfileClick = RxView.clicks(navigationView.getHeaderView(0))
                .subscribe(view -> {
                    getPresenter().gotoAccountPage();
                    drawerLayout.closeDrawers();
                });

        compositeDisposable.add(drawerOpen);
        compositeDisposable.add(menuItemSelection);
        compositeDisposable.add(userProfileClick);
    }

    private void setUpAdmob() {
        boolean enableAdmob = getResources().getBoolean(R.bool.enable_main_admob);
        if (enableAdmob) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUIChange(EventBusManager.UIControlEvent event) {
        if (event.message.equals(EventBusManager.UIControlEvent.INTRO_TO_ACCOUNT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    @Override
    public MainPagePresenter createPresenter() {
        return mainPagePresenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(currentPage);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (currentPage != R.id.navigation_item_main) {
            getPresenter().gotoHomePage();
        } else {
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp - backPressedTimestamp < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, R.string.wall_item_toast_double_click_to_exit, Toast.LENGTH_SHORT).show();
                backPressedTimestamp = currentTimestamp;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.vote_detail_menu_search_hint));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(queryListener);
        }
        return true;
    }

    final private SearchView.OnQueryTextListener queryListener =
            new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchKeyword = newText;
                    if (searchKeyword.length() == 0) {
                        if (currentPage == R.id.navigation_item_search) {
                            EventBus.getDefault().post(new EventBusManager.UIControlEvent(
                                    EventBusManager.UIControlEvent.SEARCH_KEYWORD, ""));
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchKeyword = query;
                    Log.d(TAG, "onQueryTextSubmit:" + query + "  page:" + currentPage
                            + " search page:" + R.id.navigation_item_search);
                    if (currentPage != R.id.navigation_item_search) {
                        getPresenter().gotoSearchPage(searchKeyword);
                    } else {
                        EventBus.getDefault().post(new EventBusManager.UIControlEvent(
                                EventBusManager.UIControlEvent.SEARCH_KEYWORD, searchKeyword));
                    }
                    return false;
                }
            };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            getPresenter().gotoCreateVotePage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_content);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showHomePage() {
        Fragment fragment = new MainPageFragment();
        replaceFragmentWithAnimation(fragment, R.string.drawer_home, R.color.color_primary);

        trackScreenView(AnalyzticsTag.SCREEN_MAIN);
        navigationView.getMenu().findItem(R.id.navigation_item_main).setChecked(true);
        currentPage = R.id.navigation_item_main;
    }

    @Override
    public void showMyBoxPage() {
        startActivity(new Intent(MainActivity.this, UserActivity.class));
    }

    @Override
    public void showAccountPage() {
        Fragment accountFragment = new AccountFragment();
        replaceFragmentWithAnimation(accountFragment, R.string.drawer_account,
                R.color.md_light_blue_100);

        trackScreenView(AnalyzticsTag.SCREEN_ACCOUNT);
        navigationView.getMenu().findItem(R.id.navigation_item_account).setChecked(true);
        currentPage = R.id.navigation_item_account;
    }

    @Override
    public void showAboutPage() {
        Fragment aboutFragment = new AboutFragment();
        replaceFragmentWithAnimation(aboutFragment,
                R.string.drawer_about, R.color.color_primary);

        trackScreenView(AnalyzticsTag.SCREEN_ABOUT);
        navigationView.getMenu().findItem(R.id.navigation_item_about).setChecked(true);
        currentPage = R.id.navigation_item_about;
    }

    @Override
    public void showSearchPage(final String searchKeyword) {
        Fragment fragment = new SearchFragment();
        Bundle argument = new Bundle();
        argument.putString(SearchFragment.KEY_SEARCH_KEYWORD, searchKeyword);
        fragment.setArguments(argument);
        replaceFragmentWithAnimation(fragment, R.string.drawer_search, R.color.color_primary);

        trackScreenView(AnalyzticsTag.SCREEN_SEARCH);
        navigationView.getMenu().findItem(R.id.navigation_item_search).setChecked(true);
        currentPage = R.id.navigation_item_search;
    }

    @Override
    public void showCreateVotePage() {
        startActivity(new Intent(MainActivity.this, CreateVoteActivity.class));
    }

    @Override
    public void updateUserProfile(User user) {
        View header = navigationView.getHeaderView(0);
        CircleImageView icon = (CircleImageView) header.findViewById(R.id.imgUserIcon);
        TextView name = (TextView) header.findViewById(R.id.txtUserName);
        name.setText(user.getUserName());
        Glide.with(MainActivity.this).load(user.getUserIcon()).dontAnimate()
                .override((int) getResources().getDimension(R.dimen.drawer_image_width)
                        , (int) getResources().getDimension(R.dimen.drawer_image_high))
                .placeholder(R.drawable.ic_action_account_circle).into(icon);
    }

    private void trackScreenView(String screen) {
        tracker.setScreenName(screen);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupToolBar(@StringRes int title, @ColorRes int backgroundColor) {
        int color = ContextCompat.getColor(getApplicationContext(), backgroundColor);
        toolbar.setBackgroundColor(color);
        toolbar.setTitle(title);
    }

    private void replaceFragmentWithAnimation(final Fragment fragment, @StringRes final int title,
                                              @ColorRes final int toolBarBgColor) {
        Disposable disposable = RxDrawerLayout.drawerOpen(drawerLayout, GravityCompat.START)
                .filter(open -> open == false)
                .take(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(open -> {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Slide slide = new Slide();
                    slide.setDuration(SWITCH_PAGE_ANIMATION_DURATION);
                    slide.setSlideEdge(Gravity.RIGHT);
                    fragment.setEnterTransition(slide);
                    transaction.replace(R.id.frame_content, fragment).commit();
                    setupToolBar(title, toolBarBgColor);
                }, throwable -> {});
        compositeDisposable.add(disposable);
    }
}
