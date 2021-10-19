package com.mohitsharmaji.newsproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohitsharmaji.newsproject.MainActivity;
import com.mohitsharmaji.newsproject.R;
import java.util.HashMap;
import java.util.Objects;
import static com.mohitsharmaji.newsproject.MainActivity.NAME_SHARED_PREFERENCE;
import static com.mohitsharmaji.newsproject.MainActivity.isFirstRun;
import static com.mohitsharmaji.newsproject.MainActivity.productKey_Prefs;

public class GettingStarted extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    TextView[] dots;
    int[] layoutSlides;
    EditText getProductKey;
    ImageView clearProductKey;
    Button letsGo;
    TextView keyAuthError,contactDeveloper ;
    ConstraintLayout progressbar;
    RelativeLayout showProductKeyWidget;
    ClipboardManager clipboard;

    //Shared Prefs
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor_sharedPref ;

    // Firebase Related Declarations
    FirebaseDatabase database;
    DatabaseReference refFirebaseDatabase;
    private HashMap<String,String> product_keys;

    @Override
    protected void onResume() {
        super.onResume();
       // InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //imm.hideSoftInputFromWindow(getProductKey.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstarted);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager = findViewById(R.id.viewPager_GettingStarted);
        dotsLayout = findViewById(R.id.layoutDots);
        clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clearProductKey = findViewById(R.id.clearProductKey);
        clearProductKey.setOnClickListener(view -> getProductKey.setText(""));
        getProductKey = findViewById(R.id.productKey);
        getProductKey. setShowSoftInputOnFocus(false);
        getProductKey.setOnClickListener(view -> {
            getProductKey.setText("");
            ClipData clipData = clipboard.getPrimaryClip();
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            getProductKey.setText(text);
        });
        progressbar = findViewById(R.id.progress_bar_slide3);
        keyAuthError = findViewById(R.id.keyAuthError);
        showProductKeyWidget = findViewById(R.id.showWidgets_productKey);
        layoutSlides = new int[]{R.layout.slide1,R.layout.slide2,R.layout.slide3};

        addBottomDots(0);
        AdapterViewPager adapter = new AdapterViewPager();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position==2){
                    showProductKeyWidget.setVisibility(View.VISIBLE);
                }else{
                    showProductKeyWidget.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //Firebase Initialization & Listener
        database = FirebaseDatabase.getInstance();
        refFirebaseDatabase = database.getReference("product_key");

        refFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product_keys= new HashMap<>();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String value = ds.getValue().toString();
                    product_keys.put(key,value);
                    //Log.d("key",key);
                    //Log.d("value",value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                keyAuthError.setVisibility(View.VISIBLE);
                keyAuthError.setText("Error: Connecting to Database !");
                Toast.makeText(GettingStarted.this, "Check Your Internet!", Toast.LENGTH_SHORT).show();
            }
        });

        //Initializing here after Adapter has set the View
        letsGo =  findViewById(R.id.letsGoBtn);
        contactDeveloper = findViewById(R.id.contactDeveloper);
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letsGo.setEnabled(false);
                progressbar.setVisibility(View.VISIBLE);
                String userProductKey = getProductKey.getText().toString();
                if(product_keys!=null){
                    // If Hashmap contains key then
                    if(product_keys.containsKey(userProductKey)){
                        String isKeyAlreadyUsed = product_keys.get(userProductKey);
                        if(Objects.equals(isKeyAlreadyUsed, "false")){
                            // Authentication Complete Proceed to MainActivity
                            sharedPreferences = getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                            editor_sharedPref = sharedPreferences.edit();
                            editor_sharedPref.putBoolean(isFirstRun,false);
                            editor_sharedPref.putString(productKey_Prefs,userProductKey);
                            editor_sharedPref.apply();
                            // Updating Database
                            refFirebaseDatabase.child(userProductKey).setValue("true");
                            startActivity(new Intent(GettingStarted.this, MainActivity.class));
                            finish();
                        }else{
                            progressbar.setVisibility(View.GONE);
                            letsGo.setEnabled(true);
                            keyAuthError.setVisibility(View.VISIBLE);
                            keyAuthError.setText(R.string.error_UsedKey);
                            Toast.makeText(GettingStarted.this, "This Product Key has already Activated! If you are the Owner then Contact Developer. ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressbar.setVisibility(View.GONE);
                        letsGo.setEnabled(true);
                        keyAuthError.setVisibility(View.VISIBLE);
                        keyAuthError.setText("Invalid Product Key!");
                        Toast.makeText(GettingStarted.this, "Invalid Product Key: Contact Developer!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    keyAuthError.setVisibility(View.VISIBLE);
                    keyAuthError.setText("Error: No Internet Connection!");
                    Toast.makeText(GettingStarted.this, "Error: No Internet Connection!", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    letsGo.setEnabled(true);
                }
            }
        });
        contactDeveloper.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.instagram.com/m0hit.sharmaji/"));
            startActivity(i);
            Toast.makeText(GettingStarted.this, "Sending msg to developer!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layoutSlides.length];

        int colorsActive = getResources().getColor(R.color.dot_light_active);
        int colorsInactive = getResources().getColor(R.color.dot_dark_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    /**
     * View pager adapter
     */
    public class AdapterViewPager extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public AdapterViewPager() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layoutSlides[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layoutSlides.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}