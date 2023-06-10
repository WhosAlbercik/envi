package whosalbercik.envi.registry;

import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.registry.obj.NPC;
import whosalbercik.envi.registry.obj.Registry;

import java.util.HashMap;

public final class NPCRegistry {


    private static final HashMap<String, NPC> values = new HashMap<String, NPC>();

    public static void load() {
        for (String npcId: ServerConfig.NPCS.get().valueMap().keySet()) {
            addNPC(npcId, (NPC) Registry.load(npcId, NPC.class, ServerConfig.NPCS));
        }

    }

    private static void addNPC(String id, NPC obj) {
        values.put(id, obj);
    }

    public static NPC getNPC(String id) {
        return values.get(id);
    }
}
