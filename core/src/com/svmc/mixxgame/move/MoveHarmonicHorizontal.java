package com.svmc.mixxgame.move;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.svmc.mixxgame.entity.IMoveTo;

public class MoveHarmonicHorizontal extends Move {
	float	time	= 1;
	Vector2	minTarget, maxTarget;
	IMoveTo	iMove;

	public MoveHarmonicHorizontal(boolean reserve, float time, float minX,
			float maxX, Vector2 iniPosition) {
		super();
		this.time = time;
		this.position.set(iniPosition);
		if (reserve) {
			maxTarget = new Vector2(iniPosition.x + minX, iniPosition.y);
			minTarget = new Vector2(iniPosition.x + maxX, iniPosition.y);
		} else {
			minTarget = new Vector2(iniPosition.x + minX, iniPosition.y);
			maxTarget = new Vector2(iniPosition.x + maxX, iniPosition.y);
		}
		iMove = new IMoveTo(minTarget, maxTarget, time, Interpolation.linear);
		iMove.setAutoReverse(true);
	}

	public MoveHarmonicHorizontal(boolean reserve, float time, Vector2 min,
			Vector2 max, Vector2 iniPosition) {
		super();
		this.time = time;
		this.position.set(iniPosition);
		if (!reserve) {
			minTarget = new Vector2(min);
			maxTarget = new Vector2(max);
		} else {
			minTarget = new Vector2(max);
			maxTarget = new Vector2(min);
		}

		iMove = new IMoveTo(minTarget, maxTarget, time, Interpolation.linear);
		iMove.setAutoReverse(true);
	}

	@Override
	public void update(float delta) {
		if (isActive()) {
			iMove.act(delta);
			position.set(iMove.point);
		}
	}

	public void active() {
		this.active = true;
	}

	public void deActive() {
		this.active = false;
	}

	@Override
	public void apply(Vector2 target) {
		target.set(position);
	}

	@Override
	public boolean isActive() {
		return active;
	}
}
