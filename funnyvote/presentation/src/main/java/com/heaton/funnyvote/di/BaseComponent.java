package com.heaton.funnyvote.di;

import com.heaton.funnyvote.MainActivity;
import com.heaton.funnyvote.data.user.di.UserManagerModule;
import com.heaton.funnyvote.ui.main.MainPageFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        UserManagerModule.class
})
public interface BaseComponent {
    void inject(MainActivity mainActivity);
    void inject(MainPageFragment mainPageFragment);
}
