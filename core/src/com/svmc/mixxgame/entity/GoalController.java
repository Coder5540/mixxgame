package com.svmc.mixxgame.entity;

import utils.listener.OnCompleteListener;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.svmc.mixxgame.entity.Entity.State;

public class GoalController {
	private static GoalController	instance		= null;
	private Rectangle				goalred, goalblue;
	public boolean					redDone			= false;
	public boolean					blueDone		= false;
	GoalEntity						goalEntityRed, goalEntityBlue;
	private MainPlayer				mainPlayer;

	private boolean					isDoneAction	= true;
	private boolean					isShow			= false;

	private GoalController() {
		super();
	}

	public static GoalController getInstance() {
		if (instance == null)
			instance = new GoalController();
		return instance;
	}

	public enum GoalType {
		RED, BLUE, BOTH
	}

	private GoalType	goalType	= GoalType.BOTH;

	public void registerMainPlayer(MainPlayer mainPlayer) {
		this.mainPlayer = mainPlayer;
	}

	public boolean isGameComplete() {
		return isBlueDone() && isRedDone();
	}

	public boolean isRedDone() {
		if (getGoalType() == GoalType.BLUE)
			return true;
		if (mainPlayer == null)
			return false;
		if (goalred == null)
			return true;
		if (mainPlayer.getState() == State.LIVE && !redDone
				&& goalred.overlaps(mainPlayer.getRedBound())) {
			redDone = true;
		}
		return redDone;
	}

	public boolean isBlueDone() {
		if (getGoalType() == GoalType.RED)
			return true;
		if (mainPlayer == null)
			return false;

		if (mainPlayer.getState() == State.LIVE && !blueDone
				&& goalblue.overlaps(mainPlayer.getBlueBound())) {
			blueDone = true;
		}
		return blueDone;
	}

	public void createGoalRed(RectangleMapObject object) {
		goalred = ((RectangleMapObject) object).getRectangle();
		goalEntityRed = new GoalEntity((RectangleMapObject) object);
	}

	public void createGoalBlue(RectangleMapObject object) {
		goalblue = ((RectangleMapObject) object).getRectangle();
		goalEntityBlue = new GoalEntity((RectangleMapObject) object);
	}

	public void render(SpriteBatch batch, float delta) {
		if (goalEntityBlue != null)

			goalEntityBlue.render(batch, delta);
		if (goalEntityRed != null)
			goalEntityRed.render(batch, delta);
	}

	public void show(float duration, final OnCompleteListener onCompleteListener) {
		if (goalEntityBlue == null && goalEntityRed == null) {
			setDoneAction(true);
			setShow(true);
			onCompleteListener.onComplete();
			return;
		}
		setDoneAction(false);
		if (goalEntityBlue == null) {
			goalEntityRed.show(duration, new OnCompleteListener() {

				@Override
				public void onComplete() {
					setDoneAction(true);
					setShow(true);
					if (onCompleteListener != null)
						onCompleteListener.onComplete();
				}
			});
			return;
		}
		if (goalEntityRed == null) {
			goalEntityBlue.show(duration, new OnCompleteListener() {

				@Override
				public void onComplete() {
					setDoneAction(true);
					setShow(true);
					if (onCompleteListener != null)
						onCompleteListener.onComplete();
				}
			});
			return;
		}

		goalEntityRed.show(duration, null);
		goalEntityBlue.show(duration, new OnCompleteListener() {

			@Override
			public void onComplete() {
				setDoneAction(true);
				setShow(true);
				if (onCompleteListener != null)
					onCompleteListener.onComplete();
			}
		});
	}

	public void hide(float duration, final OnCompleteListener onCompleteListener) {
		if (goalEntityBlue == null && goalEntityRed == null) {
			setDoneAction(true);
			setShow(false);
			onCompleteListener.onComplete();
			return;
		}
		setDoneAction(false);
		if (goalEntityBlue == null) {
			goalEntityRed.hide(duration, new OnCompleteListener() {

				@Override
				public void onComplete() {
					setDoneAction(true);
					setShow(false);
					if (onCompleteListener != null)
						onCompleteListener.onComplete();
				}
			});
			return;
		}
		if (goalEntityRed == null) {
			goalEntityBlue.hide(duration, new OnCompleteListener() {

				@Override
				public void onComplete() {
					setDoneAction(true);
					setShow(false);
					if (onCompleteListener != null)
						onCompleteListener.onComplete();
				}
			});
			return;
		}

		goalEntityRed.hide(duration, null);
		goalEntityBlue.hide(duration, new OnCompleteListener() {

			@Override
			public void onComplete() {
				setDoneAction(true);
				setShow(false);
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

	public GoalType getGoalType() {
		return goalType;
	}

	public void setGoalType(GoalType goalType) {
		this.goalType = goalType;
	}

	public void validateType() {
		if (goalred == null) {
			setGoalType(GoalType.BLUE);
			return;
		}
		if (goalblue == null) {
			setGoalType(GoalType.RED);
			return;
		}

		setGoalType(GoalType.BOTH);

	}

	public Rectangle getGoalred() {
		return goalred;
	}

	public void setGoalred(Rectangle goalred) {
		this.goalred = goalred;
	}

	public Rectangle getGoalblue() {
		return goalblue;
	}

	public void setGoalblue(Rectangle goalblue) {
		this.goalblue = goalblue;
	}

	public void reset() {
		isDoneAction = true;
		isShow = false;
		redDone = false;
		blueDone = false;
		goalred = null;
		goalblue = null;
		setGoalType(GoalType.BOTH);
	}

}
