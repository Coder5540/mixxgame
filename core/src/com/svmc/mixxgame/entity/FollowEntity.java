package com.svmc.mixxgame.entity;

import utils.factory.Factory;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svmc.mixxgame.attribute.Constants;
import com.svmc.mixxgame.attribute.Direct;

public class FollowEntity extends Entity {

	public ParticleEffect	effect;
	private MainPlayer		mainPlayer;
	Vector2					initPosition;

	public enum FollowType {
		RED, BLUE
	}

	private FollowType		type	= FollowType.RED;
	public OnCompleteListener	onReset;


	public FollowEntity(FollowType type) {
		super();
		this.type = type;
		if (type == FollowType.RED) {
			initPosition = new Vector2(60, 60);
		}
		if (type == FollowType.BLUE) {
			initPosition = new Vector2(Constants.WIDTH_SCREEN - 60,
					Constants.HEIGHT_SCREEN - 60);
		}
		this.position = new Vector2(initPosition);
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("effects/ballpink"),
				Gdx.files.internal("effects"));
		effect.setPosition(400, 20);
		for (int i = 0; i < effect.getEmitters().size; i++) {
			effect.getEmitters().get(i).setContinuous(true);
		}
		effect.start();

		dimesion.set(36, 36);
	}

	public void update(float delta) {
		if (getState() == State.LIVE) {
			if (mainPlayer != null) {
				if (type == FollowType.RED) {
					position.lerp(Factory.getPosition(mainPlayer.getRedBound(),
							Direct.MIDDLE), 0.005f);
				}
				if (type == FollowType.BLUE) {
					position.lerp(Factory.getPosition(
							mainPlayer.getBlueBound(), Direct.MIDDLE), 0.005f);
				}
			}
		}
		if (getState() == State.BLOCK) {
			if (onReset != null) {
				position.lerp(initPosition, 0.1f);
				if (position.epsilonEquals(initPosition, 1)) {
					onReset.onComplete();
					setState(State.LIVE);
					onReset = null;
				}
			}
		}
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		effect.setPosition(position.x, position.y);
		effect.draw(batch, delta);
	}

	public void registerPlayer(MainPlayer player) {
		this.mainPlayer = player;
	}

	public Rectangle getRectangle() {
		return new Rectangle(position.x - dimesion.x / 2, position.y
				- dimesion.y / 2, dimesion.x, dimesion.y);
	}

	public void setType(FollowType type) {
		this.type = type;
	}

	public void reset(OnCompleteListener onDoneListener) {
		setState(State.BLOCK);
		this.onReset = onDoneListener;
	}

}
