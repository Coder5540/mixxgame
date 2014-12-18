package com.svmc.mixxgame.android;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import utils.factory.StringSystem;
import utils.screen.GameCore;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.Builder;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.svmc.mixxgame.FacebookConnector.OnLoginListener;
import com.svmc.mixxgame.screens.FlashScreen;

public class MixxGameAndroid extends AndroidApplication {
	HashMap<String, String>	facebookInfo	= null;
	String					name			= "";
	UiLifecycleHelper		uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		getAppKeyHash();
		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);

		GameCore game = new GameCore() {
			@Override
			public void create() {
				super.create();
				setScreen(new FlashScreen(this));
			}
		};
		game.setFacebookConnector(new AndroidFBConnector(this));
		initialize(game, config);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		 // Logs 'install' and 'app activate' App Events.
		  AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
		// Logs 'app deactivate' App Event.
		  AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onDestroy() {
		android.os.Process.killProcess(android.os.Process.myPid());
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Session.getActiveSession() != null) {
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
			uiHelper.onActivityResult(requestCode, resultCode, data,
					new FacebookDialog.Callback() {
						@Override
						public void onError(
								FacebookDialog.PendingCall pendingCall,
								Exception error, Bundle data) {
							Log.e("Activity", String.format("Error: %s",
									error.toString()));
						}

						@Override
						public void onComplete(
								FacebookDialog.PendingCall pendingCall,
								Bundle data) {
							Log.i("Activity", "Success!");
						}
					});
		}
	}

	void getAppKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e("Hash key", something);
			}
		} catch (NameNotFoundException e1) {
			Log.e("name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("exception", e.toString());
		}
	}

	public void performFacebookLogin(final OnLoginListener listener) {
		Log.e("FACEBOOK", "performFacebookLogin");
		if (Session.getActiveSession() != null
				&& Session.getActiveSession().isOpened()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Request.newMeRequest(Session.getActiveSession(),
							new GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										String facebookID = user.getId();
										String firstName = user.getFirstName();
										String lastName = user.getLastName();
										String birthday = user.getBirthday();
										String email = "";
										try {
											email = user.getInnerJSONObject()
													.getString("email");
										} catch (JSONException e) {
											e.printStackTrace();
										}
										name = user.getName();
										if (facebookInfo == null)
											facebookInfo = new HashMap<String, String>();
										facebookInfo.put("id", facebookID);
										facebookInfo.put("first_name",
												firstName);
										facebookInfo.put("last_name", lastName);
										facebookInfo.put("name", name);
										facebookInfo
												.put("avatar",
														"https://graph.facebook.com/"
																+ facebookID
																+ "/picture?width=100&height=100");
										facebookInfo.put("access_token",
												Session.getActiveSession()
														.getAccessToken());
										facebookInfo.put(
												StringSystem._BIRTHDAY,
												birthday);
										facebookInfo.put(StringSystem._EMAIL,
												email);
										loginFBSuccessHandler
												.sendEmptyMessage(0);
										listener.onComplete(facebookInfo);
									} else {
										listener.onError();
									}
								}
							}).executeAsync();
				}
			});
		} else {
			loginFBHandler.sendEmptyMessage(0);
			boolean allowUI = false;
			Session session = new Builder(this).build();
			if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
				allowUI = false;
			} else {
				allowUI = true;
			}

			List<String> permissions = new ArrayList<String>();
			permissions.add("email");
			permissions.add("user_birthday");

			Session.openActiveSession(this, allowUI, permissions,
					new Session.StatusCallback() {
						@Override
						public void call(final Session session,
								SessionState state, Exception exception) {
							if (exception != null) {
								listener.onError();
							}
							if (session.isOpened()) {
								Request.newMeRequest(session,
										new GraphUserCallback() {
											@Override
											public void onCompleted(
													GraphUser user,
													Response response) {
												if (user != null) {
													String facebookID = user
															.getId();
													String firstName = user
															.getFirstName();
													String lastName = user
															.getLastName();
													String birthday = user
															.getBirthday();
													String email = "";
													try {
														email = user
																.getInnerJSONObject()
																.getString(
																		"email");
													} catch (JSONException e) {
														e.printStackTrace();
													}

													name = user.getName();
													if (facebookInfo == null)
														facebookInfo = new HashMap<String, String>();
													facebookInfo.put("id",
															facebookID);
													facebookInfo.put(
															"first_name",
															firstName);
													facebookInfo.put(
															"last_name",
															lastName);
													facebookInfo.put("name",
															name);
													facebookInfo
															.put("avatar",
																	"https://graph.facebook.com/"
																			+ facebookID
																			+ "/picture?width=100&height=100");
													facebookInfo
															.put("access_token",
																	session.getAccessToken());
													facebookInfo
															.put(StringSystem._BIRTHDAY,
																	birthday);
													facebookInfo
															.put(StringSystem._EMAIL,
																	email);

													Log.e("Facebook Info : ",
															"Email : "
																	+ email
																	+ "    Birthday : "
																	+ birthday);
													Log.e("Debug User : ",
															user.getInnerJSONObject()
																	.toString());
													Log.e("Debug Response : ",
															response.getRawResponse());

													loginFBSuccessHandler
															.sendEmptyMessage(0);
													listener.onComplete(facebookInfo);
												} else {
													listener.onError();
												}
											}
										}).executeAsync();
							}
						}
					});
		}
	}

	Handler	loginFBHandler			= new Handler() {

										@Override
										public void handleMessage(Message msg) {
											Toast.makeText(
													MixxGameAndroid.this,
													"Đăng nhập tài khoản facebook...",
													Toast.LENGTH_LONG).show();
										}

									};

	Handler	loginFBSuccessHandler	= new Handler() {

										public void handleMessage(Message msg) {
											Toast.makeText(
													MixxGameAndroid.this,
													"Đăng nhập thành công, chào mừng "
															+ name + "!",
													Toast.LENGTH_SHORT).show();
										}

									};
									public void shareImage(FileHandle fileHandle) {
										Intent share = new Intent(Intent.ACTION_SEND);

										share.setType("image/*");

										File imageFileToShare = fileHandle.file();

										Uri uri = Uri.fromFile(imageFileToShare);
										share.putExtra(Intent.EXTRA_STREAM, uri);
										share.putExtra(
												"android.intent.extra.TEXT",
												"I have got a new archievement ! "
														+ "https://play.google.com/store/apps/details?id=com.bgate.escaptain");

										startActivity(Intent.createChooser(share, "Share!"));
									}
									
	public void share(final String url, final String n, final String des) {
		boolean allowUI = false;
		Session session = new Builder(this).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())) {
			allowUI = false;
		} else {
			allowUI = true;
		}
		Session.openActiveSession(this, allowUI, new Session.StatusCallback() {
			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {
				if (exception != null) {
				}
				if (session.isOpened()) {
					String link = "https://play.google.com/store/apps/details?id=com.aia.hichef";

					// Define the other parameters
					String name = des;
					String caption = "MixxGame";
					String description = n;
					String picture = url;
					if (FacebookDialog.canPresentShareDialog(
							MixxGameAndroid.this,
							FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
						// Create the Native Share dialog
						FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
								MixxGameAndroid.this).setLink(link)
								.setName(name).setCaption(caption)
								.setPicture(picture)
								.setDescription(description).build();

						// Show the Native Share dialog
						uiHelper.trackPendingDialogCall(shareDialog.present());
					} else {
						// Prepare the web dialog parameters
						Bundle params = new Bundle();
						params.putString("link", link);
						params.putString("name", caption);
						params.putString("caption", caption);
						params.putString("description", description);
						params.putString("picture", picture);
						// Show FBDialog without a notification bar
						showDialogWithoutNotificationBar("feed", params);
					}
				}
			}
		});
	}

	WebDialog	dialog			= null;
	String		dialogAction	= null;
	Bundle		dialogParams	= null;

	private void showDialogWithoutNotificationBar(String action, Bundle params) {
		// Create the dialog
		dialog = new WebDialog.Builder(MixxGameAndroid.this,
				Session.getActiveSession(), action, params)
				.setOnCompleteListener(new WebDialog.OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null
								&& !(error instanceof FacebookOperationCanceledException)) {
						}
						dialog = null;
						dialogAction = null;
						dialogParams = null;
					}
				}).build();

		// Hide the notification bar and resize to full screen
		Window dialog_window = dialog.getWindow();
		dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Store the dialog information in attributes
		dialogAction = action;
		dialogParams = params;
		// Show the dialog
		dialog.show();
	}



}
