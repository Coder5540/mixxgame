package com.svmc.mixxgame.screens;

import utils.listener.OnCompleteListener;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.svmc.mixxgame.attribute.Constants;

public class CameraHandler {

	private OrthographicCamera	camera;
	private Actor				actorPosition;
	private Actor				actorTime;
	private boolean				isDone				= false;

	float						flingTime			= 0.1f;
	float						flingTimer			= 0;
	float						amountX				= 0, amountY = 0,
			velocityX = 0, velocityY = 0;
	private boolean				isIntroducingPlayer	= false;
	private boolean				waitTouch			= false;
	private boolean				block				= false;
	OnCompleteListener			onCompleteIntroducePlayerListener;
	public OnCompleteListener	waitTouchListener;

	// private Actor actorZoom;

	public enum CameraState {
		NORMAL, ZOOM_OUT, ZOOM_IN, RESET

	}

	private CameraState	camState	= CameraState.NORMAL;

	public CameraHandler(OrthographicCamera camera) {
		super();
		this.camera = camera;
		actorPosition = new Actor();
		actorTime = new Actor();
		// actorZoom = new Actor();
	}

	public void update(float delta) {
		actorPosition.act(delta);
		actorTime.act(delta);
		if (getCamState() == CameraState.RESET) {
			if (!isDone) {
				camera.position.set(actorPosition.getX(), actorPosition.getY(),
						0);
				camera.zoom = actorPosition.getRotation();
			}
		}
		if (getCamState() == CameraState.ZOOM_OUT) {
			if (!isDone) {
				camera.position.set(actorPosition.getX(), actorPosition.getY(),
						0);
				camera.zoom = actorPosition.getRotation();
			} else {
				float alpha = flingTimer / flingTime;
				amountX = velocityX * alpha * delta;
				amountY = velocityY * alpha * delta;
				flingTimer -= delta * 0.09f;
				if (flingTimer <= 0) {
					velocityX = 0;
					velocityY = 0;
				} else {
					camera.position.set(
							MathUtils.clamp(camera.position.x - amountX
									* camera.zoom, Constants.WIDTH_SCREEN
									* camera.zoom / 2, Constants.WIDTH_SCREEN
									* (1 - camera.zoom / 2)),
							camera.position.y = MathUtils.clamp(
									camera.position.y + amountY * camera.zoom,
									Constants.HEIGHT_SCREEN * camera.zoom / 2,
									Constants.HEIGHT_SCREEN
											* (1 - camera.zoom / 2)), 0);
				}

			}
		}

	}

	public void reset(final OnCompleteListener onResetListener) {
		setCamState(CameraState.RESET);
		actorPosition.clearActions();
		actorPosition.setPosition(camera.position.x, camera.position.y);
		// actorZoom.clearActions();
		actorPosition.setRotation(camera.zoom);
		Action actionMoveTo = Actions.moveTo(Constants.WIDTH_SCREEN / 2,
				Constants.HEIGHT_SCREEN / 2, .5f, Interpolation.exp5Out);
		Action zoomin = Actions.rotateTo(1f, 0.5f);
		Action action = Actions.parallel(actionMoveTo, zoomin);
		isDone = false;
		actorPosition.addAction(Actions.sequence(action,
				Actions.run(new Runnable() {
					@Override
					public void run() {
						setCamState(CameraState.NORMAL);
						isDone = true;
						if (onResetListener != null)
							onResetListener.onComplete();
					}
				})));
	}

	public void zoomOut(Vector2 position, float zoom,
			final OnCompleteListener oncompleteListener) {
		setCamState(CameraState.ZOOM_OUT);
		actorPosition.clearActions();
		actorPosition.setPosition(camera.position.x, camera.position.y);
		actorPosition.setRotation(camera.zoom);
		Action actionMoveTo = Actions.moveTo(position.x, position.y, .5f,
				Interpolation.exp5In);
		Action zoomin = Actions.rotateTo(zoom, 0.5f);
		Action action = Actions.parallel(actionMoveTo, zoomin);
		isDone = false;
		actorPosition.addAction(Actions.sequence(action,
				Actions.run(new Runnable() {

					@Override
					public void run() {
						isDone = true;
						if (oncompleteListener != null)
							oncompleteListener.onComplete();
					}
				})));
	}

	public CameraState getCamState() {
		return camState;
	}

	public void setCamState(CameraState camState) {
		this.camState = camState;

	}

	long	firsttime	= 0;
	long	secondtime	= 0;

	public boolean tap(float x, float y) {
		if (waitTouchListener != null) {
			if (waitTouch) {
				waitTouchListener.onComplete();
				waitTouchListener = null;
				waitTouch = false;
			}
			return true;
		}
		return false;
	}

	public void pan(float x, float y, float deltaX, float deltaY) {
		if (waitTouchListener != null)
			return;

		if (isIntroducingPlayer()) {
			if (firsttime == 0) {
				firsttime = System.currentTimeMillis();
				return;
			}
			if (secondtime == 0) {
				long time = System.currentTimeMillis();
				if (time - firsttime >= 2000) {
					secondtime = time;
					return;
				}
			}
			firsttime = 0;
			secondtime = 0;
			if (onCompleteIntroducePlayerListener == null) {
				return;
			}
			onCompleteIntroducePlayerListener.onComplete();
			onCompleteIntroducePlayerListener = null;
			return;
		}
		if (isDone&& !block)
			camera.position.set(
					MathUtils.clamp(camera.position.x - deltaX * camera.zoom,
							Constants.WIDTH_SCREEN * camera.zoom / 2,
							Constants.WIDTH_SCREEN * (1 - camera.zoom / 2)),
					camera.position.y = MathUtils.clamp(camera.position.y
							+ deltaY * camera.zoom, Constants.HEIGHT_SCREEN
							* camera.zoom / 2, Constants.HEIGHT_SCREEN
							* (1 - camera.zoom / 2)), 0);
	}

	public void fling(float velocityX, float velocityY) {
		if (waitTouchListener != null)
			return;
		if (isIntroducingPlayer())
			return;
		if (isDone && !block) {
			flingTimer = flingTime;
			this.velocityX = velocityX;
			this.velocityY = velocityY;
		}
	}

	public boolean isIntroducingPlayer() {
		return isIntroducingPlayer;
	}

	public void setIntroducingPlayer(boolean isIntroducingPlayer,
			OnCompleteListener onCompleteListener) {
		this.isIntroducingPlayer = isIntroducingPlayer;
		this.onCompleteIntroducePlayerListener = onCompleteListener;
	}

	public void waitTouch(float f, final OnCompleteListener onWaitDone) {
		this.waitTouchListener = onWaitDone;
		actorTime.clearActions();
		actorTime.addAction(Actions.sequence(Actions.delay(f),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						waitTouch = true;
					}
				})));
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}
	
}
