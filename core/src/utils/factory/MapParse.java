package utils.factory;

import com.badlogic.gdx.maps.MapObject;
import com.svmc.mixxgame.Level;
import com.svmc.mixxgame.attribute.R;

public class MapParse {
	public static MapParse	instance	= new MapParse();

	private MapParse() {
	}

	public String getName(MapObject object) {
		if (!object.getProperties().containsKey(R.NAME))
			return Level.DEFAULT_NAME;
		String name = (String) object.getProperties().get(R.NAME);
		return name;
	}
	
}
