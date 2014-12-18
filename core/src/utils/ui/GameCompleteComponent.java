package utils.ui;

import utils.listener.OnClickListener;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.svmc.mixxgame.Assets;
import com.svmc.mixxgame.Level;
import com.svmc.mixxgame.attribute.Constants;

public class GameCompleteComponent {
	private Stage		stage;
	private Group		container;
	private ImgLevel	imgLevel;
	private Img			btnMenu;
	private Img			btnNext;
	private Img			btnReplay;
	private Img			btnFacebook;
	private Img			btnGooglePlus;
	private Img			transparent;

	private OnClickListener	onMenuClick, onNextClick, onReplayClick,
			onGoogleClick, onFacebookClick;

	public GameCompleteComponent(Stage stage) {
		super();
		this.stage = stage;
		createComponent();
		buildPosition();
	}

	private void createComponent() {
		btnMenu = new Img(Assets.instance.game.getMenuButton());
		btnNext = new Img(Assets.instance.game.getNext());
		btnReplay = new Img(Assets.instance.game.getReplay());
		btnFacebook = new Img(Assets.instance.game.getFacebookButton());
		btnGooglePlus = new Img(Assets.instance.game.getGooglePlusButton());
		transparent = new Img(new NinePatch(Assets.instance.ui.reg_ninepatch));
		transparent.setColor(Color.BLACK);

		Img bg = new Img(new Texture(
				Gdx.files.internal("Img/hex.png")));
		Img level = new Img(Assets.instance.strings.get("" + 1));
		imgLevel = new ImgLevel(bg, level);

		container = new Group();
		container.addActor(transparent);
		container.addActor(imgLevel);
		container.addActor(btnMenu);
		container.addActor(btnNext);
		container.addActor(btnReplay);
		container.addActor(btnFacebook);
		container.addActor(btnGooglePlus);
		stage.addActor(container);
	}

	public void buildPosition() {
		transparent.setOrigin(Align.center);
		transparent.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN);
		transparent.setPosition(0, 0);
		transparent.setTouchable(Touchable.disabled);

		btnFacebook.setSize(60, 60);
		btnFacebook.setOrigin(Align.center);
		btnFacebook.setPosition(20, 10);

		btnGooglePlus.setSize(60, 60);
		btnGooglePlus.setOrigin(Align.center);
		btnGooglePlus.setPosition(btnFacebook.getWidth() + btnFacebook.getX()
				+ 10, btnFacebook.getY());

		imgLevel.setSize(200, 200);
		imgLevel.setOrigin(Align.center);
		imgLevel.setPosition(Constants.WIDTH_SCREEN / 2,
				Constants.HEIGHT_SCREEN / 2, Align.center);

		btnReplay.setOrigin(Align.center);
		btnReplay.setPosition(Constants.WIDTH_SCREEN / 2 + btnReplay.getWidth()
				/ 2, Constants.HEIGHT_SCREEN / 2 - imgLevel.getHeight() / 2
				- btnReplay.getHeight() / 2 - 5, Align.center);

		btnMenu.setOrigin(Align.center);
		btnMenu.setPosition(btnReplay.getX() - btnMenu.getWidth() - 20,
				btnReplay.getY());

		btnNext.setOrigin(Align.center);
		btnNext.setPosition(btnReplay.getX() + btnReplay.getWidth() + 20,
				btnReplay.getY());
		
		reset();
	}
	
	public void createListener(){
		createListener(btnFacebook, onFacebookClick);
		createListener(btnMenu, onMenuClick);
		createListener(btnGooglePlus, onGoogleClick);
		createListener(btnReplay, onReplayClick);
		createListener(btnNext, onNextClick);
	}

	private void reset() {
		transparent.setColor(transparent.getColor().r,
				transparent.getColor().g, transparent.getColor().b, 0f);
		btnMenu.setColor(btnMenu.getColor().r, btnMenu.getColor().g,
				btnMenu.getColor().b, 0f);
		btnReplay.setColor(btnReplay.getColor().r, btnReplay.getColor().g,
				btnReplay.getColor().b, 0f);
		btnNext.setColor(btnNext.getColor().r, btnNext.getColor().g,
				btnMenu.getColor().b, 0f);
		btnGooglePlus.setColor(btnGooglePlus.getColor().r,
				btnGooglePlus.getColor().g, btnGooglePlus.getColor().b, 0f);
		imgLevel.setColor(imgLevel.getColor().r, imgLevel.getColor().g,
				imgLevel.getColor().b, 0f);

	}

	public void render(SpriteBatch batch, float delta) {
		// container.act(delta);
		// container.draw(batch, 1f);
	}

	public void show(float duration, final OnCompleteListener onCompleteListener) {
		imgLevel.setLevel(Level.getLevel());
		
		btnMenu.addAction(Actions.sequence(Actions.alpha(1f, duration),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));
		btnNext.addAction(Actions.alpha(1f, duration));
		btnReplay.addAction(Actions.alpha(1f, duration));
		btnFacebook.addAction(Actions.alpha(1f, duration));
		btnGooglePlus.addAction(Actions.alpha(1f, duration));
		imgLevel.addAction(Actions.alpha(1f, duration));

	}

	public void hide(float duration, final OnCompleteListener onCompleteListener) {
		btnMenu.addAction(Actions.sequence(Actions.alpha(0f, duration),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));
		btnNext.addAction(Actions.alpha(0f, duration));
		btnReplay.addAction(Actions.alpha(0f, duration));
		btnFacebook.addAction(Actions.alpha(0f, duration));
		btnGooglePlus.addAction(Actions.alpha(0f, duration));
		imgLevel.addAction(Actions.alpha(0f, duration));

	}

	public OnClickListener getOnMenuClick() {
		return onMenuClick;
	}

	public void setOnMenuClick(OnClickListener onMenuClick) {
		this.onMenuClick = onMenuClick;
	}

	public OnClickListener getOnNextClick() {
		return onNextClick;
	}

	public void setOnNextClick(OnClickListener onNextClick) {
		this.onNextClick = onNextClick;
	}

	public OnClickListener getOnReplayClick() {
		return onReplayClick;
	}

	public void setOnReplayClick(OnClickListener onReplayClick) {
		System.out.println("Set OnReplay Clicked");
		this.onReplayClick = onReplayClick;
	}

	public OnClickListener getOnGoogleClick() {
		return onGoogleClick;
	}

	public void setOnGoogleClick(OnClickListener onGoogleClick) {
		this.onGoogleClick = onGoogleClick;
	}

	public OnClickListener getOnFacebookClick() {
		return onFacebookClick;
	}

	public void setOnFacebookClick(OnClickListener onFacebookClick) {
		this.onFacebookClick = onFacebookClick;
	}

	private void createListener(final Img target, final OnClickListener onClick) {
		target.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				target.addAction(Actions.sequence(
						Actions.scaleTo(1.2f, 1.2f, .1f),
						Actions.scaleTo(1f, 1f, .2f, Interpolation.swingOut),
						Actions.run(new Runnable() {

							@Override
							public void run() {
								if (onClick != null) {
									System.out.println("OnClick1");
									onClick.onClick(OnClickListener.NONE);
									target.addAction(Actions.sequence(
											Actions.touchable(Touchable.disabled),
											Actions.delay(
													.1f,
													Actions.touchable(Touchable.enabled))));
								}
								System.out.println("OnClick2");
								
							}
						})));
				super.clicked(event, x, y);
			}
		});
	}
}
