package com.svmc.mixxgame.entity;

import utils.interfaces.IMove;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	public enum State {
		LIVE, DIE, BLOCK, FREEZE
//		DIE, BLOCK, FREEZE
	}

	private State	state			= State.FREEZE;
	public Vector2	velocity		= new Vector2();
	public Vector2	acceleration	= new Vector2();
	public Vector2	position		= new Vector2();
	public Vector2	dimesion		= new Vector2();
	public Vector2	target		= new Vector2();
	
	public IMove	move;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void render(SpriteBatch batch, float delta) {

	}

}
