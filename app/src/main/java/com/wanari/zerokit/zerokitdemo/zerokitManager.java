package com.wanari.zerokit.zerokitdemo;

import com.tresorit.adminapi.AdminApi;
import com.tresorit.zerokit.Zerokit;

import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.Properties;

import static com.tresorit.zerokit.Zerokit.API_ROOT;

public class ZerokitManager {

    private static ZerokitManager instance;

    private final Zerokit mZerokit;

    private final AdminApi mAdminApi;

    private ZerokitManager() {
        try {
            Properties properties = new Properties();
            properties.load(ZerokitApplication.getInstance().getAssets().open("zerokit.properties"));
            mAdminApi = new AdminApi(properties.getProperty("adminuserid", ""), properties.getProperty("adminkey", ""),
                    ZerokitApplication.getInstance().getPackageManager().getApplicationInfo(ZerokitApplication.getInstance().getPackageName(),
                            PackageManager.GET_META_DATA).metaData.getString(API_ROOT));
        } catch (IOException e) {
            throw new RuntimeException("Invalid config file");
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("No ApiRoot definition found in the AndroidManifest.xml");
        }
        mZerokit = Zerokit.getInstance();
    }

    public static ZerokitManager getInstance() {
        if (instance == null) {
            instance = new ZerokitManager();
        }
        return instance;
    }

    public Zerokit getZerokit() {
        return mZerokit;
    }

    public AdminApi getAdminApi() {
        return mAdminApi;
    }
}
