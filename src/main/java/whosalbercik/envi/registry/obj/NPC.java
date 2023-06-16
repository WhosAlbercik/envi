package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Collection;

public class NPC extends RegistryObject<NPC> {
    private String displayname;
    private ArrayList<String> requiredQuests;
    private ArrayList<String> quests;

    protected NPC(String id, Class<NPC> type, ForgeConfigSpec.ConfigValue<Config> config) {
        super(id, type, config);
    }


    public boolean hasRequired(Player p) {
        CompoundTag persistantData = p.getPersistentData();
        ListTag completedQuests = (ListTag) persistantData.get("envi.completedQuests");

        if (completedQuests == null && !requiredQuests.isEmpty())  return false;
        else if(requiredQuests.isEmpty()) return true;

        ArrayList<String> completedQuestsStrings = new ArrayList<>();

        completedQuests.forEach((tag) -> completedQuestsStrings.add(tag.getAsString()));

        if (completedQuests.isEmpty()) {
            return false;
        }

        for (String quest: requiredQuests) {
            if (!completedQuestsStrings.contains(quest)) {
                return false;
            }
        }

        return true;
    }


    public Collection<String> getMissing(Player p) {
        CompoundTag persistantData = p.getPersistentData();
        ListTag completedQuests = persistantData.getList("envi.completedQuests", 9);

        if (completedQuests.isEmpty()) {
            return requiredQuests;
        }

        ArrayList<String> missing = new ArrayList<String>();

        for (String quest: requiredQuests) {
            if (!completedQuests.contains(quest)) {
                missing.add(quest);
            }
        }

        return missing;
    }

    public void spawnNPC(BlockPos pos, Level level) {
        Villager mob = new Villager(EntityType.VILLAGER, level);

        mob.setCustomName(Component.literal(displayname));
        addTagsFromAttributes(mob);

        level.addFreshEntity(mob);

        mob.moveTo(pos.getCenter());


    }

    private void addTagsFromAttributes(Villager mob) {
        mob.getPersistentData().putBoolean("envi.npc", true);
        mob.getPersistentData().putString("envi.id", getId());
        mob.getPersistentData().putString("envi.displayname", displayname);

        ListTag requiredQuestsTag = new ListTag();

        // add quests to listtag
        for (String quest: requiredQuests) {
            requiredQuestsTag.add(StringTag.valueOf(quest));
        }
        mob.getPersistentData().put("envi.required", requiredQuestsTag);

        ListTag questsTag = new ListTag();

        // add quests to listtag
        for (String quest: quests) {
            questsTag.add(StringTag.valueOf(quest));
        }
        mob.getPersistentData().put("envi.quests", requiredQuestsTag);

    }

    protected boolean loadAttributesFromConfig(Config data) {
        if (data == null) return false;

        this.requiredQuests = data.get("required");
        this.quests = data.get("quests");
        this.displayname = data.get("displayname");

        return getId() != null && requiredQuests != null && displayname != null && quests != null;
    }


    public String getDisplayname() {
        return displayname;
    }
    public ArrayList<String> getRequiredQuests() {
        return requiredQuests;
    }
    public ArrayList<String> getQuests() {
        return quests;
    }

}
