package utils.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface IMove {

	public void update(float delta);

	public void apply(Vector2 position);

	public float getRotation();

	public void active();

	public void deActive();

	public boolean isActive();
}
