package whosalbercik.envi.registry;

import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.obj.Quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class QuestRegistry {
    private static final HashMap<String, Quest> values = new HashMap<String, Quest>();

    public static void load() {
        for (String questId: ServerConfig.QUESTS.get().valueMap().keySet()) {
            addQuest(questId, Quest.load(questId));
        }

    }

    private static void addQuest(String id, Quest obj) {
        values.put(id, obj);
    }

    public static Quest getQuest(String id) {
        return values.get(id);
    }

    public static ArrayList<Quest> getQuests(Collection<String> questsS) {
        ArrayList<Quest> quests = new ArrayList<Quest>();

        for (String quest: questsS) {
            quests.add(getQuest(quest));
        }
        return quests;
    }

    public static List<Quest> getQuests() {
        ArrayList<Quest> quests = new ArrayList<Quest>();

        values.forEach((id, quest) -> {
            quests.add(quest);
        });
        return quests.stream().toList();
    }

}
