package whosalbercik.envi.registry;

import whosalbercik.envi.config.ServerConfig;
import whosalbercik.envi.registry.obj.Quest;
import whosalbercik.envi.registry.obj.Trade;

import java.util.ArrayList;
import java.util.Collection;

public class TradeRegistry {

    public static void load() {
        for (String questId: ServerConfig.QUESTS.get().valueMap().keySet()) {
            addTrade(questId, Trade.load(questId));
        }

    }


    private static void addTrade(String id, Trade obj) {
        QuestRegistry.values.put(id, obj);
    }

    public static Trade getTrade(String id) {
        Quest potentialTrade = QuestRegistry.values.get(id);

        if (QuestRegistry.values.get(id) instanceof Trade) {
            return (Trade) potentialTrade;
        }

        return null;
    }

    public static ArrayList<Trade> getQuests(Collection<String> tradesS) {
        ArrayList<Trade> trades = new ArrayList<Trade>();

        for (String trade: tradesS) {
            trades.add(getTrade(trade));
        }
        return trades;
    }

}
