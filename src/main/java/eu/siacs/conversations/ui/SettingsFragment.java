package eu.siacs.conversations.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import eu.siacs.conversations.Config;
import eu.siacs.conversations.R;

public class SettingsFragment extends PreferenceFragmentCompat {

	private static final int REQUEST_CODE_NOTIFICATION_RINGTONE = 1;
	private static final String KEY_NOTIFICATION_RINGTONE = "notification_ringtone";

	/*
	//http://stackoverflow.com/questions/16374820/action-bar-home-button-not-functional-with-nested-preferencescreen/16800527#16800527
	private void initializeActionBar(PreferenceScreen preferenceScreen) {
		final Dialog dialog = preferenceScreen.getDialog();

		if (dialog != null) {
			View homeBtn = dialog.findViewById(android.R.id.home);

			if (homeBtn != null) {
				View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				};

				ViewParent homeBtnContainer = homeBtn.getParent();

				if (homeBtnContainer instanceof FrameLayout) {
					ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
					if (containerParent instanceof LinearLayout) {
						((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
					} else {
						((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
					}
				} else {
					homeBtn.setOnClickListener(dismissDialogClickListener);
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// Remove from standard preferences if the flag ONLY_INTERNAL_STORAGE is not true
		if (!Config.ONLY_INTERNAL_STORAGE) {
			PreferenceCategory mCategory = (PreferenceCategory) findPreference("security_options");
			Preference mPref1 = findPreference("clean_cache");
			Preference mPref2 = findPreference("clean_private_storage");
			mCategory.removePreference(mPref1);
			mCategory.removePreference(mPref2);
		}

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference instanceof PreferenceScreen) {
			initializeActionBar((PreferenceScreen) preference);
		}
		return false;
	}
	*/

	private String getRingtonePreferenceValue() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		return prefs.getString(KEY_NOTIFICATION_RINGTONE,
				"content://settings/system/notification_sound");
	};

	private void setRingtonPreferenceValue(String value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.edit().putString(KEY_NOTIFICATION_RINGTONE, value);
	};

	@Override
	public boolean onPreferenceTreeClick(Preference preference) {
		if (KEY_NOTIFICATION_RINGTONE.equals(preference.getKey())) {
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

			String existingValue = getRingtonePreferenceValue(); // TODO
			if (existingValue != null) {
				if (existingValue.length() == 0) {
					// Select "Silent"
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
				} else {
					intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
				}
			} else {
				// No ringtone has been selected, set to the default
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
			}

			startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_RINGTONE);
			return true;
		} else {
			return super.onPreferenceTreeClick(preference);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_NOTIFICATION_RINGTONE && data != null) {
			Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (ringtone != null) {
				setRingtonPreferenceValue(ringtone.toString()); // TODO
			} else {
				// "Silent" was selected
				setRingtonPreferenceValue(""); // TODO
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// Remove from standard preferences if the flag ONLY_INTERNAL_STORAGE is not true
		if (!Config.ONLY_INTERNAL_STORAGE) {
			PreferenceCategory mCategory = (PreferenceCategory) findPreference("security_options");
			Preference mPref1 = findPreference("clean_cache");
			Preference mPref2 = findPreference("clean_private_storage");
			mCategory.removePreference(mPref1);
			mCategory.removePreference(mPref2);
		}
	}
}
