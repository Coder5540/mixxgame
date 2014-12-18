package com.svmc.mixxgame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Laser {

	public Vector2	position	= new Vector2(100, 100);
	public float	distance	= 400;
	public Color	color		= new Color(0 / 255f, 0 / 255f, 220 / 255f, 1f);
	public Color	rayColor	= new Color(Color.WHITE);
	public float	degrees		= -90;
	public Image	begin1, begin2, mid1, mid2, end1, end2;
	float			time;

	public Laser() {
		Texture texLaserS1 = new Texture(
				Gdx.files.internal("data/beamstart1.png"));
		Texture texLaserS2 = new Texture(
				Gdx.files.internal("data/beamstart2.png"));
		Texture texLaserM1 = new Texture(
				Gdx.files.internal("data/beammid1.png"));
		Texture texLaserM2 = new Texture(
				Gdx.files.internal("data/beammid2.png"));
		Texture texLaserE1 = new Texture(
				Gdx.files.internal("data/beamend1.png"));
		Texture texLaserE2 = new Texture(
				Gdx.files.internal("data/beamend2.png"));

		begin1 = new Image(texLaserS1);
		begin2 = new Image(texLaserS2);
		mid1 = new Image(texLaserM1);
		mid2 = new Image(texLaserM2);
		end1 = new Image(texLaserE1);
		end2 = new Image(texLaserE2);

	}

	public void render(SpriteBatch batch, float delta) {
		time += delta;
		distance = distance + 10 * time;
		if (distance > 400) {
			distance = 3;
			time = 0;
		}
		begin1.setColor(color);
		begin2.setColor(rayColor);
		mid1.setColor(color);
		mid2.setColor(rayColor);
		end1.setColor(color);
		end2.setColor(rayColor);

		mid1.setSize(mid1.getWidth(), distance);
		mid2.setSize(mid1.getWidth(), distance);

		begin1.setPosition(position.x, position.y);
		begin2.setPosition(position.x, position.y);

		mid1.setPosition(begin1.getX(), begin1.getY() + begin1.getHeight());
		mid2.setPosition(begin1.getX(), begin1.getY() + begin1.getHeight());

		end1.setPosition(begin1.getX(), begin1.getY() + begin1.getHeight()
				+ mid1.getHeight());
		end2.setPosition(begin1.getX(), begin1.getY() + begin1.getHeight()
				+ mid1.getHeight());

		begin1.setOrigin(begin1.getWidth() / 2, 0);
		begin2.setOrigin(begin1.getWidth() / 2, 0);

		mid1.setOrigin(mid1.getWidth() / 2, -begin1.getHeight());
		mid2.setOrigin(mid2.getWidth() / 2, -begin1.getHeight());
		end1.setOrigin(mid1.getWidth() / 2,
				-begin1.getHeight() - mid1.getHeight());
		end2.setOrigin(mid2.getWidth() / 2,
				-begin1.getHeight() - mid2.getHeight());

		begin1.setRotation(degrees);
		begin2.setRotation(degrees);
		mid1.setRotation(degrees);
		mid2.setRotation(degrees);
		end1.setRotation(degrees);
		end2.setRotation(degrees);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		begin1.draw(batch, 1f);
		begin2.draw(batch, 1f);
		mid1.draw(batch, 1f);
		mid2.draw(batch, 1f);
		end1.draw(batch, 1f);
		end2.draw(batch, 1f);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}
