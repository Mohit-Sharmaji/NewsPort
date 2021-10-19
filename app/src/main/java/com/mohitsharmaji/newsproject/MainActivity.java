package com.mohitsharmaji.newsproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohitsharmaji.newsproject.Activities.GettingStarted;
import com.mohitsharmaji.newsproject.Activities.NewsDetailActivity;
import com.mohitsharmaji.newsproject.Activities.SettingsActivity;
import com.mohitsharmaji.newsproject.Adapter.NewsAdapter;
import com.mohitsharmaji.newsproject.Api.ApiClient;
import com.mohitsharmaji.newsproject.Api.ApiInterface;
import com.mohitsharmaji.newsproject.Models.Article;
import com.mohitsharmaji.newsproject.Models.News;
import com.mohitsharmaji.newsproject.Repository.NewsRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawerLayout;
    private LinearLayout optionsUnderToolbar;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbarMA;
    private BottomNavigationView navigationView;
    private AppBarLayout bottomAppBar,topAppBar;
    boolean isNavigationHide = false;
    private NestedScrollView nestedScrollView;
    public static ConstraintLayout progressBar_main;
    public TextView label_Text;
    // Error Layout
    public static RelativeLayout internet_error_layout;
    private static TextView errTitle,errMsg;
    private Button errRetryBtn;

    // Shared Preference
    SharedPreferences sharedPreferences;
    public static final String NAME_SHARED_PREFERENCE = "com.mohitsharmaji.newsproject";
    public static final String language_pref ="Language_PREF";
    public static final String sortBy_pref="SortBy_PREF";
    public static final String isDarkTheme_prefs ="isDarkTheme_PREF";
    public static final String isFirstRun ="isFirstRun_PREF";
    public static final String productKey_Prefs ="ProductKey_PREF";
    public String getLanguage_pref,getSortBy_pref;

    // API Related Declarations
    private NewsRepository repository;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    static SwipeRefreshLayout swipeRefreshLayout;

    // Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refFirebaseDatabase;
    private ArrayList<String> product_keys;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Components
        progressBar_main = findViewById(R.id.newsLoad_progress_bar);
        label_Text = findViewById(R.id.label_MA);

        internet_error_layout = findViewById(R.id.errorLayout);
        errTitle = findViewById(R.id.errorTitle);
        errMsg = findViewById(R.id.errorMessage);
        errRetryBtn = findViewById(R.id.btnRetry);

        sharedPreferences = getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_sharedPref = sharedPreferences.edit();


        // Methods to initialize components
        settingSharedPreferences();
        initInternetErrorLayout();
        initToolbar();
        initDrawer();
        initBottomAppBar();
        //initOptionSpinners();

        firebaseDatabase = FirebaseDatabase.getInstance();
        refFirebaseDatabase = firebaseDatabase.getReference("product_key");

        // ScrollView to call Animation for BottomAppBAr
        nestedScrollView = findViewById(R.id.nestedScrollViewMA);
        nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(scrollY>oldScrollY){
                animateNavigationBar(true);
            }else{
                animateNavigationBar(false);
            }
        });

        // API Related Stuff
        //Getting Saved Articles from data into LiveData
        repository = new NewsRepository(getApplication());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMV);
        swipeRefreshLayout.setOnRefreshListener(() -> getNewsFromApi("in",getLanguage_pref,"","",getSortBy_pref));
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Getting News From Server/Internet
        progressBar_main.setVisibility(View.VISIBLE);
        getNewsFromApi("in",getLanguage_pref,"","general",getSortBy_pref);
        repository.getListLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(final List<Article> articles) {
                if(articles == null || articles.size() == 0){
                    //Toast.makeText(MainActivity.this, "onChanged : Null Data {No Saved data found either !}", Toast.LENGTH_SHORT).show();
                }else{
                    internet_error_layout.setVisibility(View.GONE);
                    adapter = new NewsAdapter(articles,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    initOnClick(articles);
                }
            }
        });
    }

    private void initInternetErrorLayout() {
        errRetryBtn.setOnClickListener(view -> {
            progressBar_main.setVisibility(View.VISIBLE);
            getNewsFromApi("in",getLanguage_pref,"","general",getSortBy_pref);
          });
    }

    private void settingSharedPreferences() {
        //Setting Shared Preference
        sharedPreferences = getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_sharedPref = sharedPreferences.edit();
        // Setting Shared Prefs if there is no prefs found
        if(sharedPreferences.getBoolean(isFirstRun,true)){ // First App Run
            editor_sharedPref.putString(language_pref,"en");
            editor_sharedPref.putString(sortBy_pref,"relevancy");
            editor_sharedPref.putBoolean(isDarkTheme_prefs,false);
            editor_sharedPref.apply();
            startActivity(new Intent(MainActivity.this, GettingStarted.class));
            finish();
        }else{
            firebaseDatabase = FirebaseDatabase.getInstance();
            refFirebaseDatabase = firebaseDatabase.getReference("product_key");
            getLanguage_pref = sharedPreferences.getString(language_pref,"");
            getSortBy_pref = sharedPreferences.getString(sortBy_pref,"");
            refFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String product_key = sharedPreferences.getString(productKey_Prefs,"");
                    product_keys= new ArrayList<>();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        product_keys.add(ds.getKey());
                    }
                    if(!product_keys.contains(product_key)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Product Key has Expired !");
                        builder.setMessage("Possible Cause: We can only have a limited number of active user. \nPlease ask the developer for new Product Key.");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(MainActivity.this,GettingStarted.class));
                                finish();
                            }
                        });
                        try {
                            AlertDialog accountDeleteDialog = builder.create();
                            accountDeleteDialog.show();
                        }catch (Exception e){
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Check Your Internet!", Toast.LENGTH_SHORT).show();
                }
            });

        }
        // Setting Default Theme
        if(sharedPreferences.getBoolean(isDarkTheme_prefs,false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else if(!sharedPreferences.getBoolean(isDarkTheme_prefs, false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            Log.d("MainActivity Error","ELSE_Block");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void initOnClick(final List<Article> articles){
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img",  article.getUrlToImage());
                intent.putExtra("date",  article.getPublishedAt());
                intent.putExtra("source",  article.getSource().getName());
                intent.putExtra("author",  article.getAuthor());

                Pair<View, String> pair = Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,
                        pair
                );
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(intent, optionsCompat.toBundle());
                }else {
                    startActivity(intent);
                }
            }
        });
    }
    public static void SwipeRefreshStop(){
        progressBar_main.setVisibility(View.GONE);
        //internet_error_layout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onStart() {
        // Called when activity is becoming visible to the user.
        super.onStart();
    }

    @Override
    protected void onResume() { // Called when activity will start interacting with the user.
        super.onResume();
    }

    //Search View
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchViewToolbarMA).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //optionsUnderToolbar.setVisibility(View.VISIBLE);

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //optionsUnderToolbar.setVisibility(View.GONE);

                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getSupportActionBar().setTitle("Search Results: "+s);
                progressBar_main.setVisibility(View.VISIBLE);
                getNewsFromApi("in",getLanguage_pref,s,"general",getSortBy_pref);
                searchView.setIconified(true);
                searchView.setIconified(true);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // Methods Related to Component's Initialization & Implementation
    private void initToolbar() {
        topAppBar = findViewById(R.id.topAppbar);
        toolbarMA = findViewById(R.id.toolbarMA);
        setSupportActionBar(toolbarMA);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        optionsUnderToolbar = findViewById(R.id.optionsUnderToolbar);
        toolbarMA.setNavigationIcon(R.drawable.ic_menu);
        toolbarMA.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void initBottomAppBar() {
        bottomAppBar = findViewById(R.id.bottomAppBarLayout_MA);
        navigationView = findViewById(R.id.bottomNavigation_MA);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.homeBottomNav:{
                    item.setChecked(true);
                    getSupportActionBar().setTitle("NewsPort");
                    progressBar_main.setVisibility(View.VISIBLE);
                    getNewsFromApi("in",getLanguage_pref,"","general",getSortBy_pref);
                    if(adapter!=null)
                        adapter.notifyDataSetChanged();
                    break;
                }
                case R.id.businessBottomNav:{
                    item.setChecked(true);
                    getSupportActionBar().setTitle("Business News");
                    progressBar_main.setVisibility(View.VISIBLE);
                    getNewsFromApi("in",getLanguage_pref,"","business",getSortBy_pref);
                    break;
                }default:{
                    Toast.makeText(this, "Something went Wrong!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return false;
        });

    }
    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout_MA);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigationViewMA);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.entertainmentNav:
                        getSupportActionBar().setTitle("Entertainment News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","entertainment",getSortBy_pref);
                        break;

                    case R.id.businessNav:
                        getSupportActionBar().setTitle("Business News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","business",getSortBy_pref);
                        break;

                    case R.id.healthNav:
                        getSupportActionBar().setTitle("Health News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","health",getSortBy_pref);

                        break;

                    case R.id.scienceNav:
                        getSupportActionBar().setTitle("Science News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","science",getSortBy_pref);
                        break;

                    case R.id.sportsNav:
                        getSupportActionBar().setTitle("Sports News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","sports",getSortBy_pref);
                        break;

                    case R.id.technologyNav:
                        getSupportActionBar().setTitle("Technology News");
                        progressBar_main.setVisibility(View.VISIBLE);
                        getNewsFromApi("in",getLanguage_pref,"","technology",getSortBy_pref);
                        break;

                    case R.id.settings_menu_navMA:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.followUs_nav_MA:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.instagram.com/m0hit.sharmaji/"));
                        startActivity(intent);
                        break;
                }
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });
    }
    private void animateNavigationBar(boolean hide){
        if(isNavigationHide && hide || !isNavigationHide&& !hide) return;
        isNavigationHide = hide;
        int moveDownY = hide ?(2* bottomAppBar.getHeight()):0;
        //int moveUpY = hide ?(2 * toolbarMA.getHeight()):0;
        bottomAppBar.animate().translationY(moveDownY).setStartDelay(100).setDuration(400).start();
        //topAppBar.animate().translationY(-moveUpY).setStartDelay(100).setDuration(400).start();
    }
    // Retrieving data from NewsApi.org & inserting it using InsertNewsArticles() .
    public void getNewsFromApi(String country,String language,String keyword,String category,String sortBy){ // Keyword here represents search Keyword sent by user
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String API_KEY = "PASTE_YOUR_API_KEY_HERE";
        Call<News> call;
        if(keyword.length()>0){
            call=apiInterface.getNewsSearch(keyword,language,sortBy,API_KEY,50);
        }
        else {

                call=apiInterface.getNews(country,API_KEY,category,50);
                call.enqueue(new Callback<News>() {
                    @Override
                    public void onResponse(Call<News> call, Response<News> response) {
                        List<Article> articles = new ArrayList<>();
                        if(response.isSuccessful() && response.body().getArticles()!=null){
                            if(!articles.isEmpty()){
                                articles.clear();
                            }
                            repository.DeleteAllSavedArticles();
                            articles = response.body().getArticles();
                            // Inserting Articles into database
                            repository.InsertNewsArticles(articles);
                        }else{
                            //Check the response code from server here
                            switch (response.code()) {
                                case 429:
                                    showErrorLayout("Ah! Snap (Server Code: 429)","Issue (Low Budget): Daily Requests Quota to Server is Completed. Come back tomorrow there's nothing You & I can do. Thank You !",429);
                                    Log.d("ServerResponse","429 Too many requests to server !");
                                    break;
                                case 404:
                                    showErrorLayout("Ah! Snap (Server Code: 404)","Issue (Server Side Error): It should be fixed soon!",404);
                                    Log.d("ServerResponse","404 not found");
                                    break;
                                case 500:
                                    showErrorLayout("Ah! Snap (Server Code: 500)","Issue (Server Broken): I'm gonna go & buy a glue stick !",500);
                                    Log.d("ServerResponse","500 server broken");
                                    break;
                                default:
                                    showErrorLayout("Ah! Snap (Server Code: "+response.code()+")","Issue: I don't know! Go Solve your problem yourself .",response.code());
                                    Toast.makeText(MainActivity.this, "Come back tomorrow Now Shu Shuu ..."+response.code(), Toast.LENGTH_SHORT).show();
                                    Log.d("ServerResponse","Unknown error");
                                    break;
                            }
                        }
                        SwipeRefreshStop();
                    }

                    @Override
                    public void onFailure(Call<News> call, Throwable t) {
                        SwipeRefreshStop();
                        if(adapter!=null){
                            if (adapter.getItemCount() == 0){
                                internet_error_layout.setVisibility(View.VISIBLE);
                                errTitle.setText("Error: No Saved Data Found !");
                                errMsg.setText("Fix: Please! Check Your Internet Connection. ");
                            }else{
                                internet_error_layout.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Please! Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            internet_error_layout.setVisibility(View.VISIBLE);
                            errTitle.setText("Error: No Saved Data Found !");
                            errMsg.setText("Fix: Please! Check Your Internet Connection. ");
                        }
                        Log.d("NewsRepository","onResponse onFailure Executed !"+t.getMessage());
                    }
                });
        }
    }

    private void showErrorLayout(String title,String text,int code) {
        if(adapter!=null){
            if (adapter.getItemCount() == 0){
                internet_error_layout.setVisibility(View.VISIBLE);
                errTitle.setText(title);
                errMsg.setText(text);
            }else{
                internet_error_layout.setVisibility(View.GONE);
                switch(code){
                    case 429:
                        Toast.makeText(this, "429: Request Quota to server has completed. Please read the Saved News or Come Back Tomorrow!", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(this, "404: No Response From Server at this moment. Please read the Saved News.", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(this, "500: Server Broken! Let me get a glue stick. ", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, "Server Response Code: "+code+"Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            internet_error_layout.setVisibility(View.VISIBLE);
            errTitle.setText(title);
            errMsg.setText(text);
        }
    }
}