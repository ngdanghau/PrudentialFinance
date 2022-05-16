package com.example.prudentialfinance.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class LanguageManager {
    private Context ctx;
    private String appName;
    private String langCode;
    private String shortCode;
    private SharedPreferences sharedPreferences;

    public LanguageManager(Context ctx, String appName){
        this.ctx = ctx;
        this.appName = appName;
        this.sharedPreferences = ctx.getSharedPreferences(this.appName, Context.MODE_PRIVATE);
    }

    public void updateResource(){
        Locale locale = new Locale(shortCode);
        locale.setDefault(locale);

        Resources resources = ctx.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public void setLang(String langCode){
        this.langCode = langCode;
        String[] langArr = langCode.split("-");

        this.shortCode = langArr[0];

        sharedPreferences.edit().putString("locale", langCode).apply();
    }

    public String getCurrent(){
        String locale = sharedPreferences.getString("locale", null);
        this.langCode = locale == null ? "en_US" : locale;
        setLang(this.langCode);
        return this.langCode;
    }

    public ArrayList<String> getList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("en-US");
        list.add("vi-VN");

        return list;
    }
}
