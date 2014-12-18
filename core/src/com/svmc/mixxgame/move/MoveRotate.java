package com.svmc.mixxgame.move;


public class MoveRotate extends Move {
	float	rotate;
	float	step;

	public MoveRotate(float rotate, float step) {
		super();
		this.rotate = rotate;
		this.step = step;
	}

	@Override
	public void update(float delta) {
		if (isActive())
			rotate += step;
	}
	
	public float getRotate() {
		return rotate;
	}
}
