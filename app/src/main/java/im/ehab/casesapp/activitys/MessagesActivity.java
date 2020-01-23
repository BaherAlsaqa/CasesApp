package im.ehab.casesapp.activitys;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import im.ehab.casesapp.adapters.PaginationMyMessageAdapter;
import im.ehab.casesapp.listeners.OnItemClickListener1;
import im.ehab.casesapp.listeners.PaginationScrollListener;
import im.ehab.casesapp.models.MessageBody;
import im.ehab.casesapp.models.Message;
import im.ehab.casesapp.models.MessageData;
import im.ehab.casesapp.retrofit.APIClient;
import im.ehab.casesapp.retrofit.APIInterface;
import im.ehab.casesapp.utils.AppSharedPreferences;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity {

    Call<MessageBody> call;
    APIInterface apiInterface;
    RecyclerView mRecyclerView;
    PaginationMyMessageAdapter adapter;
    ArrayList<Message> categoryArrayList;
    ArrayList<Message> categoryArrayListFavorite;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swiperefresh;
    AppSharedPreferences appSharedPreferences;
    AVLoadingIndicatorView progress;
    Toolbar toolbar;

    //pagination
    private static int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 2;
    private int currentPage = PAGE_START;
    private View v;
    private InterstitialAd mInterstitialAd;

    private int c_id;
    private String c_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();

        if (intent != null) {
            c_id = intent.getIntExtra(Contains.c_id, 1);

            Log.d(Contains.LOG + "11", c_id + "");

            //finds
            mRecyclerView = findViewById(R.id.recyclerview);
            swiperefresh = findViewById(R.id.swiperefresh);
            progress = findViewById(R.id.progress);

            ///////////////TODO pagination settings//////////////
            isLoading = false;
            isLastPage = false;
            currentPage = PAGE_START;
            ////////////////////////////

            swiperefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

            apiInterface = APIClient.getClient().create(APIInterface.class);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            //Initialize
            adapter = new PaginationMyMessageAdapter(this);
            categoryArrayList = new ArrayList<>();
            categoryArrayListFavorite = new ArrayList<>();
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            appSharedPreferences = new AppSharedPreferences(MessagesActivity.this);

            toolbar = findViewById(R.id.toolbar1);
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

            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(adapter);

            // Initialize Calss Mobile Ads
            MobileAds.initialize(this, getString(R.string.IDAPP));

            // Interstitial Ads
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.inter2));
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .build());

            adapter.setOnClickListener(new OnItemClickListener1() {
                @Override
                public void onItemClick(Message item) {
                    Toast.makeText(MessagesActivity.this, item.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
            ///////////
            //copy
            adapter.setOnClickListener3(new OnItemClickListener1() {
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
                        Toast.makeText(MessagesActivity.this, getString(R.string.copied_clipboard), Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(item.getId().toString(), item.getContent());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(MessagesActivity.this, getString(R.string.copied_clipboard), Toast.LENGTH_SHORT).show();

                    }

                    interAd();

                }
            });
            //share
            adapter.setOnClickListener4(new OnItemClickListener1() {
                @Override
                public void onItemClick(Message item) {

                    // Show Interstitial Ads
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        Log.d("log" + "131", "mInterstitialAd.show.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.facebook.orca");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
//                sendIntent.setPackage("com.facebook.orca");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    }

                    interAd();

                }
            });
            //email
            adapter.setOnClickListener5(new OnItemClickListener1() {
                @Override
                public void onItemClick(Message item) {

                    // Show Interstitial Ads
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        Log.d("log" + "131", "mInterstitialAd.show.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    }

                    interAd();
                }
            });
            //messenger
            adapter.setOnClickListener6(new OnItemClickListener1() {
                @Override
                public void onItemClick(Message item) {

                    // Show Interstitial Ads
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        Log.d("log" + "131", "mInterstitialAd.show.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.facebook.orca");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d("log" + "131", "The interstitial wasn't loaded yet.");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                item.getContent() + " " + "\n\n: " + "https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.facebook.orca");
                        try {
                            startActivity(sendIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MessagesActivity.this, getString(R.string.installmessenger), Toast.LENGTH_LONG).show();
                        }

                    }

                    interAd();

                }
            });

            //view progress
            progress.setVisibility(View.VISIBLE);

            loadFirstPage(c_id);

            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadFirstPage(c_id);
                }
            });

            mRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
                    Log.d(Contains.LOG+"addOnScroll", "loadMoreItems");
                    isLoading = true;
                    currentPage += 1;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextPage(c_id);
                            Log.d(Contains.LOG+"addOnScroll", "addOnScrollListener");
                        }
                    }, 1000);
                }

                @Override
                public int getTotalPageCount() {
                    Log.d(Contains.LOG+"addOnScroll", "getTotalPageCount"+TOTAL_PAGES);
                    return TOTAL_PAGES;
                }

                @Override
                public boolean isLastPage() {
                    Log.d(Contains.LOG+"addOnScroll", "isLastPage"+isLastPage);
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    Log.d(Contains.LOG+"addOnScroll", "isLoading"+isLoading);
                    return isLoading;
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
                    Log.d("logads", "onAdLoaded");
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
                Toast.makeText(MessagesActivity.this, getString(R.string.problem), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d("log" + "132", "onAdClosed");
            }
        });

    }

    private ArrayList<Message> fetchResults(Response<MessageBody> response) {
        MessageBody messageBody = response.body();
        MessageData homeItemsObject = messageBody.getMessageData();
        return homeItemsObject.getMessageData();
    }

    public void loadFirstPage(int c_id) {
        ///////////////TODO pagination settings//////////////
        isLoading = false;
        isLastPage = false;
        currentPage = PAGE_START;
        ////////////////////////////
        callTopRatedMoviesApi(c_id).enqueue(new Callback<MessageBody>() {
            @Override
            public void onResponse(Call<MessageBody> call, Response<MessageBody> response) {

                MessageBody resource = response.body();

                String status = response.body().getStatus().getMessage();
                String code = response.code() + "";

                Log.d(Contains.LOG + "MessageBody", "Status = " + status + " | Code = " + code);

                if (status.equals(Contains.message)) {
                    progress.setVisibility(View.INVISIBLE);
                    swiperefresh.setRefreshing(false);
                    adapter.clear();
                    categoryArrayList = resource.getMessageData().getMessageData();
                    TOTAL_PAGES = resource.getMessageData().getLastPage();
                    if (resource.getMessageData().getMessageData().size() > 0) {
                        c_name = resource.getMessageData().getMessageData().get(0).getCategory().getName();
                    }
                    ((TextView) toolbar.findViewById(R.id.toolbartitle)).setText(c_name);

                    Log.d(Contains.LOG + "1", categoryArrayList.size() + "");
                    adapter.addAll(categoryArrayList);

                    if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;

                }


            }

            @Override
            public void onFailure(Call<MessageBody> call, Throwable t) {
                Log.d(Contains.LOG + "2", t.getMessage());
                call.cancel();
            }
        });
    }

    public void loadNextPage(int c_id) {

        Log.d(Contains.LOG + "loadNextPage", "loadNextPage: " + currentPage);

        callTopRatedMoviesApi(c_id).enqueue(new Callback<MessageBody>() {
            @Override
            public void onResponse(Call<MessageBody> call, Response<MessageBody> response) {

                MessageBody resource = response.body();

                String status = resource.getStatus().getMessage();
                String code = response.code() + "";

                Log.d(Contains.LOG + "loadNextPage", "Status = " + status + " | Code = " + code);

                if (status.equals(Contains.message)) {
                    Log.d(Contains.LOG + "loadNextPage", "status.equals(Contains.message)");
                    categoryArrayList = fetchResults(response);

                    if (isLoading == false) {
                        Log.d(Contains.LOG + "loadNextPage", "isLoading == false");
                    } else {
                        Log.d(Contains.LOG + "loadNextPage", "isLoading == true");
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
                            Toast.makeText(MessagesActivity.this, getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageBody> call, Throwable t) {
                t.printStackTrace();
                try {
                    if (getApplicationContext() != null) {
//                        Toast.makeText(getContext(), getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MessagesActivity.this, getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Call<MessageBody> callTopRatedMoviesApi(int c_id) {
        return call = apiInterface.getCategoryMessages(c_id, currentPage);
    }
}
