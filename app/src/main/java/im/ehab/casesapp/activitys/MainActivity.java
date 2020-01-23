package im.ehab.casesapp.activitys;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import im.ehab.casesapp.Contains;
import im.ehab.casesapp.CustomViews.CustomTypefaceSpan;
import im.ehab.casesapp.R;
import im.ehab.casesapp.adapters.PaginationMyCategoryAdapter;
import im.ehab.casesapp.listeners.OnItemClickListener;
import im.ehab.casesapp.listeners.PaginationScrollListener;
import im.ehab.casesapp.models.Category;
import im.ehab.casesapp.models.CategoryBody;
import im.ehab.casesapp.models.Data;
import im.ehab.casesapp.models.ErrorBody;
import im.ehab.casesapp.retrofit.APIClient;
import im.ehab.casesapp.retrofit.APIInterface;
import im.ehab.casesapp.utils.AppSharedPreferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Call<CategoryBody> call;
    APIInterface apiInterface;
    APIInterface apiInterface1;
    RecyclerView mRecyclerView;
    PaginationMyCategoryAdapter adapter;
    ArrayList<Category> categoryArrayList;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swiperefresh;
    private DrawerLayout drawerLayout;
    AppSharedPreferences appSharedPreferences;
    AVLoadingIndicatorView progress;
    private InterstitialAd mInterstitialAd;

    //pagination
    private static int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 2;
    private int currentPage = PAGE_START;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finds
        mRecyclerView = findViewById(R.id.recyclerview);
        swiperefresh = findViewById(R.id.swiperefresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        progress = findViewById(R.id.progress);

        ///////////////TODO pagination settings//////////////
        isLoading = false;
        isLastPage = false;
        currentPage = PAGE_START;
        ////////////////////////////

        swiperefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface1 = APIClient.getClient().create(APIInterface.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Initialize
        adapter = new PaginationMyCategoryAdapter(this);
        categoryArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        appSharedPreferences = new AppSharedPreferences(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int color = Color.parseColor("#FFFFFF");
        toolbar.setTitleTextColor(color);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_button);
        actionbar.setDisplayShowTitleEnabled(false);

        ((TextView) toolbar.findViewById(R.id.toolbartitle)).setText(R.string.app_name);

        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            applyFontToMenuItem(menuItem);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.favorits:
                                startActivity(new Intent(MainActivity.this, FavoritsActivity.class));
                                break;
                            case R.id.rate:
                                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                                }
                                break;
                            case R.id.share:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                                sendIntent.setType("text/plain");
                                try {
                                    startActivity(sendIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(MainActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                                }
                                break;
                            case R.id.logout:
                                finish();
                                break;
                        }
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        // Initialize Calss Mobile Ads
        MobileAds.initialize(this, getString(R.string.IDAPP));

        // Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter1));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());

        adapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Category item) {

                // Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");

                    startActivity(new Intent(MainActivity.this, MessagesActivity.class)
                            .putExtra(Contains.c_id, item.getId()));

                } else {
                    Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                    startActivity(new Intent(MainActivity.this, MessagesActivity.class)
                            .putExtra(Contains.c_id, item.getId()));

                }

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
                        Toast.makeText(MainActivity.this, getString(R.string.problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d("log" + "132", "onAdClosed");
                    }
                });

            }
        });

        //view progress
        progress.setVisibility(View.VISIBLE);

        loadFirstPage();

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstPage();
            }
        });

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                Log.d(Contains.LOG+"addOnScroll", "addOnScrollListener");

                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Contains.LOG+"addOnScroll", "addOnScrollListener");

                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(Contains.LOG + "token", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log
                        Log.d(Contains.LOG + "token", "token = " + token);
                        sendTokenToServer(token);
                    }
                });

        // View Ads
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.mainBanner));
        adView = findViewById(R.id.adView1);
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

    private ArrayList<Category> fetchResults(Response<CategoryBody> response) {
        CategoryBody CategoryBody = response.body();
        Data homeItemsObject = CategoryBody.getData();
        return homeItemsObject.getData();
    }

    public void loadFirstPage() {
        ///////////////TODO pagination settings//////////////
        isLoading = false;
        isLastPage = false;
        currentPage = PAGE_START;
        ////////////////////////////
        callTopRatedMoviesApi().enqueue(new Callback<CategoryBody>() {
            @Override
            public void onResponse(Call<CategoryBody> call, Response<CategoryBody> response) {

                CategoryBody resource = response.body();

                String status = response.body().getStatus().getMessage();
                String code = response.code() + "";

                Log.d(Contains.LOG + "CategoryBody", "Status = " + status + " | Code = " + code);

                if (status.equals(Contains.message)) {
                    progress.setVisibility(View.INVISIBLE);
                    swiperefresh.setRefreshing(false);
                    adapter.clear();
                    categoryArrayList = resource.getData().getData();
                    TOTAL_PAGES = resource.getData().getLastPage();
                    Log.d(Contains.LOG + "1", categoryArrayList.size() + "");
                    adapter.addAll(categoryArrayList);

                    if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;

                }


            }

            @Override
            public void onFailure(Call<CategoryBody> call, Throwable t) {
                Log.d(Contains.LOG + "2", t.getMessage());
                call.cancel();
            }
        });
    }

    public void loadNextPage() {

        Log.d(Contains.LOG + "loadNextPage", "loadNextPage: " + currentPage);

        callTopRatedMoviesApi().enqueue(new Callback<CategoryBody>() {
            @Override
            public void onResponse(Call<CategoryBody> call, Response<CategoryBody> response) {

                CategoryBody resource = response.body();

                String status = resource.getStatus().getMessage();
                String code = resource.getStatus().getCode().toString();

                Log.d(Contains.LOG + "CategoryBody", "Status = " + status + " | Code = " + code);

                if (status.equals(Contains.message)) {

                    categoryArrayList = fetchResults(response);

                    if (isLoading == false) {

                    } else {
                        adapter.removeLoadingFooter();
                        isLoading = false;
                        adapter.addAll(categoryArrayList);
                    }

                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                } else {
                    try {
                        Log.d("lasterror", "error");
                        if (getApplicationContext() != null) {
//                            Toast.makeText(getContext(), getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryBody> call, Throwable t) {
                t.printStackTrace();
                try {
                    if (getApplicationContext() != null) {
//                        Toast.makeText(getContext(), getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Call<CategoryBody> callTopRatedMoviesApi() {
        return call = apiInterface.getCategory(currentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.shareItem1:

                /*// Show Interstitial Ads
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.sharemessage) + " " + "\n\n: " + "https://play.google.com/store/apps/details?id=com.shammil.azkaar");
                    sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.facebook.orca");
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MainActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                    }
                } else {*/
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
                    Toast.makeText(MainActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                }
                /*}

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
                        Toast.makeText(MainActivity.this, getString(R.string.problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d("log" + "132", "onAdClosed");
                    }
                });*/

                break;
            case R.id.ratingItem2:
                // Show Interstitial Ads
                /*if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("log" + "131", "mInterstitialAd.show.");
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.shammil.azkaar")));
                    }
                } else {*/
                Log.d("log" + "131", "The interstitial wasn't loaded yet.");
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                }
                /*}

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
                        Toast.makeText(MainActivity.this, getString(R.string.problem), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d("log" + "132", "onAdClosed");
                    }
                });*/

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendTokenToServer(String token) {
        Call<ErrorBody> call = apiInterface1.deviceToken(token);
        call.enqueue(new Callback<ErrorBody>() {
            @Override
            public void onResponse(Call<ErrorBody> call, Response<ErrorBody> response) {

                Log.d(Contains.LOG + "ErrorBody", " | Code = " + response.body());
                if (response.isSuccessful()) {
                    ErrorBody resource = response.body();

                    String status = resource.getStatus().getMessage();
                    String code = response.code() + "";

                    Log.d(Contains.LOG + "ErrorBody", "Status = " + status + " | Code = " + code);

                    if (status.equals(Contains.message)) {

                        Log.d(Contains.LOG + "device_token", resource.getErrorData().getDeviceToken());

                    }
                } else {
                    Toast.makeText(MainActivity.this, "device token error", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ErrorBody> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font;

        font = Typeface.createFromAsset(getAssets(), "fonts/Aljazeera.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

}
