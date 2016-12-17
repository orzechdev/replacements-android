package com.replacements.replacements.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.replacements.replacements.data.ClassDbAdapter;
import com.replacements.replacements.data.DbAdapter;
import com.replacements.replacements.data.ReplacementDbAdapter;
import com.replacements.replacements.data.ScheduleUrlFilesDbAdapter;
import com.replacements.replacements.data.TeacherDbAdapter;
import com.replacements.replacements.fragments.HomeFragment;
import com.replacements.replacements.fragments.ProfileFragment;
import com.replacements.replacements.fragments.ProfileFragmentClass;
import com.replacements.replacements.fragments.ProfileFragmentTeacher;
import com.replacements.replacements.R;
import com.replacements.replacements.fragments.ReplacementsFragment;
import com.replacements.replacements.fragments.ScheduleFragment;
import com.replacements.replacements.interfaces.ApplicationConstants;
import com.replacements.replacements.preferences.SettingsActivity;
//import com.replacements.replacements.sync.GcmUserRegistration;
//import com.replacements.replacements.sync.GcmUserUnregistration;
import com.replacements.replacements.sync.ProfileRegister;
import com.replacements.replacements.sync.ProfileSetToServer;
import com.replacements.replacements.sync.ScheduleUpdate;


public class ReplacementsMain extends AppCompatActivity {
    private static final String CLASS_NAME = ReplacementsMain.class.getName();
    public ConnectivityManager connManager;
    private NavigationView mNavigationView;
    private int mCurrentSelectedPosition;
    private DrawerLayout mDrawerLayout;
    public ViewPager viewPager;
    private boolean configChanged;
    public boolean rotationChanged;
    private int currentProfileTab;
    private ViewPagerAdapter adapter;
    public ProfileFragmentClass profileFragmentClass;
    public ProfileFragmentTeacher profileFragmentTeacher;
    private DbAdapter dbAdapter;
    private ReplacementDbAdapter replacementDbAdapter;
    private ClassDbAdapter classDbAdapter;
    private TeacherDbAdapter teacherDbAdapter;
    private ScheduleUrlFilesDbAdapter scheduleUrlFilesDbAdapter;
    private String no_internet;
    private ArrayList<Long> myDataSetAll = new ArrayList<>();
    // GCM
    private static final String REG_ID = "regId";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacements_main);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        dbAdapter.close();
        //dbAdapter.close();
        scheduleUrlFilesDbAdapter = new ScheduleUrlFilesDbAdapter(this);
        scheduleUrlFilesDbAdapter.open();
        scheduleUrlFilesDbAdapter.close();

    //    Intent scheduleIntent = new Intent(this, ScheduleUpdate.class);
        //scheduleIntent.putExtra("jsonUpdate", true);
    //    this.startService(scheduleIntent);


        // Ustawienie ostatnio odwiedzanego linku dla planu lekcji - NA RAZIE NIE USUWAC
//        if(savedInstanceState == null) {
//            Log.i("ReplacementsMain", "onCreate");
//            //if (savedInstanceState.getBoolean("wasRun", false)) {
//            SharedPreferences prefs = getApplicationContext().getSharedPreferences("dane", Context.MODE_PRIVATE);
//            SharedPreferences.Editor edit = prefs.edit();
//            edit.putString("lastUrlSchedule", "");
//            edit.commit();   // can use edit.apply() but in this case commit is better
//            //}
//        }

        //classDbAdapter = new ClassDbAdapter(getApplicationContext());
        //teacherDbAdapter = new TeacherDbAdapter(getApplicationContext());
        replacementDbAdapter = new ReplacementDbAdapter(this);
        classDbAdapter = new ClassDbAdapter(this);
        teacherDbAdapter = new TeacherDbAdapter(this);

        setupToolbar();
        initNavigationDrawer();

        Bundle intentExtra = getIntent().getExtras();
        boolean isNotified;

        //TODO
//        SharedPreferences mSharedPreferences2 = getSharedPreferences("dane", 0);
//        SharedPreferences.Editor localEditor2 = mSharedPreferences2.edit();
//        localEditor2.putInt("chosenSchool", 1);
//        localEditor2.putBoolean("schoolToChange", false);
//        localEditor2.putBoolean("schoolChangeStarted", false);
//        localEditor2.apply();
        //TODO


        if (savedInstanceState != null) {
            Log.i("QQQQQQQQQ", "4");
            mCurrentSelectedPosition = savedInstanceState.getInt("STATE_SELECTED_POSITION");
            Menu menu = mNavigationView.getMenu();
            menu.getItem(mCurrentSelectedPosition).setChecked(true);
            menuItems(menu.getItem(mCurrentSelectedPosition));
        } else if (intentExtra != null) {
            Log.i("QQQQQQQQQ", "1");
            isNotified = intentExtra.getBoolean("isNotified", false);
            Log.i("resume", "2");
            if (isNotified) {
                Log.i("resume", "3");
                int extraMenuItem = intentExtra.getInt("menuItem", 2);
                Menu menu = mNavigationView.getMenu();
                menu.getItem(extraMenuItem).setChecked(true);
                menuItems(menu.getItem(extraMenuItem));
                if(extraMenuItem == 2){
                    SharedPreferences mSharedPreferences = getSharedPreferences("dane", 0);
                    SharedPreferences.Editor localEditor = mSharedPreferences.edit();
                    localEditor.putLong("savedTime", 0);
                    localEditor.apply();
                }
            } else {
                Log.i("QQQQQQQQQ", "5-1");
                Menu menu = mNavigationView.getMenu();
                menu.getItem(2).setChecked(true);
                menuItems(menu.getItem(2));
            }
        } else {
            Log.i("QQQQQQQQQ", "5-2");
            Menu menu = mNavigationView.getMenu();
            menu.getItem(2).setChecked(true);
            menuItems(menu.getItem(2));
        }


//        connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
//        //Sprawdzenie czy w ustawieniach sa wlaczone powiadomienia GCM
//        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if(localSharedPreferences.getBoolean("pref_notify_switch",true)) {
//            Log.i("ReplMainGCM","check 1");
//            // Sprawdzenie czy uzytkownik byl juz rejestrowany w GCM
//            SharedPreferences prefs = getSharedPreferences("gcm", Context.MODE_PRIVATE);
//            String registrationId = prefs.getString(REG_ID, "");
//            if (TextUtils.isEmpty(registrationId)) {
//                Log.i("ReplMainGCM","check 2");
//                // Check if Google Play Service is installed in Device
//                // Play services is needed to handle GCM stuffs
//                if (checkPlayServices()) {
//                    Log.i("ReplMainGCM","check 3");
//                    if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
//                        Log.i("ReplMainGCM","check 4");
//                        // Register Device in GCM Server by IntentService
//                        Intent gcmUserRegistration = new Intent(this, GcmUserRegistration.class);
//                        this.startService(gcmUserRegistration);
//                    }
//                }
//            }
//        }else{
//            Log.i("ReplMainGCM","check not 1");
//            // Sprawdzenie czy uzytkownik byl juz rejestrowany w GCM
//            SharedPreferences prefs = getSharedPreferences("gcm", Context.MODE_PRIVATE);
//            String registrationId = prefs.getString(REG_ID, "");
//            if (!TextUtils.isEmpty(registrationId)) {
//                Log.i("ReplMainGCM", "check not 2");
//                if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
//                    Log.i("ReplMainGCM", "check not 3");
//                    // Unregister Device from GCM Server by IntentService
//                    Intent gcmUserUnregistration = new Intent(this, GcmUserUnregistration.class);
//                    this.startService(gcmUserUnregistration);
//                }
//            }
//        }
    }

    // Check if Google Playservices is installed in Device or not
//    private boolean checkPlayServices() {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(this);
//        if(result != ConnectionResult.SUCCESS) {
//            if(googleAPI.isUserResolvableError(result)) {
//                Log.i(CLASS_NAME, "checkPlayServices support, not installed");
//                googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(CLASS_NAME, "checkPlayServices not support");
//                Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.no_support_google_api), Snackbar.LENGTH_LONG).show();
//            }
//            return false;
//        }else{
//            Log.i(CLASS_NAME, "checkPlayServices support");
//            //Snackbar.make(findViewById(R.id.drawer_layout), "This device supports Play services, App will work normally", Snackbar.LENGTH_LONG).show();
//        }
//        return true;
//    }

    @Override
    protected void onStart() {
        super.onStart();

        no_internet = getString(R.string.no_internet_connect);

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean prefDev = prefs.getBoolean("pref_dev", false);
//
//        if(prefDev){
//            Menu menu = mNavigationView.getMenu();
//            //menu.getItem(0).setVisible(true);
//            menu.getItem(1).setVisible(true);
//        }else{
//            Menu menu = mNavigationView.getMenu();
//            //menu.getItem(0).setVisible(false);
//            menu.getItem(1).setVisible(false);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            boolean toDoRegisterUser1 = prefs.getBoolean("toDoRegisterUser1", false);
            boolean toDoUnregisterUser1 = prefs.getBoolean("toDoUnregisterUser1", false);
            boolean toDoRegisterUser2 = prefs.getBoolean("toDoRegisterUser2", false);
            boolean toDoUnregisterUser2 = prefs.getBoolean("toDoUnregisterUser2", false);
            SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (localSharedPreferences.getBoolean("pref_notify_switch", true) && !toDoRegisterUser1 && !toDoRegisterUser1 && !toDoUnregisterUser1 && !toDoUnregisterUser2) {
                boolean isDataToChange = prefs.getBoolean("dataToChange", false);
                boolean isModulesToChange = prefs.getBoolean("modulesToChange", false);
                if (isDataToChange && !isModulesToChange) {
                    changeSetInServer(1);
                }
                if (!isDataToChange && isModulesToChange) {
                    changeSetInServer(2);
                }
                if (isDataToChange && isModulesToChange) {
                    changeSetInServer(3);
                }
            } else {
                String urlUnregister;
                String urlRegister;
                if(prefs.getInt("chosenSchool", 1) == 1){
                    urlRegister = ApplicationConstants.SCHOOL_SERVER_1;
                }else{
                    urlRegister = ApplicationConstants.SCHOOL_SERVER_2;
                }
                if (toDoRegisterUser1 || toDoRegisterUser2) {
                    Log.i(CLASS_NAME, "onResume toDoRegisterUser");
                    String FCMToken = FirebaseInstanceId.getInstance().getToken();
                    Intent profileRegister = new Intent(this, ProfileRegister.class);
                    profileRegister.putExtra("serverAction", 1);
                    profileRegister.putExtra("token", FCMToken);
                    profileRegister.putExtra("url", urlRegister);
                    this.startService(profileRegister);
                }
                if (toDoUnregisterUser1) {
                    urlUnregister = ApplicationConstants.SCHOOL_SERVER_1;
                    Log.i(CLASS_NAME, "onResume toDoUnregisterUser1");
                    String FCMToken = FirebaseInstanceId.getInstance().getToken();
                    Intent profileRegister = new Intent(this, ProfileRegister.class);
                    profileRegister.putExtra("serverAction", 2);
                    profileRegister.putExtra("token", FCMToken);
                    profileRegister.putExtra("url", urlUnregister);
                    this.startService(profileRegister);
                }
                if (toDoUnregisterUser2) {
                    urlUnregister = ApplicationConstants.SCHOOL_SERVER_2;
                    Log.i(CLASS_NAME, "onResume toDoUnregisterUser2");
                    String FCMToken = FirebaseInstanceId.getInstance().getToken();
                    Intent profileRegister = new Intent(this, ProfileRegister.class);
                    profileRegister.putExtra("serverAction", 2);
                    profileRegister.putExtra("token", FCMToken);
                    profileRegister.putExtra("url", urlUnregister);
                    this.startService(profileRegister);
                }
            }
        }
        String dirScheduleName = prefs.getString("scheduleFilesDirNameCurrent", "");
        if (dirScheduleName.equals("")){
            if (!prefs.getBoolean("scheduleUpdateToDo", false)) {
                Intent scheduleIntent = new Intent(getBaseContext(), ScheduleUpdate.class);
                scheduleIntent.putExtra("jsonUpdate", true);
                startService(scheduleIntent);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("STATE_SELECTED_POSITION", mCurrentSelectedPosition);
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt("STATE_SELECTED_POSITION", 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
        menuItems(menu.getItem(mCurrentSelectedPosition));
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    //    ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    public void setupViewPager(ViewPager viewPager) {
        if(adapter == null) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
        }
        if(profileFragmentClass == null) {
            profileFragmentClass = new ProfileFragmentClass();
            adapter.addFrag(profileFragmentClass, getString(R.string.tab_class));
        }
        if(profileFragmentTeacher == null) {
            profileFragmentTeacher = new ProfileFragmentTeacher();
            adapter.addFrag(profileFragmentTeacher, getString(R.string.tab_teacher));
        }
        viewPager.setAdapter(adapter);
        Log.i("WYBRANO", "Add");
    }
//    public ViewPagerAdapter getCurrentViewPage(){
//        return adapter;
//    }
    private void clearViewPager(ViewPager viewPager){
        viewPager.removeAllViews();
    //    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    //    adapter.clearFrags();
    //    viewPager.setAdapter(adapter);
        Log.i("WYBRANO", "Clear");
    }
    private void visibilityTabLayout(TabLayout tabLayout){
        if (tabLayout.getTabCount() == 0) {
            tabLayout.setVisibility(View.GONE);
            Log.i("WYBRANO", "GONE");
        } else{
            tabLayout.setVisibility(View.VISIBLE);
            Log.i("WYBRANO", "VISIBLE");
        }
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
//        public void clearFrags(){
//            mFragmentList.clear();
//        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
    }
    public int getCurrentProfileTab(){
        return currentProfileTab;
    }

    @SuppressWarnings("deprecation")
    private void initNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        View hView =  mNavigationView.getHeaderView(0);
        LinearLayout drawerHeader = (LinearLayout) hView.findViewById(R.id.drawer_header);
        TextView textHeader = (TextView)drawerHeader.findViewById(R.id.drawer_header_text);
        SharedPreferences prefs = getSharedPreferences("dane", Context.MODE_PRIVATE);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            drawerHeader.setBackgroundDrawable(ContextCompat.getDrawable(this, (prefs.getInt("chosenSchool", 1) == 1)? R.drawable.header_image_1 : R.drawable.header_image_2));
        } else {
            drawerHeader.setBackground(ContextCompat.getDrawable(this, (prefs.getInt("chosenSchool", 1) == 1)? R.drawable.header_image_1 : R.drawable.header_image_2));
        }
        textHeader.setText((prefs.getInt("chosenSchool", 1) == 1)? R.string.school_name_1 : R.string.school_name_2);

    //    setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }
    private void setupDrawerContent(NavigationView navigationView) {

    //    addItemsRunTime(navigationView);

        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItems(menuItem);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void selectMenuItem(int menuItem){
        Log.i("ReplacementsMain", "selectMenuItem");
        Menu menu = mNavigationView.getMenu();
        menu.getItem(menuItem).setChecked(true);
        menuItems(menu.getItem(menuItem));
    }

    private void menuItems(MenuItem menuItem){
        TabLayout tabLayout;
        switch (menuItem.getItemId()) {
            // Wybranie profilu i podmiana odpowiedniego fragmentu
            case R.id.nav_profile:
                menuItem.setChecked(true);
                Log.i("WYBRANO", "0");
                mCurrentSelectedPosition = 0;
                setTitle(menuItem.getTitle());


                viewPager = (ViewPager) findViewById(R.id.viewpager);
                if(!configChanged || rotationChanged) {
                    Fragment fragmentProfile = new ProfileFragment();
                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fmProfile = getSupportFragmentManager();
                    FragmentTransaction ftProfile = fmProfile.beginTransaction();
                    //        ftProfile.remove(fmProfile.findFragmentById(R.id.content_frame));
                    ftProfile.replace(R.id.content_frame, fragmentProfile);
                    ftProfile.commit();
                    Log.i("WYBRANO", "1a");

                    // Insert pages into viewPager and tabs from viewPager to tabLayout and set visibility
                    Log.i("WYBRANO", "2a");
                    setupViewPager(viewPager);
                    Log.i("WYBRANO", "3a");
                    tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                    Log.i("WYBRANO", "4a");
                    tabLayout.setupWithViewPager(viewPager);
                    Log.i("WYBRANO", "5a");
                    visibilityTabLayout(tabLayout);
                    Log.i("WYBRANO", "6a");
                    configChanged = true;
                    rotationChanged = false;
                    Log.i("WYBRANO", "BYLOBYLOBYLOBYLO");

                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            viewPager.setCurrentItem(tab.getPosition());
                            switch (tab.getPosition()) {
                                case 0:
                                    currentProfileTab = 0;
                                    break;
                                case 1:
                                    currentProfileTab = 1;
                                    break;
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                        }
                    });
                }
            //    if (viewPager.getVisibility() == View.VISIBLE){
            //        Log.i("WYBRANO", "6 - Visible");
            //    } else if (viewPager.getVisibility() == View.GONE){
            //        Log.i("WYBRANO", "6 - Gone");
            //    }
                break;
            // Wybranie aktualnosci i podmiana odpowiedniego fragmentu
            case R.id.nav_home:
                menuItem.setChecked(true);
                Log.i("WYBRANO", "1");
                mCurrentSelectedPosition = 1;
                setTitle(menuItem.getTitle());

                // Clear pages and insetr empty space to tabLayout and set visibility
            //    viewPager = (ViewPager) findViewById(R.id.viewpager);
            //    clearViewPager(viewPager);
            //    tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            //    tabLayout.setupWithViewPager(viewPager);
            //    visibilityTabLayout(tabLayout);
                tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                if(configChanged) {
                    tabLayout.removeAllTabs();
                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    clearViewPager(viewPager);
                }
                visibilityTabLayout(tabLayout);

                Fragment fragmentHome = new HomeFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fmHome = getSupportFragmentManager();
                FragmentTransaction ftHome = fmHome.beginTransaction();
                ftHome.replace(R.id.content_frame, fragmentHome);
                ftHome.commit();
                configChanged = false;
                break;
            // Wybranie zastepstw i podmiana odpowiedniego fragmentu
            case R.id.nav_replacements:
                menuItem.setChecked(true);
                Log.i("WYBRANO", "2");
                mCurrentSelectedPosition = 2;
                setTitle(menuItem.getTitle());

                // Clear pages and insetr empty space to tabLayout and set visibility
            //    viewPager = (ViewPager) findViewById(R.id.viewpager);
            //    clearViewPager(viewPager);
            //    tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            //    tabLayout.setupWithViewPager(viewPager);
            //    visibilityTabLayout(tabLayout);
                tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                if(configChanged) {
                    tabLayout.removeAllTabs();
                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    clearViewPager(viewPager);
                }
                visibilityTabLayout(tabLayout);

                Fragment fragmentReplacements = new ReplacementsFragment();
                //Bundle args = new Bundle();
                //args.putInt(ReplacementsFragment.ARG_PLANET_NUMBER, position);
                //fragment.setArguments(args);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fmReplacements = getSupportFragmentManager();
                FragmentTransaction ftReplacements = fmReplacements.beginTransaction();
                ftReplacements.replace(R.id.content_frame, fragmentReplacements);
                ftReplacements.commit();
                configChanged = false;
                break;
            // Wybranie planu i podmiana odpowiedniego fragmentu
            case R.id.nav_schedule:
                menuItem.setChecked(true);
        //        menuItem.setCheckable(false);
                Log.i("WYBRANO", "3");
                mCurrentSelectedPosition = 3;
                setTitle(menuItem.getTitle());

                // Clear pages and insetr empty space to tabLayout and set visibility
            //    viewPager = (ViewPager) findViewById(R.id.viewpager);
            //    clearViewPager(viewPager);
            //    tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            //    tabLayout.setupWithViewPager(viewPager);
            //    visibilityTabLayout(tabLayout);
                tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                if(configChanged) {
                    tabLayout.removeAllTabs();
                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    clearViewPager(viewPager);
                }
                visibilityTabLayout(tabLayout);

                Fragment fragmentSchedule = new ScheduleFragment();
                FragmentManager fmSchedule = getSupportFragmentManager();
                FragmentTransaction ftSchedule = fmSchedule.beginTransaction();
                ftSchedule.replace(R.id.content_frame, fragmentSchedule);
                ftSchedule.commit();
                configChanged = false;
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                String urlIntent = prefs.getString("schedule_url", "http://zschocianow.pl/plan/");
//
//                //String urlIntent = getString(R.string.url_schedule);
//                Intent scheduleIntent = new Intent(Intent.ACTION_VIEW);
//                scheduleIntent.setData(Uri.parse(urlIntent));
//                startActivity(scheduleIntent);
                break;
            // Wybranie ustawien i otworzenie activity ustawien
            case R.id.nav_settings:
                menuItem.setCheckable(false);
                Log.i("WYBRANO", "4");
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
        }
    }


    public ArrayList<Long> readAllFromSQLite() {
        myDataSetAll.clear();

        Cursor cursor;
        classDbAdapter = new ClassDbAdapter(this);
        classDbAdapter.open();
        cursor = classDbAdapter.getAllIdsSelected();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long idOfData = cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_ID));
                    if(cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED)) == 1){
                        myDataSetAll.add(idOfData);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        classDbAdapter.close();

        teacherDbAdapter = new TeacherDbAdapter(this);
        teacherDbAdapter.open();
        cursor = teacherDbAdapter.getAllIdsSelected();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long idOfData = cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_ID));
                    if(cursor.getLong(cursor.getColumnIndex(ClassDbAdapter.KEY_SELECTED)) == 1){
                        myDataSetAll.add(idOfData);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        teacherDbAdapter.close();

        return myDataSetAll;
    }

    public void changeSetInServer(int toChange){
        Log.i(CLASS_NAME, "changeSetInServer 10");
        connManager = ((ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            Log.i(CLASS_NAME, "changeSetInServer 20");
            // Run service which will change user set in server
            Intent profileSetToServer = new Intent(this, ProfileSetToServer.class);
            if(toChange == 1) {
                Log.i(CLASS_NAME, "changeSetInServer 30");
                profileSetToServer.putExtra("serverAction", 1);
                // Get all selected user classes and teachers
                readAllFromSQLite();
                String dataIdsString;
                if(!myDataSetAll.isEmpty()){
                    dataIdsString = myDataSetAll.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                }else{
                    dataIdsString = "";
                }
                profileSetToServer.putExtra("dataIds", dataIdsString);
            }else if(toChange == 2){
                Log.i(CLASS_NAME, "changeSetInServer 40");
                profileSetToServer.putExtra("serverAction", 2);
                SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(localSharedPreferences.getString("pref_notify_repl", "1").equals("3")){
                    profileSetToServer.putExtra("modules", "2");
                }else if(!localSharedPreferences.getString("pref_notify_repl", "1").equals("3")){
                    profileSetToServer.putExtra("modules", "1,2");
                }
            }else{
                Log.i(CLASS_NAME, "changeSetInServer 50");
                profileSetToServer.putExtra("serverAction", 3);
                // Get all selected user classes and teachers
                readAllFromSQLite();
                String dataIdsString;
                if(!myDataSetAll.isEmpty()){
                    dataIdsString = myDataSetAll.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                }else{
                    dataIdsString = "";
                }
                profileSetToServer.putExtra("dataIds", dataIdsString);
                SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if(!localSharedPreferences.getString("pref_notify_repl", "1").equals("3")) {
                    profileSetToServer.putExtra("modules", "1,2");
                }else if(localSharedPreferences.getString("pref_notify_repl", "1").equals("3")){
                    profileSetToServer.putExtra("modules", "2");
                }
            }
            this.startService(profileSetToServer);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            Fragment fragmentWebView = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            //Obsluga klikanie wstecz w internetowym widoku planu lekcji
            if (fragmentWebView instanceof ScheduleFragment) {
                boolean goback = false;
                try {
                    goback = ((ScheduleFragment) fragmentWebView).canGoBackWebView();
                }catch(IOException e){
                    e.printStackTrace();
                }
                if (goback) {
                    Log.i(CLASS_NAME, "onBackPressed goback");
                    ((ScheduleFragment) fragmentWebView).goBackWebView();
                    //((ScheduleFragment) fragmentWebView).setScaleWebView();
                }else {
                    Log.i(CLASS_NAME, "onBackPressed not goback");
                    super.onBackPressed();
                }
            }else{
                super.onBackPressed();
            }
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //Wstawienie do menu odnosnikow z folderu menu (lub menu-v21, itp.) z pliku activity_replacements_main.xml_main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.replacements_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        CharSequence mTitle;
        mTitle = title;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {
            rotationChanged = true;
            Log.i("On Config Change","LANDSCAPE");
        }else{
            rotationChanged = true;
            Log.i("On Config Change","PORTRAIT");
        }
    }
}