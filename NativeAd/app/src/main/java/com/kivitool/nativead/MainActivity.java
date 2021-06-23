package com.kivitool.nativead;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_ads_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                if (nativeAd != null){
                    nativeAd = unifiedNativeAd;
                }

                CardView cardView = findViewById(R.id.ad_container);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.layout_add_attribution, null);
                populateNativeAd(unifiedNativeAd, adView);
                cardView.removeAllViews();
                cardView.addView(adView);

            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener(){

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {

                Toast.makeText(MainActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();

                super.onAdFailedToLoad(loadAdError);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateNativeAd(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView){

        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body_text));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.star_rating));
        unifiedNativeAdView.setMediaView(unifiedNativeAdView.findViewById(R.id.media_view));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.add_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_icon));

        unifiedNativeAdView.getMediaView().setMediaContent(unifiedNativeAd.getMediaContent());
        ((TextView)unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());

        if (unifiedNativeAd.getBody() == null){

            unifiedNativeAdView.setVisibility(View.INVISIBLE);

        }else{

            ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
            unifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);

        }

        if (unifiedNativeAd.getAdvertiser() == null){

            unifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);

        }else{

            ((TextView)unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);

        }

        if (unifiedNativeAd.getStarRating() == null){

            unifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);

        }else{

            ((RatingBar)unifiedNativeAdView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
            unifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);

        }

        if (unifiedNativeAd.getIcon() == null){

            unifiedNativeAdView.getIconView().setVisibility(View.GONE);

        }else{

            ((ImageView)unifiedNativeAdView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);

        }

        if (unifiedNativeAd.getCallToAction() == null){

            unifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);

        }else{

            ((Button)unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);

        }

        unifiedNativeAdView.setNativeAd(unifiedNativeAd);

    }

    @Override
    protected void onDestroy() {

        if (nativeAd != null){

            nativeAd.destroy();

        }

        super.onDestroy();
    }
}