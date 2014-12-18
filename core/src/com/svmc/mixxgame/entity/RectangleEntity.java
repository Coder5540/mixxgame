package com.svmc.mixxgame.entity;

import utils.factory.Factory;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.svmc.mixxgame.Assets;
import com.svmc.mixxgame.attribute.MoveType;
import com.svmc.mixxgame.attribute.R;
import com.svmc.mixxgame.move.MoveHarmonicHorizontal;
import com.svmc.mixxgame.move.MoveRotate;

public class RectangleEntity extends Entity {
	private Image	img;
	private boolean	isDoneAction	= true;
	private boolean	isShow			= true;

	public RectangleEntity(RectangleMapObject object) {
		super();
		Color color = null;
		if (object.getProperties().containsKey("color")) {
			color = Factory.getColor((String) object.getProperties().get(
					"color"));
		} else if (object.getProperties().containsKey("color_r")) {
			color = new Color(Float.parseFloat((String) object.getProperties()
					.get("color_r")) / 255f, Float.parseFloat((String) object
					.getProperties().get("color_g")) / 255f,
					Float.parseFloat((String) object.getProperties().get(
							"color_b")) / 255f,
					Float.parseFloat((String) object.getProperties().get(
							"color_a")));
		}

		Rectangle rectangle = object.getRectangle();
		dimesion.set(rectangle.width, rectangle.height);
		position = new Vector2(rectangle.x + rectangle.width / 2, rectangle.y
				+ rectangle.height / 2);
		createUi(color);
		createMove(object);

		// reset variable
		setShow(false);
		setDoneAction(true);
		img.addAction(Actions.scaleTo(0, 0));

	}
	float degree = 0;
	public void update(float delta) {
		if (!isDoneAction)
			return;
		if (move != null) {
			move.update(delta);
			move.apply(position);
			img.setPosition(position.x, position.y, Align.center);
			if (move instanceof MoveRotate) {
				img.setRotation(move.getRotation());
			}
		}
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		img.act(delta);
		img.draw(batch, 1f);
	}

	public Rectangle getRectangle() {
		return new Rectangle(position.x - dimesion.x / 2, position.y
				- dimesion.y / 2, dimesion.x, dimesion.y);
	}

	void createUi(Color color) {
		img = new Image(Assets.instance.ui.reg_ninepatch);
		if (color != null)
			img.setColor(color);
		img.setSize(dimesion.x, dimesion.y);
		img.setPosition(position.x, position.y, Align.center);
		img.setOrigin(Align.center);
		img.addAction(Actions.visible(false));
	}

	void createMove(RectangleMapObject object) {
		MapProperties properties = object.getProperties();
		if (properties.containsKey(R.MOVE_TYPE)) {
			String moveType = (String) (properties.get(R.MOVE_TYPE));
			if (moveType.equalsIgnoreCase(MoveType.HARMONIC_X.getCode())) {
				float minX = Float.parseFloat((String) properties.get(R.MIN_X));
				float maxX = Float.parseFloat((String) properties.get(R.MAX_X));
				float minY = Float.parseFloat((String) properties.get(R.MIN_Y));
				float maxY = Float.parseFloat((String) properties.get(R.MAX_Y));

				float speed = Float
						.parseFloat((String) properties.get(R.SPEED));
				boolean isEnd = properties.containsKey("end");
				move = new MoveHarmonicHorizontal(isEnd,speed, new Vector2(position.x
						+ minX, position.y + minY), new Vector2(position.x
						+ maxX, position.y + maxY), position);
				move.active();
			}

			if (moveType.equalsIgnoreCase(MoveType.HARMONIC_Y.getCode())) {

			}
			if (moveType.equalsIgnoreCase(MoveType.ROTATE.getCode())) {
				move = new MoveRotate(0, 1);
				move.active();
			}
		}
	}

	public void show(float duration, final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		img.clearActions();
		img.addAction(Actions.sequence(
				Actions.scaleTo(1f, 1f, duration, Interpolation.swingOut),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						setShow(true);
						setDoneAction(true);
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));
	}

	public void hide(float duration, final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		img.clearActions();
		img.addAction(Actions.sequence(
				Actions.scaleTo(0f, 0f, duration, Interpolation.swingIn),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						setShow(false);
						setDoneAction(true);
						if (onCompleteListener != null)
							onCompleteListener.onComplete();
					}
				})));

	}

	public boolean isDoneAction() {
		return isDoneAction;
	}

	public void setDoneAction(boolean isDoneAction) {
		this.isDoneAction = isDoneAction;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

}
