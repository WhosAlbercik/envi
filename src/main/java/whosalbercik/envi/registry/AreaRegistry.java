package whosalbercik.envi.registry;

import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.registry.obj.Registry;
import whosalbercik.envi.registry.obj.Area;

import java.util.ArrayList;
import java.util.HashMap;

public class AreaRegistry {

    private static HashMap<String, Area> values = new HashMap<>();

    public static void load() {
        for (String questId: ServerConfig.AREAS.get().valueMap().keySet()) {
            addArea(questId, (Area) Registry.loadObject(questId, Area.class, ServerConfig.AREAS));
        }

    }


    private static void addArea(String id, Area obj) {
        AreaRegistry.values.put(id, obj);
    }

    public static Area getArea(String id) {
        return values.get(id);
    }

    public static ArrayList<Area> getAreas() {
        ArrayList<Area> Areas = new ArrayList<Area>();

        values.forEach((id, area) -> Areas.add(area));

        return Areas;
    }

}
