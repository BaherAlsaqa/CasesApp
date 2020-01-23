package im.ehab.casesapp.activitys;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.ehab.casesapp.Contains;
import im.ehab.casesapp.R;
import im.ehab.casesapp.adapters.FavoritsAdapter;
import im.ehab.casesapp.listeners.OnItemClickListener1;
import im.ehab.casesapp.models.Message;
import im.ehab.casesapp.utils.AppSharedPreferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class FavoritsActivity extends AppCompatActivity {

    FavoritsAdapter favoritsAdapter;
    AppSharedPreferences appSharedPreferences;
    ArrayList<Message> messageArrayList;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorits);

        //finds
        recyclerView = findViewById(R.id.recyclerview);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        int color = Color.parseColor("#033C32");
        toolbar.setTitleTextColor(color);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
        ((TextView) toolbar.findViewById(R.id.toolbartitle)).setText(R.string.favorite);

        //Initialize
        appSharedPreferences = new AppSharedPreferences(FavoritsActivity.this);
        messageArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageArrayList = appSharedPreferences.readArray(Contains.arraySave);
        Log.d(Contains.LOG+"fav", "messageArrayList = "+messageArrayList.size()+"");
        favoritsAdapter = new FavoritsAdapter(messageArrayList, getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoritsAdapter);
        favoritsAdapter.notifyDataSetChanged();

        // Initialize Calss Mobile Ads
        MobileAds.initialize(this, getString(R.string.IDAPP));

        // Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter2));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());

        favoritsAdapter.setOnClickListener(new OnItemClickListener1() {
            @Override
            public void onItemClick(Message item) {
                Toast.makeText(FavoritsActivity.this, item.getContent(), Toast.LENGTH_SHORT).show();
            }
        });
        ///////////
        //copy
        favoritsAdapter.setOnClickListener3(new OnItemClickListener1() {
            @Override
            public void onItemClick(Message item) {

                // Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");

                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(item.getId().toString(), item.getContent());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(FavoritsActivity.this, getString(R.string.copied_clipboard), Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(item.getId().toString(), item.getContent());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(FavoritsActivity.this, getString(R.string.copied_clipboard), Toast.LENGTH_SHORT).show();

                }

                interAd();

            }
        });
        //share
        favoritsAdapter.setOnClickListener4(new OnItemClickListener1() {
            @Override
            public void onItemClick(Message item) {

                // Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                }

                interAd();

            }
        });
        //email
        favoritsAdapter.setOnClickListener5(new OnItemClickListener1() {
            @Override
            public void onItemClick(Message item) {

                // Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                }

                interAd();

            }
        });
        //messenger
        favoritsAdapter.setOnClickListener6(new OnItemClickListener1() {
            @Override
            public void onItemClick(Message item) {

                // Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FavoritsActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }

                }

                interAd();

            }
        });

        // View Ads
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.messageBanner));
        adView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d( "logads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("logfailed", "onAdFailedToLoad = " + i);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d("logonadclosed", "onAdClosed");
            }
        });

    }

    private void interAd() {

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("log" + "132", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("log" + "132", "onAdFailedToLoad = " + i);
                Toast.makeText(FavoritsActivity.this, getString(R.string.problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d("log" + "132", "onAdClosed");
            }
        });

    }
}
