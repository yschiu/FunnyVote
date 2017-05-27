package com.heaton.funnyvote.data.user.di;

import android.app.Application;

import com.heaton.funnyvote.data.user.UserManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserManagerModule {
    @Singleton
    @Provides
    public UserManager provideUserManager(Application application) {
        return UserManager.getInstance(application);
    }
}
