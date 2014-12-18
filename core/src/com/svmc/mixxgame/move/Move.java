package com.svmc.mixxgame.move;

import utils.interfaces.IMove;

import com.badlogic.gdx.math.Vector2;

public class Move implements IMove {
	public Vector2	velocity		= new Vector2(0, 0);
	public Vector2	position		= new Vector2(200, 200);
	public Vector2	dimesion		= new Vector2(20, 20);
	public Vector2	acceleration	= new Vector2(0, 0);
	public float	rotation;
	public boolean	active			= false;

	@Override
	public void update(float delta) {

	}

	@Override
	public void apply(Vector2 consumer) {

	}
	

	@Override
	public float getRotation(){
		return rotation;
	}

	@Override
	public void active() {
		active = true;
	}

	@Override
	public void deActive() {
		active = false;
	}

	@Override
	public boolean isActive() {
		return active;
	}

}
