package com.svmc.mixxgame.entity;

import utils.interfaces.IGameEvent;
import utils.listener.OnCompleteListener;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.svmc.mixxgame.Level;
import com.svmc.mixxgame.attribute.Constants;
import com.svmc.mixxgame.attribute.EventType;
import com.svmc.mixxgame.entity.GoalController.GoalType;

public class MainPlayer extends Entity implements InputProcessor {
	public Vector2				target		= new Vector2(
													Constants.WIDTH_SCREEN / 2 - 40,
													Constants.HEIGHT_SCREEN / 2);
	public Vector2				iniposition	= new Vector2();
	private OnCompleteListener	onReset;

	public MainPlayer() {
		velocity.set(0, 100);
		acceleration.set(0, 0);
		dimesion.set(36, 36);
		position.set(100, 100);
		setState(State.FREEZE);
	}

	public void recreate() {
		target.set(Constants.WIDTH_SCREEN / 2 - 40, Constants.HEIGHT_SCREEN / 2);
		iniposition.set(target);
		position.set(100, 100);
		setState(State.FREEZE);

	}

	public void reset(OnCompleteListener onDoneListener) {
		setState(State.BLOCK);
		this.onReset = onDoneListener;
	}

	public boolean isAlive() {
		return getState() == State.LIVE;
	}

	public void update(float delta, IGameEvent event) {
		if (getState() == State.FREEZE)
			return;
		if (getState() == State.LIVE) {
			position.lerp(target, 0.04f);

			if (GoalController.getInstance().getGoalType() == GoalType.BOTH
					&& getRedBound().overlaps(getBlueBound())) {
				event.broadcastEvent(EventType.PLAYER_COLLISION,
						getRedBound().x / 2 + getRedBound().width / 4
								+ getBlueBound().x / 2 + getBlueBound().width
								/ 4, getRedBound().y / 2 + getRedBound().height
								/ 4 + getBlueBound().y / 2
								+ getBlueBound().height / 4);
			}
		}
		if (getState() == State.BLOCK) {
			target.set(iniposition);
			position.lerp(target, 0.06f);
			if (position.epsilonEquals(target, 1)) {
				if (onReset != null) {
					onReset.onComplete();
					setState(State.LIVE);
				}
			}
		}
	}

	public void updateAnimating(float delta,
			OnCompleteListener onCompleteListener) {
		setState(State.BLOCK);
		target.set(iniposition);
		position.lerp(iniposition, 0.06f);
		if (position.epsilonEquals(target, 1f)) {
			if (onCompleteListener != null) {
				onCompleteListener.onComplete();
				setState(State.LIVE);
			}
		}

	}

	@Override
	public boolean keyDown(int keycode) {

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// target.set(screenX, Constants.HEIGHT_SCREEN- screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void pan(float x, float y, float deltaX, float deltaY) {
		if (getState() != State.LIVE)
			return;
		target.x -= deltaX;
		target.y += deltaY;
	}

	public Rectangle getRedBound() {
		if (GoalController.getInstance().getGoalType() == GoalType.BLUE)
			return null;

		if (GoalController.getInstance().redDone)
			return GoalController.getInstance().getGoalred();

		if (Level.getLevel() < 8)
			return new Rectangle(position.x - dimesion.x / 2, position.y
					- dimesion.y / 2, dimesion.x, dimesion.y);

		return new Rectangle(position.x - dimesion.x / 2, position.y
				- dimesion.y / 2, dimesion.x, dimesion.y);
	}

	public Rectangle getBlueBound() {
		if (GoalController.getInstance().blueDone)
			return GoalController.getInstance().getGoalblue();
		if (Level.getLevel() < 8)
			return new Rectangle((Constants.WIDTH_SCREEN - position.x)
					- dimesion.x / 2, Constants.HEIGHT_SCREEN - position.y
					- dimesion.y / 2, dimesion.x, dimesion.y);

		return new Rectangle(
				(Constants.WIDTH_SCREEN - position.x) - dimesion.x, position.y
						- dimesion.y / 2, dimesion.x, dimesion.y);
	}

}
