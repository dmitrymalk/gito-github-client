package com.dmitrymalkovich.android.githubanalytics.traffic;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class TrafficAdActivity extends TrafficActivity {

    @Override
    protected void onResume() {
        super.onResume();
        AdView adView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-6820123979073037~3030459702");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
