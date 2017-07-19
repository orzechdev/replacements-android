package com.replacements.replacements.fragments;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.replacements.replacements.R;
import com.replacements.replacements.helpers.AppWebViewClients;
import com.replacements.replacements.helpers.HtmlJSInterface;
import com.replacements.replacements.sync.ScheduleUpdate;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


@SuppressLint("SetJavaScriptEnabled")
public class ScheduleFragment extends Fragment implements Observer {
    private static final String CLASS_NAME = ScheduleFragment.class.getName();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView webView;
    private ProgressBar mProgressBar;
    private ConnectivityManager connManager;
    private String no_internet;
    private String downloading_schedule;
    private String no_internet_no_download;
    private String refreshed_all_schedules;
    private String webViewUrl;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ScheduleFragment", "onCreateView");
        // Inflate the layout for this fragment
        View fragment_view = inflater.inflate(R.layout.fragment_schedule_old, container, false);

        no_internet = getString(R.string.no_internet_connect);
        downloading_schedule = getString(R.string.downloading_schedule);
        no_internet_no_download = getString(R.string.no_internet_no_download);
        refreshed_all_schedules = getString(R.string.refreshed_all_schedules);

        // Zaladowanie strony z planem lekcji do widoku
        webView = (WebView) fragment_view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        //webView.setInitialScale(400);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progress_bar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);//ContextCompat.getColor(getActivity(), R.color.red)
        webViewUrl = "";
        webView.setWebViewClient(new AppWebViewClients(mProgressBar) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("ScheduleFragment", "shouldOverrideUrlLoading 1");
                //saveUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
//                if (url.equals(getString(R.string.url_schedule))) {
//                    //view.setInitialScale(400);
//                } else {
//                    //view.setInitialScale(200);
//                }
                // HERE YOU GET url
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                if(getActivity() != null && !url.equals("auto:blank")) {
//
//                }
                //if(webViewUrl.equals("")) {
                //    saveUrl(url);
                //}
                view.loadUrl("javascript:(function() { " +
                        //"document.body.style.position = 'absolute';\n" +
                        "document.body.style.zoom = 1;\n" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var contentWidth = document.body.scrollWidth, \n" +
                        //"    windowWidth = window.outerWidth, \n" +
                        "    windowWidth = document.getElementsByClassName('tabtytul')[0].clientWidth, \n" +
                        "    newScale = windowWidth / contentWidth;\n" +
                        "document.body.style.zoom = parseInt(newScale * 10, 10)/10;\n" +
                        "})()");
                view.loadUrl("javascript:(function() { "
                        + "window.HTMLOUT.setHtml('<html>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</html>');})();");
                super.onPageFinished(view, url);
            }

//            @SuppressWarnings("deprecation")
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                // Handle the error
//                int currentapiVersion = Build.VERSION.SDK_INT;
//                if (currentapiVersion < Build.VERSION_CODES.M) {
//                    Log.i("ScheduleFragment", "onReceivedError <M 1 - " + Integer.toString(errorCode));
//                    if (errorCode == ERROR_UNKNOWN) {
//                        Log.i("ScheduleFragment", "onReceivedError <M 2");
//                        if (failingUrl.endsWith(".html")) {
//                            Log.i("ScheduleFragment", "onReceivedError <M 3");
//                            if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
//                                Log.i("ScheduleFragment", "onReceivedError <M 4");
//                                String newUrl = failingUrl.replace("file:///android_asset/plany", "http://www.zschocianow.pl/plan");
//                                newUrl = newUrl.replace("file:///android_asset", "http://www.zschocianow.pl/plan");
//                                webView.loadUrl(newUrl);
//                            }
//                        }
//                    }
//                }
//            }
//
//            @TargetApi(Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
//                // Redirect to deprecated method, so you can use it in all SDK versions
//                int currentapiVersion = Build.VERSION.SDK_INT;
//                if (currentapiVersion >= Build.VERSION_CODES.M) {
//                    Log.i("ScheduleFragment", "onReceivedError >M 1 - " + Integer.toString(rerr.getErrorCode()));
//                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
//                    if (rerr.getErrorCode() == ERROR_UNKNOWN) {
//                        Log.i("ScheduleFragment", "onReceivedError >M 2");
//                        if (req.getUrl().toString().endsWith(".html")) {
//                            Log.i("ScheduleFragment", "onReceivedError <M 3");
//                            if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
//                                Log.i("ScheduleFragment", "onReceivedError >M 4");
//                                String newUrl = req.getUrl().toString().replace("file:///android_asset/plany", "http://www.zschocianow.pl/plan");
//                                newUrl = newUrl.replace("file:///android_asset", "http://www.zschocianow.pl/plan");
//                                webView.loadUrl(newUrl);
//                            }
//                        }
//                    }
//                }
//            }
        });
    //    mSwipeRefreshLayout = (SwipeRefreshLayout) fragment_view.findViewById(R.id.swipe_container);
    //    mSwipeRefreshLayout.setOnRefreshListener(
    //            new SwipeRefreshLayout.OnRefreshListener() {
    //                @Override
    //                public void onRefresh() {
//                        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
//                            webView.clearCache(true);
//                            webView.clearHistory();
//                            ScheduleFragment.ClearCookies(getActivity());
//
//
//                            Log.i("ScheduleFragment", "refreshAll 1");
//
//                            String url;
//                            String lastUrl;
//
//                            SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
//                            lastUrl = prefs.getString("lastUrlSchedule", "");
//
//                            Log.i("ScheduleFragment", "refreshAll 2 url - " + lastUrl);
//
//                            if(!lastUrl.equals("")) {
//                                Log.i("ScheduleFragment", "refreshAll 3");
//
//                                url = lastUrl;
//                            }else{
//                                Log.i("ScheduleFragment", "refreshAll 4");
//
//                                webView.clearCache(true);
//                                webView.clearHistory();
//
//                                ScheduleFragment.ClearCookies(getActivity());
//
//                                // Pobranie z ustawien linku do strony z planem lekcji
//                                SharedPreferences prefsDefault = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                                url = prefsDefault.getString("schedule_url", getString(R.string.url_schedule));
//                            }
//
//                            url = url.replace("file:///android_asset/plany", "http://www.zschocianow.pl/plan");
//                            url = url.replace("file:///android_asset", "http://www.zschocianow.pl/plan");
//
//                            Log.i("ScheduleFragment","refreshAll 5 url - " + url);
//                            webView.loadUrl(url);
//
//                            mProgressBar.setVisibility(View.VISIBLE);
//                        } else {
//                            Snackbar.make(getActivity().findViewById(R.id.drawer_layout), no_internet, Snackbar.LENGTH_LONG).show();
//                        }
    //                    mSwipeRefreshLayout.setRefreshing(false);
    //                }
    //            }
    //    );
    //    mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);

        HtmlJSInterface htmlJSInterface = new HtmlJSInterface();

        htmlJSInterface.addObserver(this);
        webView.addJavascriptInterface(htmlJSInterface, "HTMLOUT");

        //TODO bellow to delete if doesn't solve problem
        if (savedInstanceState != null)
            webView.restoreState(savedInstanceState);

        return fragment_view;
    }

    //TODO bellow to delete if doesn't solve problem
    @Override
    public void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }


    // Called when our JavaScript Interface Observables are updated.
    @Override
    public void update(Observable observable, Object observation) {
        // Got full page source.
        if (observable instanceof HtmlJSInterface) {
            String html = (String) observation;
            onHtmlChanged(html);
        }
    }

    private void saveUrl(String url){
        Log.i("ScheduleFragment", "saveUrl - " + url);
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("dane", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastUrlSchedule", url);
        edit.apply();
    }

    private void onHtmlChanged(String html) {
        // Do stuff here...
        Log.i("ScheduleFragment", "onHtmlChanged");
        // Aby zapisywac ostatni url do pamieci, trzeba go zapisac nie tylko przy wybraniu nowego linku, ale tez przy cofnieciu wstecz przyciskie wstecz
        webView.post(new Runnable() {
            @Override
            public void run() {
                saveUrl(webView.getUrl());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("ScheduleFragment", "onStart");

        SharedPreferences sharedPref = getActivity().getSharedPreferences("dane", 0);
        String dirName = sharedPref.getString("scheduleFilesDirNameCurrent","");

        if (dirName.equals("")){
            connManager = ((ConnectivityManager)getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
            if((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
                if (!isMyServiceRunning(ScheduleUpdate.class)) {
                    Intent scheduleIntent = new Intent(getActivity().getBaseContext(), ScheduleUpdate.class);
                    if (!sharedPref.getBoolean("scheduleUpdateToDo", false))
                        scheduleIntent.putExtra("jsonUpdate", true);
                    getActivity().startService(scheduleIntent);
                }
                String HTMLcode = "<html><body style=\"text-align:center; background:#6986c1; font-size:18px; color:#FFFFFF; padding-top:20px\">" +
                        downloading_schedule +
                        "</body></html>";
                webView.loadDataWithBaseURL(null, HTMLcode, "text/html", "UTF-8", null);
            }else{
                String HTMLcode = "<html><body style=\"text-align:center; background:#6986c1; font-size:18px; color:#FFFFFF; padding-top:20px\">" +
                        no_internet_no_download +
                        "</body></html>";
                webView.loadDataWithBaseURL(null, HTMLcode, "text/html", "UTF-8", null);
            }
        }else {

            String urlName;
            String lastUrlName = "";

            if(webView != null) {
                Log.i("ScheduleFragment", "onStart 2");

                SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
                lastUrlName = prefs.getString("lastUrlSchedule","");
            }
            if(!lastUrlName.equals("")) {
                Log.i("ScheduleFragment", "onStart 3");

                urlName = lastUrlName;
            }else{
                Log.i("ScheduleFragment", "onStart 4");

                webView.clearCache(true);
                webView.clearHistory();

                ScheduleFragment.ClearCookies(getActivity());

                SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String mainFile = localSharedPreferences.getString("schedule_main_file","lista.html");

                urlName = "file:///" + getActivity().getApplicationContext().getFilesDir() + File.separator + dirName + File.separator + mainFile;
            }

            webView.loadUrl(urlName);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("messageSchedule"));
    }

    @Override
    public void onPause() {
        Log.i("ScheduleFragment", "onPause");
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    public boolean canGoBackWebView() throws IOException {
        String url = webView.getUrl();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("dane", 0);
        String dirScheduleName = sharedPref.getString("scheduleFilesDirNameCurrent", "");
        if (dirScheduleName.equals(""))
            return false;

        SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mainFile = localSharedPreferences.getString("schedule_main_file","lista.html");

        String urlName = "file:///" + getActivity().getApplicationContext().getFilesDir() + File.separator + dirScheduleName + File.separator + mainFile;

        Log.i(CLASS_NAME, "canGoBackWebView url: " + url);
        Log.i(CLASS_NAME, "canGoBackWebView urlName: " + urlName);

        File f1 = new File(url);
        File f2 = new File(urlName);

        String f1Path = f1.getCanonicalPath();
        String f2Path = f2.getCanonicalPath();

        Log.i(CLASS_NAME, "canGoBackWebView f1 path: " + f1Path);
        Log.i(CLASS_NAME, "canGoBackWebView f2 path: " + f2Path);

        if(f1Path.equals(f2Path)){
            Log.i(CLASS_NAME, "canGoBackWebView url the same");
        }else{
            Log.i(CLASS_NAME, "canGoBackWebView url NOT the same");
        }

        if(webView.canGoBack() && !f1Path.equals(f2Path)){
            return true;
        }else{
            return !f1Path.equals(f2Path);
        }
    }

    public void goBackWebView(){
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            loadFileToWebView("",true);
        }
    }

    //Obsluga clikniec w odnosniki w menu dla wersji Androida wiekszej lub rownej 5.0
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plans_list:
                Log.i("inne select","2");
                if(!loadFileToWebView("",true)) {
                    if (!isMyServiceRunning(ScheduleUpdate.class)) {
                        refreshAll();
                    }
                }
                return true;
            case R.id.action_refresh_all:
                Log.i("inne select","1");
                if (!isMyServiceRunning(ScheduleUpdate.class)) {
                    refreshAll();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    public static void ClearCookies(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("ScheduleFragment", "Using ClearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }else{
            Log.d("ScheduleFragment", "Using ClearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager= CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void refreshAll(){
        connManager = ((ConnectivityManager)getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if ((connManager != null) && (connManager.getActiveNetworkInfo() != null)) {
            webView.clearCache(true);
            webView.clearHistory();
            ScheduleFragment.ClearCookies(getActivity());

            SharedPreferences sharedPref = getActivity().getSharedPreferences("dane", 0);
            String dirName = sharedPref.getString("scheduleFilesDirNameCurrent","");

            if (dirName.equals("")){
                String HTMLcode = "<html><body style=\"text-align:center; background:#6986c1; font-size:18px; color:#FFFFFF; padding-top:20px\">" +
                        downloading_schedule +
                        "</body></html>";
                webView.loadDataWithBaseURL(null, HTMLcode, "text/html", "UTF-8", null);
            }else{
                Snackbar.make(getActivity().findViewById(R.id.drawer_layout), downloading_schedule, Snackbar.LENGTH_LONG).show();
            }

            Intent scheduleIntent = new Intent(getActivity().getBaseContext(), ScheduleUpdate.class);
            scheduleIntent.putExtra("jsonUpdate", true);
            getActivity().startService(scheduleIntent);

            //mProgressBar.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(getActivity().findViewById(R.id.drawer_layout), no_internet, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_refresh).setVisible(false);
    }

    private boolean loadFileToWebView(String url, boolean loadList){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("dane", 0);

        String dirScheduleName = sharedPref.getString("scheduleFilesDirNameCurrent", "");

        if (dirScheduleName.equals(""))
            return false;

        String urlName = "file:///" + getActivity().getApplicationContext().getFilesDir() + File.separator + dirScheduleName;

        if(!loadList) {

            String prefixMain = sharedPref.getString("scheduleUpdatePrefix", "no_name_file");

            String dirWithFile = url.replaceFirst(prefixMain, "");
            String[] dirFileSplit = dirWithFile.split("/");

            for (String dirOrFile : dirFileSplit) {
                urlName += File.separator + dirOrFile;
            }
        }else{
            SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String mainFile = localSharedPreferences.getString("schedule_main_file","lista.html");
            urlName += File.separator + mainFile;
        }

        webView.loadUrl(urlName);

        return true;
    }

    // TA METODA WLASCIWIE NIE DZIALA, ALE NIE SZKODZI ONA, WIEC NIECH NA RAZIE ZOSTANIE - TRZEBA JAKOS INACZEJ SPRAWDZAC CZY SERWIS ISTNIEJE (NP. BINDOWANIE SERWISU)
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(CLASS_NAME, "isMyServiceRunning true");
                return true;
            }
        }
        Log.i(CLASS_NAME, "isMyServiceRunning false");
        return false;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("scheduleUpdated")){
                if(intent.getBooleanExtra("scheduleUpdated",false))
                    loadFileToWebView("",true);
                Snackbar.make(getActivity().findViewById(R.id.drawer_layout), refreshed_all_schedules, Snackbar.LENGTH_LONG).show();
            }
            if(intent.hasExtra("networkIsOn")){
                if(intent.getBooleanExtra("networkIsOn",false))
                    if (!isMyServiceRunning(ScheduleUpdate.class))
                        refreshAll();
            }
        }
    };
}