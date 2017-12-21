package com.studytor.app.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

//import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.studytor.app.R;
//import com.studytor.app.sync.GcmUserRegistration;
//import com.studytor.app.sync.GcmUserUnregistration;
import com.studytor.app.interfaces.ApplicationConstants;
import com.studytor.app.sync.ProfileRegister;
import com.studytor.app.sync.ProfileSetToServer;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String CLASS_NAME = SettingsFragment.class.getName();
	private Preference pref;
	private boolean refreshCheckbox;
	private boolean notifyCheckbox;
	private String notifyRepl;
	private String notifySchedule;
	private ConnectivityManager connManager;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private Cursor cursor;
	private ArrayList<Long> myDataSetAll = new ArrayList<>();
	private String tempNotifyRepl;
	// GCM
	private static final String REG_ID = "regId";

	private void initSummary(Preference paramPreference) {
	    if ((paramPreference instanceof PreferenceCategory)) {
			PreferenceCategory localPreferenceCategory = (PreferenceCategory)paramPreference;
			for (int i = 0;; i++) {
				if (i >= localPreferenceCategory.getPreferenceCount()) {
					return;
				}
				initSummary(localPreferenceCategory.getPreference(i));
			}
	    }
	    updatePrefSummary(paramPreference);
	}
	
	private void updatePrefSummary(Preference paramPreference) {
        //Sprawdzenie czy wybrane ustawienia z ustawien sa typu ListPreference
	    if ((paramPreference instanceof ListPreference)) {
	    	ListPreference localListPreference = (ListPreference)paramPreference;
			paramPreference.setSummary(/*pref_freq_check + " " + */localListPreference.getEntry());
	    }
	    EditTextPreference localEditTextPreference;
        //Sprawdzenie czy wybrane ustawienia z ustawien sa typu EditTextPreference - na ta chwile nie uzywane
	    if ((paramPreference instanceof EditTextPreference)) {
	    	localEditTextPreference = (EditTextPreference)paramPreference;
	    	if (paramPreference.getKey().equalsIgnoreCase("editKey")) {
	    		paramPreference.setSummary("I am not going to display a password!");
	    	}

			paramPreference.setSummary(localEditTextPreference.getText());

			//Wyswietlenie w opisie tokenu FCM
			Log.i(CLASS_NAME, "PREF 3");
			if ((paramPreference.getKey().equals("pref_fcm_token"))) {
				Log.i(CLASS_NAME, "PREF 4");
				String FCMToken = FirebaseInstanceId.getInstance().getToken();
				((EditTextPreference) paramPreference).setText(FCMToken);
				paramPreference.setSummary(FCMToken);
			}
		} else {
			if(paramPreference.getKey().equals("pref_school_pref")){
				Log.i(CLASS_NAME, "PREF 5");
				SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
				String schoolName;
				switch (prefs.getInt("chosenSchool", 0)){
					case 1:
						schoolName = "Gimnazjum";
						break;
					case 2:
						schoolName = "Zespół Szkół";
						break;
					default:
						schoolName = "no school selected";
						break;
				}
				paramPreference.setSummary(schoolName);
			}
		}
	}
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		connManager = ((ConnectivityManager)getActivity().getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE));
		addPreferencesFromResource(R.xml.preferences);
//		checkPlayServices();
		SharedPreferences sharedPref = getActivity().getSharedPreferences("dane", 0);
		boolean devOptionsVisible = sharedPref.getBoolean("developerOptionsVisible",false);
		//Ukrywanie opcji deweloperskich
		if(!devOptionsVisible){
			Preference devPreference = findPreference("pref_app_dev_settings");
			getPreferenceScreen().removePreference(devPreference);
		}
		Preference prefSchoolPref = findPreference("pref_school_pref");
		for (int i = 0;; i++){
			if (i >= getPreferenceScreen().getPreferenceCount()) {
				return;
			}
			initSummary(getPreferenceScreen().getPreference(i));
		}
	}

	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
  
	public void onResume() {
		super.onResume();
		savePreferenceValueInTemp();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void savePreferenceValueInTemp(){
		SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		tempNotifyRepl = localSharedPreferences.getString("pref_notify_repl", "1");
	}
	
	public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString) {
	    pref = findPreference(paramString);
		//Sprawdzenie czy powiadomienia o nowych zastepstwach sa wlaczone i rejestracja lub wyrejestrowanie uzytkownika z GCM
		if (paramString.equals("pref_notify_switch")) {
			notifyCheckbox = paramSharedPreferences.getBoolean(paramString, false);
			String FCMToken = FirebaseInstanceId.getInstance().getToken();
			if (!notifyCheckbox) {
			    // Unregister Device from Server by IntentService

				SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);
				SharedPreferences.Editor localEditor = prefs.edit();

				if(prefs.getBoolean("toDoUnregisterUser1", false) || (prefs.getInt("chosenSchool", 1) == 1 && !prefs.getBoolean("toDoRegisterUser1", false))){
					localEditor.putBoolean("toDoUnregisterUser1", true);
					String url = ApplicationConstants.SCHOOL_SERVER_1;
					Intent profileRegister = new Intent(getActivity(), ProfileRegister.class);
					profileRegister.putExtra("serverAction", 2);
					profileRegister.putExtra("token", FCMToken);
					profileRegister.putExtra("url", url);
					getActivity().getApplicationContext().startService(profileRegister);
				}
				if(prefs.getBoolean("toDoUnregisterUser2", false) || (prefs.getInt("chosenSchool", 1) == 2 && !prefs.getBoolean("toDoRegisterUser2", false))){
					localEditor.putBoolean("toDoUnregisterUser2", true);
					String url = ApplicationConstants.SCHOOL_SERVER_2;
					Intent profileRegister = new Intent(getActivity(), ProfileRegister.class);
					profileRegister.putExtra("serverAction", 2);
					profileRegister.putExtra("token", FCMToken);
					profileRegister.putExtra("url", url);
					getActivity().getApplicationContext().startService(profileRegister);
				}
				localEditor.apply();

				Log.i("oSPC pref_notify_switch", "off");
			}else{
				// Register Device in Server by IntentService

				SharedPreferences prefs = getActivity().getSharedPreferences("dane", Context.MODE_PRIVATE);

				String url = (prefs.getInt("chosenSchool", 1) == 1)? ApplicationConstants.SCHOOL_SERVER_1 : ApplicationConstants.SCHOOL_SERVER_2;
				Intent profileRegister = new Intent(getActivity(), ProfileRegister.class);
				profileRegister.putExtra("serverAction", 1);
				profileRegister.putExtra("token", FCMToken);
				profileRegister.putExtra("url", url);
				getActivity().getApplicationContext().startService(profileRegister);

				Log.i("oSPC pref_notify_switch", "on");
			}
		}
		if ((paramString.equals("pref_notify_repl"))) {
			pref.setSummary(((ListPreference) pref).getEntry());
			notifyRepl = paramSharedPreferences.getString(paramString, "3");
			if(!notifyRepl.equals("3")) {
				// Wedlug profilu || Wszystkie
				// Run service which will change user set in server
				Intent profileSetToServer = new Intent(getActivity(), ProfileSetToServer.class);
				int serverAction = 1;
				if(tempNotifyRepl.equals("3")){
					serverAction = 3;
					String prefNotifyModules = "1,2";
//					SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//					if(localSharedPreferences.getString("pref_notify_schedule", "1").equals("2")) {
//						prefNotifyModules = "1";
//					}
					profileSetToServer.putExtra("modules", prefNotifyModules);
					SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
					localEditor.putBoolean("modulesToChange", true);
					localEditor.apply();
				}
				profileSetToServer.putExtra("serverAction", serverAction);
				if (notifyRepl.equals("1")) {
					// Wedlug profilu
					tempNotifyRepl = "1";
					// Get all selected user classes and teachers
				} else {
					// Wszystkie
					tempNotifyRepl = "2";
					myDataSetAll.clear();
				}
				String dataIdsString;
				if(!myDataSetAll.isEmpty()){
					dataIdsString = myDataSetAll.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
				}else{
					dataIdsString = "";
				}
				profileSetToServer.putExtra("dataIds", dataIdsString);
				SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
				localEditor.putBoolean("dataToChange", true);
				localEditor.apply();
				getActivity().getApplicationContext().startService(profileSetToServer);
			}else{
				// Wylaczone
				tempNotifyRepl = "3";
				// Run service which will change user set in server
				Intent profileSetToServer = new Intent(getActivity(), ProfileSetToServer.class);
				profileSetToServer.putExtra("serverAction", 2);
				String prefNotifyModules = "2";
				SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//				if(localSharedPreferences.getString("pref_notify_schedule", "1").equals("2")) {
//					prefNotifyModules = "";
//				}
				profileSetToServer.putExtra("modules", prefNotifyModules);
				SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
				localEditor.putBoolean("modulesToChange", true);
				localEditor.apply();
				getActivity().getApplicationContext().startService(profileSetToServer);
			}
		}
//		if ((paramString.equals("pref_notify_schedule"))) {
//			pref.setSummary(((ListPreference) pref).getEntry());
//			notifySchedule = paramSharedPreferences.getString(paramString, "1");
//			if(notifySchedule.equals("1")) {
//				// Wlaczone
//				// Run service which will change user set in server
//				Intent profileSetToServer = new Intent(getActivity(), ProfileSetToServer.class);
//				profileSetToServer.putExtra("serverAction", 2);
//				String prefNotifyModules = "1,2";
//				SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//				if(localSharedPreferences.getString("pref_notify_repl", "1").equals("3")) {
//					prefNotifyModules = "2";
//				}
//				profileSetToServer.putExtra("modules", prefNotifyModules);
//				SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
//				localEditor.putBoolean("modulesToChange", true);
//				localEditor.apply();
//				getActivity().getApplicationContext().startService(profileSetToServer);
//			}else{
//				// Wylaczone
//				// Run service which will change user set in server
//				Intent profileSetToServer = new Intent(getActivity(), ProfileSetToServer.class);
//				profileSetToServer.putExtra("serverAction", 2);
//				String prefNotifyModules = "1";
//				SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//				if(localSharedPreferences.getString("pref_notify_repl", "1").equals("3")) {
//					prefNotifyModules = "";
//				}
//				profileSetToServer.putExtra("modules", prefNotifyModules);
//				SharedPreferences.Editor localEditor = getActivity().getSharedPreferences("dane", 0).edit();
//				localEditor.putBoolean("modulesToChange", true);
//				localEditor.apply();
//				getActivity().getApplicationContext().startService(profileSetToServer);
//			}
//		}

		//Wyswietlenie nowego URL do listy planow
		Log.i(CLASS_NAME, "PREF 5");
		if ((paramString.equals("schedule_main_file"))) {
			Log.i(CLASS_NAME, "PREF 6");
			String prefText = ((EditTextPreference) pref).getText().trim();
			pref.setSummary(prefText);
		}
		//Wyswietlenie powrot do oryginalnego tokenu FCM po zmianie
		Log.i(CLASS_NAME, "PREF 1");
		if ((paramString.equals("pref_fcm_token"))) {
			Log.i(CLASS_NAME, "PREF 2");
			String FCMToken = FirebaseInstanceId.getInstance().getToken();
			((EditTextPreference) pref).setText(FCMToken);
			pref.setSummary(FCMToken);
		}
	}

	// Check if Google Playservices is installed in Device or not
//	private boolean checkPlayServices() {
//		GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//		int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
//		if(result != ConnectionResult.SUCCESS) {
//			if(googleAPI.isUserResolvableError(result)) {
//				Log.i(CLASS_NAME, "checkPlayServices support, not installed");
//				googleAPI.getErrorDialog(getActivity(), result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//			} else {
//				Log.i(CLASS_NAME, "checkPlayServices not support");
//				Snackbar.make(getActivity().findViewById(R.id.drawer_layout), getString(R.string.no_support_google_api), Snackbar.LENGTH_LONG).show();
//			}
//			getPreferenceScreen().findPreference("pref_notify_switch").setEnabled(false);
//			return false;
//		}else{
//			Log.i(CLASS_NAME, "checkPlayServices support");
//			//Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "This device supports Play services, App will work normally", Snackbar.LENGTH_LONG).show();
//		}
//		getPreferenceScreen().findPreference("pref_notify_switch").setEnabled(true);
//		return true;
//	}

}