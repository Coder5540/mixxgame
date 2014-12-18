package com.svmc.mixxgame.screens;

import utils.listener.OnClickListener;
import utils.listener.OnCompleteListener;
import utils.ui.Img;
import utils.ui.ImgLevel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.svmc.mixxgame.Assets;
import com.svmc.mixxgame.Level;
import com.svmc.mixxgame.Level.LevelNotify;
import com.svmc.mixxgame.attribute.Constants;

public class UISystem {
	private Stage		stage;
	public Image		background;
	public Image		menu;
	ImgLevel			imgLevel;
	private LabelStyle	style;
	private boolean		show			= false;
	private boolean		isDoneAction	= false;
	public boolean		menuClick		= false;
	OnClickListener		onMenuClick;

	public UISystem(Stage stage) {
		this.stage = stage;
		style = new LabelStyle();
		style.font = Assets.instance.fontFactory.getLight20();
		style.fontColor = Color.WHITE;
	}

	public void create() {
		createBackground();
		createMenuButton();
		createMenuLevel();
		buildPosition();
		buildListener();
		hide(new OnCompleteListener() {

			@Override
			public void onComplete() {
				menu.setVisible(true);
				imgLevel.setVisible(true);
			}
		});
	}

	// ===================== Initial ========================
	private void createBackground() {
		background = new Image(Assets.instance.ui.getBackground());
		background.setOrigin(Align.center);
		background.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN);
		stage.addActor(background);
	}

	private void createMenuButton() {
		menu = new Image(Assets.instance.game.getMenuButton());
		menu.setOrigin(Align.center);
		menu.setSize(60, 60);
		menu.setVisible(false);
		stage.addActor(menu);
	}

	private void createMenuLevel() {
		Img bg = new Img(Assets.instance.game.getHexTile());
		bg.setSize(66, 66);
		Img level = new Img(Assets.instance.strings.get("" + Level.getLevel()));
		imgLevel = new ImgLevel(bg, level);
		imgLevel.setVisible(false);
		stage.addActor(imgLevel);
	}

	public void updateLevel() {
		imgLevel.setLevel(Level.getLevel());
	}

	private void buildPosition() {
		float offset = 12;
		menu.setPosition(Constants.WIDTH_SCREEN - 10 - menu.getWidth(),
				Constants.HEIGHT_SCREEN - 20 - menu.getHeight());
		imgLevel.setPosition(menu.getX() - imgLevel.background.getWidth()
				- offset, menu.getY());
	}

	public void show(final OnCompleteListener listener) {
		isDoneAction = false;
		show = true;
		float offset = 12;
		imgLevel.addAction(Actions.moveTo(
				Constants.WIDTH_SCREEN - 10 - menu.getWidth()
						- imgLevel.background.getWidth() - offset,
				Constants.HEIGHT_SCREEN - 20 - menu.getHeight(), .5f,
				Interpolation.swingIn));
		menu.addAction(Actions.sequence(Actions.moveTo(Constants.WIDTH_SCREEN
				- 10 - menu.getWidth(),
				Constants.HEIGHT_SCREEN - 16 - menu.getHeight(), .5f,
				Interpolation.swingIn), Actions.run(new Runnable() {

			@Override
			public void run() {
				isDoneAction = true;
				if (listener != null)
					listener.onComplete();
			}
		})));

	}

	public void hide(final OnCompleteListener listener) {
		isDoneAction = false;
		show = false;
		imgLevel.addAction(Actions.moveBy(0, 100, .5f, Interpolation.swingIn));
		menu.addAction(Actions.sequence(
				Actions.moveBy(0, 100, .5f, Interpolation.swingIn),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						isDoneAction = true;
						if (listener != null)
							listener.onComplete();
					}
				})));
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isShowMenu() {
		return menuClick;
	}

	public void setMenuClick(boolean menuClick) {
		this.menuClick = menuClick;
	}

	public void setOnMenuClick(OnClickListener listener) {
		this.onMenuClick = listener;
	}

	public void buildListener() {
		menu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				menu.addAction(Actions.sequence(
						Actions.scaleTo(1.2f, 1.2f, .1f),
						Actions.scaleTo(1f, 1f, .1f, Interpolation.swingOut),
						Actions.run(new Runnable() {
							@Override
							public void run() {
								if (onMenuClick != null) {
									if (isShowMenu()) {
										onMenuClick
												.onClick(OnClickListener.DOWN);
										menuClick = false;

									} else {
										onMenuClick.onClick(OnClickListener.UP);
										menuClick = true;
									}
								}
							}
						})));
			}
		});
	}

	public void reset(OnCompleteListener listener) {

	}

	public LevelNotify	levelNotify	= new LevelNotify() {

										@Override
										public void notifyChange() {
											updateLevel();
										}
									};

	public boolean isDoneAction() {
		return isDoneAction;
	}

	public void setDoneAction(boolean isDoneAction) {
		this.isDoneAction = isDoneAction;
	}

}