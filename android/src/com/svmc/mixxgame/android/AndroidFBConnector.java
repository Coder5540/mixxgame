package com.svmc.mixxgame.android;


import android.app.Activity;

import com.badlogic.gdx.files.FileHandle;
import com.svmc.mixxgame.FacebookConnector;

public class AndroidFBConnector implements FacebookConnector {
	private Activity activity;

	public AndroidFBConnector(Activity main) {
		activity = main;
	}

	@Override
	public void login(OnLoginListener listener) {
		((MixxGameAndroid) activity).performFacebookLogin(listener);
	}

	@Override
	public void logout(OnLogoutListener listener) {
	}

	@Override
	public void restorePreviousSession(OnLoginListener listener) {

	}

	@Override
	public void like(String link, OnActionListener listener) {
		// ((DairlyAssistantAndroid) activity).likeFacebook(link, listener);
	}

	@Override
	public void share(String link, OnActionListener listener) {
		// ((DairlyAssistantAndroid) activity).shareFacebook(link, listener);
	}

	@Override
	public void rate(String link, OnActionListener listener) {
		// ((MainActivity) activity).rateApp(link, listener);
	}

	@Override
	public void download(String link, OnActionListener listener) {
		// ((MainActivity) activity).downloadApp(link, listener);
	}

	@Override
	public void share(FileHandle fileHandle) {
		((MixxGameAndroid) activity).shareImage(fileHandle);
	}

	@Override
	public void share(String url, String name, String des) {
		((MixxGameAndroid) activity).share(url, name, des);
	}
}
