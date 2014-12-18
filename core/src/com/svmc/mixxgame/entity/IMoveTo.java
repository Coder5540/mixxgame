package com.svmc.mixxgame.entity;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class IMoveTo {

	private float			duration, time;
	private Interpolation	interpolation;
	private boolean			reverse, began, complete;

	public Vector2			startPoint	= new Vector2();
	public Vector2			endPoint	= new Vector2();
	public Vector2			point		= new Vector2();
	private boolean			autoReverse	= false;

	public IMoveTo(Vector2 startPoint, Vector2 endPoint, float duration) {
		this.duration = duration;
		restart();
		this.startPoint.set(startPoint);
		this.endPoint.set(endPoint);
		this.point.set(startPoint);
	}

	public IMoveTo(Vector2 startPoint, Vector2 endPoint, float duration,
			Interpolation interpolation) {
		this.duration = duration;
		this.interpolation = interpolation;
		restart();
		this.startPoint.set(startPoint);
		this.endPoint.set(endPoint);
		this.point.set(startPoint);
	}

	public boolean act(float delta) {
		if (complete)
			return true;
		// executing.
		if (!began) {
			begin();
			began = true;
		}
		time += delta;
		complete = time >= duration;
		float percent;
		if (complete)
			percent = 1;
		else {
			percent = time / duration;
			if (interpolation != null)
				percent = interpolation.apply(percent);
		}
		update(reverse ? 1 - percent : percent);
		if (complete) {
			end();
			if (autoReverse) {
				this.endPoint.set(startPoint);
				this.startPoint.set(point);
				restart();
			}
		}

		return complete;
	}

	public void begin() {
		point.set(startPoint.x, startPoint.y);
		time = 0;
		complete = false;
	}

	public void end() {
		point.set(endPoint.x, endPoint.y);
		time = duration;
	}

	public void update(float percent) {
		point.set(startPoint.x + (endPoint.x - startPoint.x) * percent,
				startPoint.y + (endPoint.y - startPoint.y) * percent);
	}

	public void finish() {
		time = duration;
	}

	public void restart() {
		time = 0;
		began = false;
		complete = false;
	}

	public void reset() {
		reverse = false;
		interpolation = null;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public boolean isReverse() {
		return reverse;
	}

	/** When true, the action's progress will go from 100% to 0%. */
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public boolean isCompleted() {
		return complete;
	}

	public void setAutoReverse(boolean autoReverse) {
		this.autoReverse = autoReverse;
	}
}
