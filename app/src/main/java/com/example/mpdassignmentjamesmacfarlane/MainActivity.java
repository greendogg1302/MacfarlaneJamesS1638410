package com.example.mpdassignmentjamesmacfarlane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navView;
    private DrawerLayout drawer;
    private String result;
    private LinearLayout CILayer;
    private LinearLayout CRLayer;
    private LinearLayout PRLayer;
    private Button searchSubmit;
    private ArrayList<ItemType> currIncsArr = new ArrayList<ItemType>();
    private ArrayList<ItemType> currRoadArr = new ArrayList<ItemType>();
    private ArrayList<ItemType> planRoadArr = new ArrayList<ItemType>();
    // Traffic Scotland URLs
    private String currRoad = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String planRoad = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String incURL = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private ViewFlipper flip;
    private Spinner dateSpin;
    private Spinner monthSpin;
    private Spinner yearSpin;
    private EditText search;
    private LinearLayout searchDest;

    private TextView itemTitle;
    private TextView itemSTDate;
    private TextView itemENDate;
    private TextView itemDesc;
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyDHb5CVJvPxkTgJkIbTTOLRyGQZ9j9JikU";


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this deals with the creation and preperation of the map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView=findViewById(R.id.mapview);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // this handles the rest of the items for the details page
        itemDesc=findViewById(R.id.itemDesc);
        itemSTDate=findViewById(R.id.itemStart);
        itemENDate=findViewById(R.id.itemEnd);
        itemTitle=findViewById(R.id.itemTitle);



        //This all deals with the items needed for the search function
        search = findViewById(R.id.roadSearch);
        dateSpin = (Spinner) findViewById(R.id.date);
        ArrayAdapter<CharSequence> dateadapter = ArrayAdapter.createFromResource(this,
                R.array.date, android.R.layout.simple_spinner_item);
        dateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpin.setAdapter(dateadapter);

        monthSpin = (Spinner) findViewById(R.id.month);
        ArrayAdapter<CharSequence> monthadapter = ArrayAdapter.createFromResource(this,
                R.array.month, android.R.layout.simple_spinner_item);
        monthadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpin.setAdapter(monthadapter);

        yearSpin = (Spinner) findViewById(R.id.year);
        ArrayAdapter<CharSequence> yearadapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpin.setAdapter(yearadapter);

        searchSubmit = (Button) findViewById(R.id.searchSubmit);
        searchSubmit.setOnClickListener(this);
        searchDest = findViewById(R.id.searchDest);

        //this section deals with the setup of the navigation bar/drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //this deals with the rest of the items that will be called upon on later in the programme
        CILayer = findViewById(R.id.incidents);
        CRLayer = findViewById(R.id.roadw);
        PRLayer = findViewById(R.id.plannedroadw);
        flip = findViewById(R.id.Flip);

        //This is where the threads start, and what creates the ui for the user
        startProgress();
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(flip.getDisplayedChild()==4){
            flip.setDisplayedChild(0);
        } else{
            super.onBackPressed();
        }
    }

    public void onClick(View aview) {
        if (aview == searchSubmit) {
            searchDest.removeAllViews();
            Search searchInc = new Search("currinc");
            Thread th = new Thread(searchInc);
            th.start();
            th.setPriority(10);
            Search searchRoad = new Search("currroad");
            Thread th1 = new Thread(searchRoad);
            th1.start();
            th1.setPriority(9);
            Search searchPRoad = new Search("planroad");
            Thread th2 = new Thread(searchPRoad);
            th2.start();
            th2.setPriority(8);
        }else if((int)aview.getId()<1000){
            setMap(currIncsArr.get((int)aview.getId()));
            flip.setDisplayedChild(4);
        }else if((int)aview.getId()<2000&&(int)aview.getId()>=1000){
            setMap(currRoadArr.get((int)aview.getId()-1000));
            flip.setDisplayedChild(4);
        }else if((int)aview.getId()>=2000){
            setMap(planRoadArr.get((int)aview.getId()-2000));
            flip.setDisplayedChild(4);
        }
    }

    public void searchItems(ArrayList<ItemType> disArray) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            Date searchedDate = null;
            if (!dateSpin.getSelectedItem().toString().replace(" ","").isEmpty()) {
                searchedDate = sdf.parse(dateSpin.getSelectedItem().toString() + " " + monthSpin.getSelectedItem().toString() + " " + yearSpin.getSelectedItem().toString());
            }
            String searchedRoad = search.getText().toString();
            int i = 0;
            if (searchedDate != null && searchedRoad != "") {
                while (i < disArray.size()) {
                    if (disArray.get(i).getTitle().toLowerCase().contains(searchedRoad.toLowerCase()) && searchedDate.after(disArray.get(i).getStDate()) && searchedDate.before(disArray.get(i).getEnDate())) {
                        LinearLayout layout = createLayout();
                        TextView title = createATextView(0, disArray.get(i).getTitle(), 30, 20, 0);
                        title.setGravity(Gravity.CENTER);
                        Drawable dr = getDrawable(R.drawable.customborder);
                        TextView date = createATextView(0, sdf.format(disArray.get(i).getStDate())+" until "+sdf.format(disArray.get(i).getEnDate()), 15, 20, 0);
                        date.setGravity(Gravity.CENTER);
                        layout.setBackground(dr);
                        layout.addView(title);
                        layout.addView(date);
                        searchDest.addView(layout);
                    }
                    i++;
                }
            } else if (searchedDate != null) {
                while (i < disArray.size()) {
                    if (searchedDate.after(disArray.get(i).getStDate()) && searchedDate.before(disArray.get(i).getEnDate())) {
                        LinearLayout layout = createLayout();
                        TextView title = createATextView(0, disArray.get(i).getTitle(), 30, 20, 0);
                        title.setGravity(Gravity.CENTER);
                        Drawable dr = getDrawable(R.drawable.customborder);
                        TextView date = createATextView(0, sdf.format(disArray.get(i).getStDate())+" until "+sdf.format(disArray.get(i).getEnDate()), 15, 20, 0);
                        date.setGravity(Gravity.CENTER);
                        layout.setBackground(dr);
                        layout.addView(title);
                        layout.addView(date);
                        searchDest.addView(layout);
                    }
                    i++;
                }
            } else if (searchedRoad!="") {
                while (i < disArray.size()) {
                    if (disArray.get(i).getTitle().toLowerCase().contains(searchedRoad.toLowerCase())) {
                        LinearLayout layout = createLayout();
                        TextView title = createATextView(0, disArray.get(i).getTitle(), 30, 20, 0);
                        title.setGravity(Gravity.CENTER);
                        Drawable dr = getDrawable(R.drawable.customborder);
                        TextView date = createATextView(0, sdf.format(disArray.get(i).getStDate())+" until "+sdf.format(disArray.get(i).getEnDate()), 15, 20, 0);
                        date.setGravity(Gravity.CENTER);
                        layout.setBackground(dr);
                        layout.addView(title);
                        layout.addView(date);
                        searchDest.addView(layout);
                    }
                    i++;
                }
            }else{
                searchDest.removeAllViews();
                TextView title = createATextView(0, "Search Item Not Valid", 30, 20, 0);
                title.setGravity(Gravity.CENTER);
                searchDest.addView(title);
            }

        } catch (Exception e) {
            Log.e("Parse Error:", e.toString());
        }

    }

    public void setMap(ItemType item){
        String[] latlong =  item.getLatLong().split(" ");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        LatLng location = new LatLng(latitude, longitude);
        System.out.println(location.toString());
        gmap.moveCamera(CameraUpdateFactory.newLatLng(location));
        gmap.addMarker(new MarkerOptions()
                .position(location)
                .title(item.getTitle()));
        itemTitle.setText(item.getTitle());
        itemSTDate.setText(item.getStDate().toString());
        itemENDate.setText(item.getEnDate().toString());
        itemDesc.setText(item.getDesc());
    }

    public void startProgress() {
        // Run network access on a separate thread;
        Task conn = new Task(incURL, "currinc");
        Thread th = new Thread(conn);
        th.start();
        th.setPriority(10);
        Task connRoad = new Task(currRoad, "currroad");
        Thread th1 = new Thread(connRoad);
        th1.start();
        th1.setPriority(9);
        Task connPRoad = new Task(planRoad, "planroad");
        Thread th2 = new Thread(connPRoad);
        th2.start();
        th2.setPriority(8);
    } //

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.currInc:
                flip.setDisplayedChild(0);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.currRoad:
                flip.setDisplayedChild(1);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.planRoad:
                flip.setDisplayedChild(2);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.searchItem:
                flip.setDisplayedChild(3);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
    }

    private class Search implements Runnable {
        private String page;

        public Search(String lpage) {
            page = lpage;
        }


        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (page == "currinc") {
                        searchItems(currIncsArr);
                    } else if (page == "currroad") {
                        searchItems(currRoadArr);
                    } else if (page == "planroad") {
                        searchItems(planRoadArr);
                    }
                }
            });
        }

    }


    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable {
        private String url;
        private String page;

        public Task(String aurl, String lpage) {
            url = aurl;
            page = lpage;
        }


        @Override
        public void run() {

            loadarr(url, page);

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if (page == "currinc") {
                        displayScreen(currIncsArr, page);
                    } else if (page == "currroad") {
                        displayScreen(currRoadArr, page);
                    } else if (page == "planroad") {
                        displayScreen(planRoadArr, page);
                    }
                }
            });
        }

    }


    public void loadarr(String url, String type) {


        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";


        Log.e("MyTag", "in run");

        try {
            Log.e("MyTag", "in try");
            aurl = new URL(url);
            yc = aurl.openConnection();
            int st;
            int en;
            String title = "";
            String latLong = "";
            String desc = "";

            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            //
            // Throw away the first 2 header lines before parsing
            //
            //
            //
            while ((inputLine = in.readLine()) != null) {
                try {
                    result = result + inputLine;
                    //Log.e("MyTag",inputLine);
                    if (inputLine.contains("<item>")) {
                        while (inputLine.contains("</item>") == false) {
                            inputLine = in.readLine();
                            if (inputLine.contains("<title>")) {
                                st = inputLine.indexOf("<title>") + "<title>".length();
                                en = inputLine.lastIndexOf("</title>");
                                title = inputLine.substring(st, en);
                            }
                            if (inputLine.contains("<description>") && inputLine.contains("</description>")) {
                                st = inputLine.indexOf("<description>") + "<description>".length();
                                en = inputLine.lastIndexOf("</description>");
                                desc = inputLine.substring(st, en);
                            } else if (inputLine.contains("<description>")) {
                                st = inputLine.indexOf("<description>") + "<description>".length();
                                desc = inputLine.substring(st, inputLine.length() - 1);
                                inputLine = in.readLine();
                                while (!inputLine.contains("</description>")) {
                                    desc += inputLine;

                                    inputLine = in.readLine();
                                }

                                en = inputLine.lastIndexOf("</description>");
                                desc += inputLine.substring(0, en);
                            }
                            if (inputLine.contains("<georss:point>")) {

                                st = inputLine.indexOf("<georss:point>") + "<georss:point>".length();
                                en = inputLine.lastIndexOf("</georss:point>");
                                latLong = inputLine.substring(st, en);
                            }
                        }
                        if (type == "currinc") {
                            CurrentIncedent inc = new CurrentIncedent(title, desc, latLong);
                            currIncsArr.add(inc);
                        } else if (type == "currroad") {
                            CurrentRoadworks ro = new CurrentRoadworks(title, desc, latLong);
                            currRoadArr.add(ro);
                        } else if (type == "planroad") {

                            PlannedRoadworks plRo = new PlannedRoadworks(title, desc, latLong);
                            planRoadArr.add(plRo);
                        }

                    }
                } catch (Exception e) {
                    Log.e("that din't work", e.toString());
                }

            }
            in.close();

        } catch (IOException ae) {

            Log.e("MyTag", ae.toString());
        }

    }

    public void displayScreen(ArrayList<ItemType> disArray, String page) {
        int i = 0;
        if (disArray.size() == 0) {
            TextView msg = createATextView(0, "No Items to Show", 30, 20, 0);
            msg.setGravity(Gravity.CENTER);
            if (page == "currinc") {
                CILayer.addView(msg);
            } else if (page == "currroad") {
                CRLayer.addView(msg);
            } else if (page == "planroad") {
                PRLayer.addView(msg);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

        while (i < disArray.size()) {
            LinearLayout layout = createLayout();
            TextView title = createATextView(0, disArray.get(i).getTitle(), 30, 20, 0);
            Drawable dr = getDrawable(R.drawable.customborder);
            title.setGravity(Gravity.CENTER);
            TextView date = createATextView(0, sdf.format(disArray.get(i).getStDate())+" until "+sdf.format(disArray.get(i).getEnDate()), 15, 20, 0);
            date.setGravity(Gravity.CENTER);
            layout.setBackground(dr);
            layout.addView(title);
            layout.addView(date);
            layout.setOnClickListener(this);
            if (page == "currinc") {
                layout.setId(i);
                CILayer.addView(layout);
            } else if (page == "currroad") {
                layout.setId(1000+i);
                CRLayer.addView(layout);
            } else if (page == "planroad") {
                layout.setId(2000+i);
                PRLayer.addView(layout);
            }
            i++;
        }

    }


    public LinearLayout createLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams _params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        _params.setMargins(10, 20, 10, 20);
        layout.setLayoutParams(_params);
        layout.setPadding(10, 20, 10, 20);
        layout.setGravity(Gravity.CENTER);
        return layout;
    }


    public TextView createATextView(int align,
                                    String text, int fontSize, int margin, int padding) {

        TextView textView_item_name = new TextView(this);

// LayoutParams layoutParams = new LayoutParams(
// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
// layoutParams.gravity = Gravity.Center;
        RelativeLayout.LayoutParams _params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        _params.setMargins(margin, margin, margin, margin);
        _params.addRule(align);
        textView_item_name.setLayoutParams(_params);

        textView_item_name.setText(text);
        textView_item_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView_item_name.setTextColor(Color.parseColor("#000000"));
// textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView_item_name.setPadding(padding, padding, padding, padding);

        return textView_item_name;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

} // End of MainActivity