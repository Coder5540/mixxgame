package com.svmc.mixxgame.screens;

import utils.factory.Factory;
import utils.factory.GamePreferences;
import utils.factory.SoundManager;
import utils.factory.SoundManager.SoundName;
import utils.interfaces.IGameEvent;
import utils.listener.OnClickListener;
import utils.listener.OnCompleteListener;
import utils.screen.AbstractGameScreen;
import utils.screen.GameCore;
import utils.ui.ForceUi;
import utils.ui.GameCompleteComponent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svmc.mixxgame.Assets;
import com.svmc.mixxgame.GameSystem;
import com.svmc.mixxgame.Level;
import com.svmc.mixxgame.attribute.Constants;
import com.svmc.mixxgame.attribute.Direct;
import com.svmc.mixxgame.attribute.EventType;
import com.svmc.mixxgame.attribute.GameState;
import com.svmc.mixxgame.entity.Entity.State;
import com.svmc.mixxgame.entity.GoalController;
import com.svmc.mixxgame.entity.GoalController.GoalType;
import com.svmc.mixxgame.entity.MainPlayer;
import com.svmc.mixxgame.entity.RectangleEntity;
import com.svmc.mixxgame.screens.CameraHandler.CameraState;

public class GameScreen extends AbstractGameScreen {

	SelectLevel				selectLevel;
	ForceUi					forceUi;
	UISystem				uiSystem;
	GameSystem				gameSystem;
	long					starttime			= 0;
	long					endtime				= 0;
	boolean					isExit				= false;
	float					timeExit			= 0;

	ParticleEffect			eBlue;
	ParticleEffect			eRed;
	ParticleEffect			explosion;
	MainPlayer				mainPlayer;
	CameraHandler			cameraHandler;
	TutorialHandler			tutorialHandler;
	GameCompleteComponent	completeHandler;
	boolean					isGameComplete		= false;
	private boolean			callAnimationPlayer	= false;

	public GameScreen(GameCore game) {
		super(game);
	}

	@Override
	public void show() {
		super.show();
		createUI();
		createEffect();
		setActiveUI(true);
		gameSystem = new GameSystem();
		gameSystem.createWorld();
		gameSystem.registerMainPlayer(mainPlayer);
		cameraHandler = new CameraHandler(camera);
		tutorialHandler = new TutorialHandler(stageUI, cameraHandler);
		createListener();
		setGameState(GameState.INITIAL);
		SoundManager.getInstance().playSound(SoundName.FINISH);
		Gdx.input.setCatchBackKey(true);
	}

	void createUI() {
		Gdx.gl20.glLineWidth(2f);
		uiSystem = new UISystem(stage);
		uiSystem.create();
		Level.setLevelNotify(uiSystem.levelNotify);
		selectLevel = new SelectLevel(stage, selectLevelListener);
		forceUi = new ForceUi(stage);
		setGameState(GameState.RUNING);
	}

	void createEffect() {
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal("effects/bomb"),
				Gdx.files.internal("effects"));
		explosion.allowCompletion();

		eBlue = new ParticleEffect();
		eBlue.load(Gdx.files.internal("effects/ballblue2"),
				Gdx.files.internal("effects"));
		for (ParticleEmitter emiter : eBlue.getEmitters()) {
			emiter.setContinuous(true);
		}
		eBlue.start();
		eRed = new ParticleEffect();
		eRed.load(Gdx.files.internal("effects/ballred"),
				Gdx.files.internal("effects"));
		for (ParticleEmitter emiter : eRed.getEmitters()) {
			emiter.setContinuous(true);
		}
		eRed.start();

		mainPlayer = new MainPlayer();
		mainPlayer.iniposition.set(Constants.WIDTH_SCREEN / 2 - 40,
				Constants.HEIGHT_SCREEN / 2);
		mainPlayer.setState(State.FREEZE);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

	@Override
	public void update(float delta) {
		cameraHandler.update(delta);
		if (cameraHandler.getCamState() != CameraState.NORMAL)
			return;
		selectLevel.update(delta);

		switch (getGameState()) {
			case INITIAL:
				if (gameSystem.isDoneLineAction()
						&& gameSystem.isDoneRectangleAction()) {
					gameSystem.hideLineEntities(0.1f, null);
					gameSystem.hideRectangleEntities(0.2f,
							new OnCompleteListener() {
								@Override
								public void onComplete() {
									setGameState(GameState.ANIMATING);
									mainPlayer.setState(State.FREEZE);
								}
							});
				}
				break;
			case ANIMATING:
				showGame(new OnCompleteListener() {

					@Override
					public void onComplete() {
						callAnimationPlayer = true;
					}
				});
				if (callAnimationPlayer) {
					mainPlayer.updateAnimating(delta, new OnCompleteListener() {
						@Override
						public void onComplete() {
							callAnimationPlayer = false;
							uiSystem.show(null);
							setGameState(GameState.RUNING);
						}
					});
				}

				break;
			case RUNING:
				updateRunning(delta);
				mainPlayer.update(delta, event);
				if (gameSystem.goalController.isGameComplete())
					event.broadcastEvent(EventType.LEVEL_COMPLETE, 0, 0);

				if (GoalController.getInstance().getGoalType() == GoalType.BOTH) {
					gameSystem.updateWorld(delta, event);
				}
				if (GoalController.getInstance().getGoalType() == GoalType.RED) {
					gameSystem.updateWorldRed(delta, event);
				}
				if (GoalController.getInstance().getGoalType() == GoalType.BLUE) {
					gameSystem.updateWorldBlue(delta, event);
				}
				break;
			case PAUSE:
				selectLevel.update(delta);
				break;
			case GAME_COMPLETE:
				if (!isGameComplete) {
					gameSystem.hideLineEntities(0.2f, null);
					uiSystem.hide(null);
					gameSystem.hideRectangleEntities(0.3f,
							new OnCompleteListener() {

								@Override
								public void onComplete() {
									if (completeHandler == null)
										completeHandler = new GameCompleteComponent(
												stage);
									completeHandler.show(1f,
											new OnCompleteListener() {

												@Override
												public void onComplete() {
													System.out
															.println("SetOnClick");
													completeHandler
															.setOnNextClick(new OnClickListener() {

																@Override
																public void onClick(
																		int count) {
																	SoundManager
																			.getInstance()
																			.playSound(
																					SoundName.BUTTON_CLICK);
																	switchLevel(Level
																			.getLevel() + 1);
																}
															});

													completeHandler
															.setOnReplayClick(new OnClickListener() {

																@Override
																public void onClick(
																		int count) {
																	SoundManager
																			.getInstance()
																			.playSound(
																					SoundName.BUTTON_CLICK);
																	switchLevel(Level
																			.getLevel());
																}
															});
													completeHandler
															.setOnFacebookClick(new OnClickListener() {

																@Override
																public void onClick(
																		int count) {
																	String url = "http://1.bp.blogspot.com/-Tcb-AezraU0/Tg9thEIT_TI/AAAAAAAAEAM/RX1hwIY5LRM/s1600/mystery+box.jpg";
																	parent.getFacebookConnector()
																			.share(url,
																					"An addictive game ever. ",
																					"MixxGame");
																}
															});
													completeHandler
															.setOnMenuClick(new OnClickListener() {

																@Override
																public void onClick(
																		int count) {
																	SoundManager
																			.getInstance()
																			.playSound(
																					SoundName.BUTTON_CLICK);
																	completeHandler
																			.hide(0.2f,
																					new OnCompleteListener() {

																						@Override
																						public void onComplete() {
																							uiSystem.show(null);
																							uiSystem.setMenuClick(true);
																							selectLevel
																									.show(null);
																						}
																					});

																}
															});
													completeHandler
															.createListener();

												}
											});
								}
							});

					isGameComplete = true;
				}
				break;
			case GAME_OVER:
				break;
			default:
				break;
		}

	}

	void updateRunning(float delta) {
		checkGameOver();
	}

	void checkGameOver() {
	}

	boolean	soundBlue	= false;
	boolean	soundRed	= false;

	@Override
	public void drawBatch(SpriteBatch batch) {

		if (selectLevel.isShowing())
			return;
		gameSystem.render(batch, Gdx.graphics.getDeltaTime());

		if (GoalController.getInstance().getGoalType() != GoalType.RED) {
			if (!gameSystem.goalController.isBlueDone()) {
				eBlue.setPosition(
						mainPlayer.getBlueBound().x
								+ mainPlayer.getBlueBound().width / 2,
						mainPlayer.getBlueBound().y
								+ mainPlayer.getBlueBound().getHeight() / 2);
				if (soundBlue)
					soundBlue = false;
			} else {
				if (!soundBlue) {
					SoundManager.getInstance().playSound(SoundName.FINISH);
					soundBlue = true;
				}
				Vector2 position = new Vector2(eBlue.getEmitters().get(0)
						.getX(), eBlue.getEmitters().get(0).getY());
				Vector2 target = new Vector2();
				GoalController.getInstance().getGoalblue().getCenter(target);
				position.lerp(target, 0.1f);
				eBlue.setPosition(position.x, position.y);
			}
			eBlue.draw(batch, Gdx.graphics.getDeltaTime());
		}

		if (GoalController.getInstance().getGoalType() != GoalType.BLUE) {
			if (!gameSystem.goalController.isRedDone()) {
				eRed.setPosition(
						mainPlayer.getRedBound().x
								+ mainPlayer.getRedBound().width / 2,
						mainPlayer.getRedBound().y
								+ mainPlayer.getRedBound().getHeight() / 2);
				if (soundRed)
					soundRed = false;
			} else {
				if (!soundRed) {
					SoundManager.getInstance().playSound(SoundName.FINISH);
					soundRed = true;
				}
				Vector2 position = new Vector2(
						eRed.getEmitters().get(0).getX(), eRed.getEmitters()
								.get(0).getY());
				Vector2 target = new Vector2();
				GoalController.getInstance().getGoalred().getCenter(target);
				position.lerp(target, 0.1f);
				eRed.setPosition(position.x, position.y);
			}
			eRed.draw(batch, Gdx.graphics.getDeltaTime());
		}
		if (GoalController.getInstance().getGoalType() == GoalType.BOTH) {
			explosion.draw(batch, Gdx.graphics.getDeltaTime());
		}
	}

	@Override
	public void drawShapeFill(ShapeRenderer shapeRenderer) {
		if (selectLevel.isShowing())
			return;
		if (starttime != 0) {
			if (!forceUi.isShowing()) {
				forceUi.show(null);
			}
			float distance = (System.currentTimeMillis() - starttime) / 10f;
			float max = 420;
			if (distance > max) {
				distance = 2 * max - distance;
			}
			shapeRenderer.rect(65, Constants.HEIGHT_SCREEN - 46,
					MathUtils.clamp(distance, 0, max), 22);
		}
	}

	@Override
	public void drawShapeLine(ShapeRenderer shapeRenderer) {
		if (selectLevel.isShowing())
			return;
		shapeRenderer.setColor(Color.RED);
		if (GoalController.getInstance().getGoalred() != null)
			shapeRenderer.rect(GoalController.getInstance().getGoalred().x,
					GoalController.getInstance().getGoalred().y, GoalController
							.getInstance().getGoalred().width, GoalController
							.getInstance().getGoalred().height);

		shapeRenderer.setColor(Color.BLUE);
		if (GoalController.getInstance().getGoalblue() != null)
			shapeRenderer.rect(GoalController.getInstance().getGoalblue().x,
					GoalController.getInstance().getGoalblue().y,
					GoalController.getInstance().getGoalblue().width,
					GoalController.getInstance().getGoalblue().height);
		shapeRenderer.setColor(Color.GREEN);

		for (RectangleEntity rectEntity : gameSystem.rects) {
			if (rectEntity.isDoneAction() && rectEntity.isShow()) {
				Rectangle rect = rectEntity.getRectangle();
				shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (selectLevel.isShowing())
			return false;
		if (getGameState() == GameState.RUNING) {
			return mainPlayer.touchDown(screenX, screenY, pointer, button);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (selectLevel.isShowing())
			return false;
		endtime = System.currentTimeMillis();
		viewport.unproject(touchPoint.set(screenX, screenY));
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		if (cameraHandler.getCamState() == CameraState.ZOOM_OUT) {
			cameraHandler.fling(velocityX, velocityY);
			return true;
		}

		return super.fling(velocityX, velocityY, button);
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if (cameraHandler.getCamState() == CameraState.ZOOM_OUT) {
			cameraHandler.pan(x, y, deltaX, deltaY);
			return true;
		}
		if (selectLevel.isShowing())
			return false;

		if (getGameState() == GameState.RUNING)
			mainPlayer.pan(x, y, deltaX, deltaY);
		return super.pan(x, y, deltaX, deltaY);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
			if (selectLevel.isShowing()) {
				if (selectLevel.isDoneAction()) {
					selectLevel.hide(new OnCompleteListener() {
						@Override
						public void onComplete() {
							setGameState(GameState.RUNING);
						}
					});
					uiSystem.setMenuClick(false);
				}
				return true;
			}
			if (cameraHandler.getCamState() == CameraState.ZOOM_OUT) {
				cameraHandler.reset(null);
				return true;
			}
			if (!isExit) {
				setActiveUI(true);
				if (tutorialHandler.isDoneAction())
					isExit = true;
					tutorialHandler.showMessage("Press again to exit",
							new OnCompleteListener() {
								@Override
								public void onComplete() {
									tutorialHandler
											.hideMessage(new OnCompleteListener() {
												@Override
												public void onComplete() {
													isExit = false;
												}
											});
									
								}
							});

			} else {
				GamePreferences.getInstance().flush();
				System.exit(0);
			}
		}

		if (keycode == Keys.D) {
			if (completeHandler == null) {
				completeHandler = new GameCompleteComponent(stage);
			}
			return true;
		}

		if (keycode == Keys.SPACE) {
			if (getGameState() == GameState.RUNING)
				setGameState(GameState.PAUSE);
			else if (getGameState() == GameState.PAUSE)
				setGameState(GameState.RUNING);
		}
		if (keycode == Keys.NUM_1) {
			tutorialHandler.showMessage("This is a demo ",
					new OnCompleteListener() {

						@Override
						public void onComplete() {
							tutorialHandler
									.hideMessage(new OnCompleteListener() {
										@Override
										public void onComplete() {
											tutorialHandler
													.showMessage(
															"Try with more text , this will be fun at after all!",
															new OnCompleteListener() {

																@Override
																public void onComplete() {
																	tutorialHandler
																			.showMessageNormal(
																					"KKk, test text in the normal way",
																					new OnCompleteListener() {

																						@Override
																						public void onComplete() {
																							tutorialHandler
																									.delay(2f,
																											new OnCompleteListener() {

																												@Override
																												public void onComplete() {
																													tutorialHandler
																															.showMessageNormal(
																																	"Lol, the second text ",
																																	new OnCompleteListener() {

																																		@Override
																																		public void onComplete() {
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

		if (keycode == Keys.NUM_2) {
			if (!uiSystem.isShow())
				uiSystem.show(new OnCompleteListener() {

					@Override
					public void onComplete() {

					}
				});

			else
				uiSystem.hide(new OnCompleteListener() {

					@Override
					public void onComplete() {
					}
				});
		}
		if (keycode == Keys.NUM_3) {
			if (gameSystem.isDoneLineAction()) {
				if ((gameSystem.isShowLine())) {
					gameSystem.hideLineEntities(0.5f, new OnCompleteListener() {

						@Override
						public void onComplete() {

						}
					});
				} else {
					gameSystem.showLineEntities(0.5f, new OnCompleteListener() {

						@Override
						public void onComplete() {

						}
					});
				}
			}
		}

		if (keycode == Keys.NUM_4) {
			if (gameSystem.isDoneRectangleAction()) {
				if ((gameSystem.isShowRect())) {
					gameSystem.hideRectangleEntities(0.5f,
							new OnCompleteListener() {

								@Override
								public void onComplete() {

								}
							});
				} else {
					gameSystem.showRectangleEntities(0.5f,
							new OnCompleteListener() {

								@Override
								public void onComplete() {

								}
							});
				}
			}
		}
		if (keycode == Keys.A) {
			if (!selectLevel.isShowing())
				selectLevel.show(null);

			else
				selectLevel.hide(null);
		}
		if (keycode == Keys.S) {
			if (uiSystem.isShow())
				uiSystem.hide(null);
			else
				uiSystem.show(null);
		}
		if (keycode == Keys.D) {
			if (forceUi.isShowing())
				forceUi.hide(null);
			else
				forceUi.show(null);
		}

		return super.keyDown(keycode);
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if (cameraHandler.getCamState() == CameraState.ZOOM_OUT
				&& cameraHandler.tap(x, y)) {
			return true;
		}
		if (selectLevel.isShowing())
			return false;
		if (count >= 2 && !cameraHandler.isBlock()) {
			final Vector2 point = new Vector2();
			viewport.unproject(point.set(x, y));
			if (cameraHandler.getCamState() == CameraState.NORMAL) {
				float scale = 0.6f;
				cameraHandler.zoomOut(Factory.getVisualPoint(point, scale),
						scale, new OnCompleteListener() {

							@Override
							public void onComplete() {

							}
						});
			} else if (cameraHandler.getCamState() == CameraState.ZOOM_OUT) {
				cameraHandler.reset(new OnCompleteListener() {
					@Override
					public void onComplete() {
					}
				});
			}
		}
		return super.tap(x, y, count, button);
	}

	public boolean keyUp(int keycode) {
		return false;
	};

	public void createListener() {
		uiSystem.setOnMenuClick(onMenuClick);
	}

	OnClickListener	onMenuClick			= new OnClickListener() {

											@Override
											public void onClick(int count) {
												SoundManager
														.getInstance()
														.playSound(
																SoundName.BUTTON_CLICK);
												if (count == OnClickListener.UP) {
													if (getGameState() != GameState.ANIMATING) {
														setGameState(GameState.PAUSE);
														selectLevel.show(null);
													}
												}
												if (count == OnClickListener.DOWN) {
													if (getGameState() != GameState.ANIMATING) {
														setGameState(GameState.RUNING);
														if (isGameComplete) {
															isGameComplete = false;
															completeHandler = null;
														}
														selectLevel
																.hide(new OnCompleteListener() {
																	@Override
																	public void onComplete() {
																	}
																});
													}
												}
											}
										};

	OnClickListener	selectLevelListener	= new OnClickListener() {
											@Override
											public void onClick(int count) {
												switchLevel(count);
											}
										};
	IGameEvent		event				= new IGameEvent() {
											@Override
											public void broadcastEvent(
													EventType type, float x,
													float y) {
												if (type == EventType.PLAYER_COLLISION) {
													explosion.setPosition(x, y);
													explosion.start();
													gameSystem.reset();
													mainPlayer
															.reset(new OnCompleteListener() {

																@Override
																public void onComplete() {
																	mainPlayer
																			.setState(State.LIVE);
																	setGameState(GameState.RUNING);
																}
															});
												}
												if (type == EventType.LEVEL_COMPLETE) {
													SoundManager
															.getInstance()
															.playSound(
																	SoundName.FINISH);
													saveLevelOpen();
													setGameState(GameState.GAME_COMPLETE);
												}
												if (type == EventType.GAME_OVER) {
													if (mainPlayer.getState() != State.BLOCK)
														SoundManager
																.getInstance()
																.playSound(
																		SoundName.COLLAPSE);
													gameSystem.resetFollow();
													mainPlayer
															.reset(new OnCompleteListener() {

																@Override
																public void onComplete() {
																	mainPlayer
																			.setState(State.LIVE);
																	setGameState(GameState.RUNING);
																}
															});
												}
											}
										};

	protected void switchLevel(int count) {
		Level.setLevel(count);
		Assets.instance.assetMap.loadLevel(count);
		GoalController.getInstance().reset();
		mainPlayer.recreate();
		completeHandler = null;
		isGameComplete = false;
		starttime = 0;
		callAnimationPlayer = false;
		setActiveUI(false);
		timeExit = 0;
		isExit = false;
		createUI();
		createEffect();
		gameSystem = new GameSystem();
		gameSystem.createWorld();
		gameSystem.registerMainPlayer(mainPlayer);
		createListener();
		setGameState(GameState.INITIAL);
		setActiveUI(true);
	}

	private void showGame(final OnCompleteListener onCompleteListener) {
		if (!gameSystem.isShowLine() && !gameSystem.isShowRect()
				&& gameSystem.isDoneLineAction()
				&& gameSystem.isDoneRectangleAction()) {
			gameSystem.showLineEntities(0.5f, new OnCompleteListener() {

				@Override
				public void onComplete() {
					if (!gameSystem.isShowRect()) {
						gameSystem.showRectangleEntities(0.5f,
								new OnCompleteListener() {

									@Override
									public void onComplete() {
										if (Level.getLevel() == 0) {
											tutorialHandler.introducePlayer(
													Factory.getPosition(
															mainPlayer
																	.getBlueBound(),
															Direct.MIDDLE),
													new OnCompleteListener() {

														@Override
														public void onComplete() {
															mainPlayer
																	.reset(new OnCompleteListener() {

																		@Override
																		public void onComplete() {
																			tutorialHandler
																					.showMessage(
																							"Now, let's start you adventure. Goodluck for you!",
																							new OnCompleteListener() {

																								@Override
																								public void onComplete() {
																									tutorialHandler
																											.hideMessage(new OnCompleteListener() {

																												@Override
																												public void onComplete() {
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
										} else {
											onCompleteListener.onComplete();
										}
									}
								});
					}
				}
			});
		}
	}

	public void showTutorial(float delta, OnCompleteListener onCompleteListener) {
		mainPlayer.updateAnimating(delta, new OnCompleteListener() {
			@Override
			public void onComplete() {
				if (!gameSystem.isShowLine() && !gameSystem.isShowRect()
						&& gameSystem.isDoneLineAction()
						&& gameSystem.isDoneRectangleAction()) {
					gameSystem.showLineEntities(0.5f, new OnCompleteListener() {

						@Override
						public void onComplete() {
							if (!gameSystem.isShowRect()) {
								gameSystem.showRectangleEntities(0.5f,
										new OnCompleteListener() {

											@Override
											public void onComplete() {
												mainPlayer.setState(State.LIVE);
												if (Level.getLevel() == 0) {
													tutorialHandler.introducePlayer(
															Factory.getPosition(
																	mainPlayer
																			.getBlueBound(),
																	Direct.MIDDLE),
															new OnCompleteListener() {

																@Override
																public void onComplete() {
																	mainPlayer
																			.reset(new OnCompleteListener() {

																				@Override
																				public void onComplete() {
																					tutorialHandler
																							.showMessage(
																									"Now, let's start you adventure. Goodluck for you!",
																									new OnCompleteListener() {

																										@Override
																										public void onComplete() {
																											tutorialHandler
																													.hideMessage(new OnCompleteListener() {

																														@Override
																														public void onComplete() {
																															setGameState(GameState.RUNING);
																														}
																													});
																										}
																									});
																				}
																			});
																}
															});
												} else {
													setGameState(GameState.RUNING);
												}
											}
										});
							}
						}
					});
				}
			}
		});
	}

	public void saveLevelOpen() {
		if (Level.getLevel() == GamePreferences.getInstance().getLevelOpen()) {
			if (Level.getLevel() < Level.getMaxLevel()) {
				GamePreferences.getInstance().setLevelOpen(
						Level.getLevel() + 2, true);
				GamePreferences.getInstance().flush();
				selectLevel.updateLock(Level.getLevel() + 1);
				System.out.println("SetActive Level is called"
						+ (Level.getLevel() + 1));
			}
		}
	}
}
