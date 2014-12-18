package com.svmc.mixxgame.entity;

import utils.factory.Factory;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.svmc.mixxgame.Assets;

public class GoalEntity extends Entity {
	private boolean		isDoneAction	= true;
	private boolean		isShow			= true;
	Line				line;

	Image				img;
	private Animation	animation;
	float				time;

	public GoalEntity(RectangleMapObject object) {
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

		Array<Vector2> vertices = new Array<Vector2>();
		vertices.add(new Vector2(rectangle.x, rectangle.y));
		vertices.add(new Vector2(rectangle.x, rectangle.y + rectangle.height));
		vertices.add(new Vector2(rectangle.x + rectangle.width, rectangle.y
				+ rectangle.height));
		vertices.add(new Vector2(rectangle.x + rectangle.width, rectangle.y));

		line = new Line(vertices, color);
		line.hide();

		animation = Assets.instance.game.getAnimationBlue();
		img = new Image(animation.getKeyFrame(0));

	}

	public void update(float delta) {
		if (!isDoneAction)
			return;
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
//		img.draw(batch, 1f);
//		line.render(batch, delta);
	}

	public Rectangle getRectangle() {
		return new Rectangle(position.x - dimesion.x / 2, position.y
				- dimesion.y / 2, dimesion.x, dimesion.y);
	}

	public void show(float duration, final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		line.show(duration, new OnCompleteListener() {

			@Override
			public void onComplete() {
				setShow(true);
				setDoneAction(true);
				if (onCompleteListener != null)
					onCompleteListener.onComplete();
			}
		});
	}

	public void hide(float duration, final OnCompleteListener onCompleteListener) {
		setDoneAction(false);
		line.hide(duration, new OnCompleteListener() {

			@Override
			public void onComplete() {
				setShow(false);
				setDoneAction(true);
				if (onCompleteListener != null)
					onCompleteListener.onComplete();
			}
		});
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
