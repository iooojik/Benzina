package com.kirovcompany.benzina;

import android.content.Context;
import android.util.Log;

public class Application extends android.app.Application {

    private static Application applicationInstance;

    public static synchronized Application getInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;

    }

    public void initAppLanguage(Context context) {
        LocaleUtils.initialize(context, LocaleUtils.getSelectedLanguageId());
    }

}
