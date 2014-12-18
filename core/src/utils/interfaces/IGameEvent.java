package utils.interfaces;

import com.svmc.mixxgame.attribute.EventType;

public interface IGameEvent {
	public void broadcastEvent(EventType type, float x, float y);
}
