package com.svmc.mixxgame.screens;

import utils.factory.Factory;
import utils.factory.UIUtils;
import utils.listener.OnCompleteListener;
import utils.ui.CustomGroup;
import utils.ui.Img;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.svmc.mixxgame.Assets;
import com.svmc.mixxgame.attribute.Constants;
import com.svmc.mixxgame.attribute.Direct;
import com.svmc.mixxgame.entity.GoalController;

public class TutorialHandler {
	private Stage			stage;
	Img						bg;
	Label					lbText;
	CustomGroup				container;
	Actor					actor;
	private CameraHandler	cameraHandler;
	private boolean isDoneAction= true;

	public TutorialHandler(Stage stage, CameraHandler cameraHandler) {
		super();
		this.stage = stage;
		this.cameraHandler = cameraHandler;
		createElement();
	}

	void createElement() {
		container = new CustomGroup();
		bg = new Img(new NinePatch(Assets.instance.ui.reg_ninepatch));
		bg.setColor(140 / 255f, 140 / 244f, 140 / 255f, 0.75f);
		lbText = UIUtils
				.getLabel("This is a demo Message .......", Color.WHITE);
		lbText.setWrap(true);
		lbText.setWidth(Constants.WIDTH_SCREEN - 100);
		lbText.setAlignment(Align.center, Align.center);
		container.addActor(bg);
		container.addActor(lbText);
		container.setPosition(0, -400);

		actor = new Actor();
		actor.setPosition(-100, -100);
		stage.addActor(container);
		stage.addActor(actor);
	}

	public void showMessage(String message,
			final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		lbText.setText(message);
		bg.setSize(Constants.WIDTH_SCREEN,
				lbText.getStyle().font.getWrappedBounds(message,
						Constants.WIDTH_SCREEN - 100).height + 100);
		bg.setPosition(0, 0);
		lbText.setPosition(Constants.WIDTH_SCREEN / 2, bg.getCenter().y,
				Align.center);
		container.addAction(Actions.sequence(
				Actions.moveTo(0, 10, 1f, Interpolation.swingOut),
				Actions.delay(.5f), Actions.run(new Runnable() {

					@Override
					public void run() {
						setDoneAction(true);
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));
	}

	public void showMessageNormal(final String message,
			final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		lbText.clearActions();
		lbText.addAction(Actions.sequence(
				Actions.alpha(0f, .3f, Interpolation.exp10Out),
				Actions.delay(.1f), Actions.run(new Runnable() {

					@Override
					public void run() {
						lbText.setText(message);
						bg.setPosition(0, 0);
						bg.addAction(Actions.sequence(
								Actions.sizeTo(
										Constants.WIDTH_SCREEN,
										lbText.getStyle().font
												.getWrappedBounds(
														message,
														Constants.WIDTH_SCREEN - 100).height + 100,
										0.2f, Interpolation.swingOut), Actions
										.run(new Runnable() {

											@Override
											public void run() {
												lbText.setPosition(
														Constants.WIDTH_SCREEN / 2,
														bg.getCenter().y,
														Align.center);
												lbText.addAction(Actions.sequence(
														Actions.alpha(
																1f,
																.4f,
																Interpolation.swingOut),
														Actions.run(new Runnable() {

															@Override
															public void run() {
																setDoneAction(true);
																if (onCompleteListener != null)
																	onCompleteListener
																			.onComplete();
															}
														})));
											}
										})));
					}
				})));
	}

	public void delay(float duration,
			final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		container.setIgnoreUpdate(true);
		actor.clearActions();
		actor.addAction(Actions.sequence(Actions.delay(duration),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						setDoneAction(true);
						container.setIgnoreUpdate(false);
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));
	}

	public void hideMessage(final OnCompleteListener listener) {
		setDoneAction(false);
		container.addAction(Actions.sequence(
				Actions.moveTo(0, -400, 1f, Interpolation.swingIn),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						setDoneAction(true);
						if (listener != null)
							listener.onComplete();
					}
				})));

	}

	public void introducePlayer(Vector2 position,
			final OnCompleteListener onCompleteListener) {
		cameraHandler.setBlock(true);
		float zoom = 0.6f;
		cameraHandler.zoomOut(Factory.getVisualPoint(position, zoom), zoom,
				new OnCompleteListener() {
					@Override
					public void onComplete() {
						cameraHandler.waitTouch(0.4f, new OnCompleteListener() {

							@Override
							public void onComplete() {
								showMessage("This is a fire ball ",
										new OnCompleteListener() {

											@Override
											public void onComplete() {
												cameraHandler
														.waitTouch(
																0.1f,
																new OnCompleteListener() {

																	@Override
																	public void onComplete() {
																		hideMessage(new OnCompleteListener() {

																			@Override
																			public void onComplete() {
																				showTarget(onCompleteListener);
																			}
																		});

																	}
																});
											}
										});
							}
						});
					}
				});
	}

	private void showTarget(final OnCompleteListener onCompleteListener) {
		float zoom = 0.6f;
		cameraHandler.zoomOut(
				Factory.getVisualPoint(Factory.getPosition(
						GoalController.getInstance().getGoalblue(), Direct.MIDDLE), zoom),
				zoom, new OnCompleteListener() {

					@Override
					public void onComplete() {
						showMessage(
								"This is your target, move you ball over here ",
								new OnCompleteListener() {

									@Override
									public void onComplete() {
										cameraHandler.waitTouch(0.2f,
												new OnCompleteListener() {

													@Override
													public void onComplete() {
														hideMessage(new OnCompleteListener() {

															@Override
															public void onComplete() {
																cameraHandler
																		.reset(new OnCompleteListener() {

																			@Override
																			public void onComplete() {
																				if (onCompleteListener != null)
																					onCompleteListener
																							.onComplete();
																				cameraHandler.setBlock(false);
																			}
																		});
															}
														});
													}
												});
									}
								});
					}
				});

	}

	public void introducePlayer2(Vector2 position,
			final OnCompleteListener onCompleteListener) {
		float zoom = 0.6f;
		cameraHandler.zoomOut(Factory.getVisualPoint(position, zoom), zoom,
				new OnCompleteListener() {

					@Override
					public void onComplete() {
						showMessage("This is your fire ball",
								new OnCompleteListener() {
									@Override
									public void onComplete() {
										showMessageNormal(
												"Touch and drag to move you ball",
												new OnCompleteListener() {

													@Override
													public void onComplete() {
														cameraHandler
																.setIntroducingPlayer(
																		true,
																		new OnCompleteListener() {

																			@Override
																			public void onComplete() {
																				cameraHandler
																						.setIntroducingPlayer(
																								false,
																								null);
																				hideMessage(new OnCompleteListener() {

																					@Override
																					public void onComplete() {
																						cameraHandler
																								.reset(new OnCompleteListener() {

																									@Override
																									public void onComplete() {
																										if (onCompleteListener != null)
																											onCompleteListener
																													.onComplete();
																									}
																								});
																					}
																				});

																			}
																		});

													}
												});
									}
								});
					}
				});
	}

	public boolean isDoneAction() {
		return isDoneAction;
	}

	public void setDoneAction(boolean isDoneAction) {
		this.isDoneAction = isDoneAction;
	}
	
	
}
