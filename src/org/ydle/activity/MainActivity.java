package org.ydle.activity;

import org.ydle.R;
import org.ydle.activity.wizard.WizardActivity;
import org.ydle.layout.DashboardLayout;
import org.ydle.model.configuration.Configuration;
import org.ydle.utils.ActivityUtils;

import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = "Ydle.MainActivity";

	@InjectView(R.id.dashboard)
	DashboardLayout dashboard;

	@InjectView(R.id.btn_pieces)
	Button btnRoom;
	@InjectView(R.id.btn_events)
	Button btnEvents;
	@InjectView(R.id.btn_status)
	Button btnStatut;
	@InjectView(R.id.btn_cmds)
	Button btnCmd;

	@InjectView(R.id.btn_sarah)
	Button btnSarah;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs.registerOnSharedPreferenceChangeListener(this);

		Configuration conf = getConf();

		if (conf.firstStart) {
			startWizard();
		}

		btnRoom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent main = new Intent(MainActivity.this,
						RoomListActivity.class);
				startActivity(main);
			}
		});

		btnEvents.setActivated(false);
		btnEvents.setAlpha(0.3f);

		btnStatut.setActivated(false);
		btnStatut.setAlpha(0.3f);

		btnSarah.setText(R.string.menu_sarah);

		btnCmd.setText(R.string.menu_yana);

		onUpdate(conf);

	}

	private void startWizard() {
		Intent intent = new Intent(MainActivity.this, WizardActivity.class);
		ActivityUtils.showFisrtStartdDialog(MainActivity.this,
				getText(R.string.wizard_m_title),
				getText(R.string.wizard_m_msg), getText(R.string.ok),
				getText(R.string.cancel), intent);
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onExit");
		AlertDialog.Builder aDb = new AlertDialog.Builder(this);
		aDb.setTitle(getText(R.string.exit_msg));

		aDb.setCancelable(false);
		aDb.setPositiveButton(getText(R.string.exit),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent broadcastIntent = new Intent();
						broadcastIntent.setAction(ACTION_LOGOUT);
						sendBroadcast(broadcastIntent);
					}
				});
		aDb.setNegativeButton(getText(R.string.cancel),
				new AlertDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.cancel();
					}
				});

		aDb.create().show();

	}

	public void onUpdate(Configuration conf) {
		if (conf.yanaApp) {
			btnCmd.setOnClickListener(yanaClickListener);
			btnCmd.setActivated(true);
			btnCmd.setAlpha(1f);
		} else {
			btnCmd.setActivated(false);
			btnCmd.setAlpha(0.3f);
			btnCmd.setOnClickListener(null);
		}

		if (conf.yanaApp) {
			btnSarah.setOnClickListener(sarahClickListener);
			btnSarah.setActivated(true);
			btnSarah.setAlpha(1f);
		} else {
			btnSarah.setActivated(false);
			btnSarah.setAlpha(0.3f);
			btnSarah.setOnClickListener(null);
		}

	}

	View.OnClickListener yanaClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent yana = new Intent(Intent.ACTION_MAIN);
			// yana.setAction("fr.nover.yana.YANA");
			// yana.addCategory(Intent.CATEGORY_DEFAULT);
			yana.setComponent(new ComponentName("fr.nover.yana",
					"fr.nover.yana.Yana"));
			try {
				startActivity(yana);
			} catch (ActivityNotFoundException e) {
				Log.d(TAG, "activity inconnue : fr.nover.yana.YANA");
				ActivityUtils.showDownloadDialog(MainActivity.this,
						getText(R.string.yana_install_title),
						getText(R.string.yana_install_msg),
						getText(R.string.ok), getText(R.string.cancel),
						"fr.nover.yana");
				// Toast.makeText(MainActivity.this,
				// "Erreur de chargement de l'app Yana",
				// Toast.LENGTH_SHORT).show();
			}

		}
	};

	View.OnClickListener sarahClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent sarah = new Intent(Intent.ACTION_MAIN);
			sarah.setComponent(new ComponentName("net.android.clientsarah",
					"net.android.clientsarah.MainActivity"));
			try {
				startActivity(sarah);
			} catch (ActivityNotFoundException e) {
				Log.d(TAG,
						"activity inconnue : net.android.clientsarah.MainActivity");
				ActivityUtils.showDownloadDialog(MainActivity.this,
						getText(R.string.sarah_install_title),
						getText(R.string.sarah_install_msg),
						getText(R.string.ok), getText(R.string.cancel),
						"net.android.clientsarah");
				// Toast.makeText(MainActivity.this,
				// "Erreur de chargement de l'app S.A.R.A.H",
				// Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(TAG, "Main.onSharedPreferenceChanged : " + key);
		Configuration conf = ActivityUtils.getConf(sharedPreferences);
		onUpdate(conf);
	}

}