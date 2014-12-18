package com.svmc.mixxgame;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;

public interface FacebookConnector {

	public void login(OnLoginListener listener);

	public void logout(OnLogoutListener listener);

	public void like(String link, OnActionListener listener);

	public void share(String link, OnActionListener listener);

	public void rate(String link, OnActionListener listener);

	public void download(String link, OnActionListener listener);

	public void restorePreviousSession(OnLoginListener listener);

	public interface OnLoginListener {
		public void onComplete(Map<String, String> userInfo);

		public void onError();
	}

	public interface OnLogoutListener {
		public void onComplete();

		public void onError();
	}

	public interface OnActionListener {
		public void onComplete();

		public void onError();
	}

	void share(FileHandle fileHandle);

	void share(String url, String name, String des);
}
