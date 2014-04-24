/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.cbsb.ftpserv;

import java.io.File;

import com.cbsb.ftpserv.R.string;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ConfigureActivity extends Activity implements OnClickListener {
	private EditText usernameBox;
	private EditText passwordBox;
	private EditText portNumberBox;
	private EditText chrootDirBox;
	private CheckBox wifiCheckBox;
	private CheckBox netCheckBox;
	private CheckBox awakeCheckBox;
	
	private Button saveButton;
	private Button cancelButton;
	
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String PORTNUM = "portNum";
	public final static String CHROOTDIR = "chrootDir";
	public final static String ACCEPT_WIFI = "allowWifi";
	public final static String ACCEPT_NET = "allowNet";
	public final static String STAY_AWAKE = "stayAwake";
	private static final int IDM_About = 101;
	private static final int IDM_Help = 102;
	
    protected Context activityContext = this;
	
    public ConfigureActivity() {
	}
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		
		SharedPreferences settings = null;
		// Inflate our UI from its XML layout description.
        setContentView(R.layout.configure_activity);
		
        saveButton = (Button) findViewById(R.id.config_save);
        saveButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.config_cancel);
        cancelButton.setOnClickListener(this);
        
        usernameBox = (EditText) findViewById(R.id.config_username);
        passwordBox = (EditText) findViewById(R.id.config_password);
        portNumberBox = (EditText) findViewById(R.id.config_portnum);
        chrootDirBox = (EditText) findViewById(R.id.config_chroot);
        wifiCheckBox = (CheckBox) findViewById(R.id.config_wifi_checkbox);
        netCheckBox = (CheckBox) findViewById(R.id.config_net_checkbox);
        awakeCheckBox = (CheckBox) findViewById(R.id.config_awake_checkbox);
        
		settings = getSharedPreferences(Defaults.getSettingsName(),
				Defaults.getSettingsMode());
		
		String username = settings.getString(USERNAME, "");
		String password = settings.getString(PASSWORD, "");
		int portNumber = settings.getInt(PORTNUM, Defaults.getPortNumber());
		String chroot = settings.getString(CHROOTDIR, Defaults.chrootDir);
		boolean acceptNet = settings.getBoolean(ACCEPT_NET, Defaults.acceptNet);
		boolean acceptWifi = settings.getBoolean(ACCEPT_WIFI, Defaults.acceptWifi);
		boolean stayAwake = settings.getBoolean(STAY_AWAKE, Defaults.stayAwake);
		
		
		
		// The String named chroot holds the default chroot directory. If the
		// directory doesn't actually exist in the file system, then use
		// the root directory instead.
		File chrootTest = new File(chroot); 
		if(!chrootTest.isDirectory() || !chrootTest.canRead()) {
			chroot = "/";
		}
		
		usernameBox.setText(username);
		passwordBox.setText(password);
		portNumberBox.setText(Integer.toString(portNumber));
		chrootDirBox.setText(chroot);
		wifiCheckBox.setChecked(acceptWifi);
		netCheckBox.setChecked(acceptNet);
		awakeCheckBox.setChecked(stayAwake);
		
		
		
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/Dungeon.TTF");
        TextView tv = (TextView) findViewById(R.id.riseftp_setting);
        tv.setTypeface(tf);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_About, Menu.NONE, string.about).setIcon(R.drawable.ic_menu_info_details);
    	menu.add(Menu.NONE, IDM_Help, Menu.NONE, string.help)
    	.setIcon(R.drawable.ic_menu_help);
		return super.onCreateOptionsMenu(menu);
	}
	
    DialogInterface.OnClickListener ignoreDialogListener = 
        	new DialogInterface.OnClickListener() 
        {
        	public void onClick(DialogInterface dialog, int which) {
        	}
        };

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case IDM_About:
        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cbsbdev.com/?page_id=517"));
			startActivity(browserIntent);
            return true;
        case IDM_Help:
            //
            //
           	TextView textView = new TextView(getApplicationContext());
        	textView.setText(R.string.instructions_text);
        	
        	AlertDialog dialog =  new AlertDialog.Builder(activityContext).create();
        	CharSequence instructions = getText(R.string.instructions_text);
        	dialog.setMessage(instructions);
        	dialog.setTitle(getText(R.string.instructions_label));
        	dialog.setButton(getText(R.string.ok), ignoreDialogListener);
        	dialog.show();
            return true;
        default:

            return false;}
        }

	protected void onStart() {
		super.onStart();
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	protected void onPause() {
    	super.onPause();
    }
    
    protected void onStop() {
    	super.onStop();
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    }

	public void onClick(View v) {
		// This function is called when the user clicks "save."
		
		if(v == cancelButton) {
			finish();
			return;
		}
		// Let's validate all the input fields.
		String errString = null;
		int portNum = 0;
		String username = usernameBox.getText().toString();
		String password = passwordBox.getText().toString();
		String portNumberString = portNumberBox.getText().toString();
		String chrootDir = chrootDirBox.getText().toString();
		boolean acceptWifi = wifiCheckBox.isChecked();
		boolean acceptNet = netCheckBox.isChecked();
		boolean stayAwake = awakeCheckBox.isChecked();
		
		validateBlock: {
			if(!username.matches("[a-zA-Z0-9]+")) {
				errString = getString(R.string.username_validation_error);
				break validateBlock;
			}
			if (!password.matches("[a-zA-Z0-9]+")){
				errString = getString(R.string.password_validation_error); 
				break validateBlock;
			}

			try {
				portNum = Integer.parseInt(portNumberString);
			} catch (Exception e) {
				portNum = 0;
			}
			if(portNum <= 0 || portNum > 65535) {
				errString = getString(R.string.port_validation_error);
				break validateBlock;
			}
			
			if(!(new File(chrootDir).isDirectory())) {
				errString = getString(R.string.chrootDir_validation_error);
				break validateBlock;
			}
			// At least one of the wifi/net listeners must be enabled,
			// otherwise there's nothing for the server to do.
			if(!acceptNet && !acceptWifi) {
				errString = getString(R.string.at_least_one_listener);
				break validateBlock;
			}
		}
		if(errString != null) {
			AlertDialog dialog =  new AlertDialog.Builder(this).create();
        	dialog.setMessage(errString);
        	dialog.setTitle(getText(R.string.instructions_label));
        	// This whole mess just adds a do-nothing "OK" button to the dialog
        	dialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        				return;
        		} });
        	dialog.show();
        	return;
		}
		
		// Validation was successful, save the settings object
		SharedPreferences settings = getSharedPreferences(
				Defaults.getSettingsName(), Defaults.getSettingsMode());
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(USERNAME, username);
		editor.putString(PASSWORD, password);
		editor.putInt(PORTNUM, portNum);
		editor.putString(CHROOTDIR, chrootDir);
		editor.putBoolean(ACCEPT_WIFI, acceptWifi );
		editor.putBoolean(ACCEPT_NET, acceptNet);
		editor.putBoolean(STAY_AWAKE, stayAwake);
		editor.commit();
		
		finish();  // close this Activity
	}
}
