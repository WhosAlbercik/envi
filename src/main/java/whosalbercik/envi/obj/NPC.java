package whosalbercik.envi.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import whosalbercik.envi.config.ServerConfig;

import java.util.ArrayList;
import java.util.Collection;

public class NPC {
    private String displayname;
    private final String id;
    private ArrayList<String> requiredQuests;
    private ArrayList<String> quests;
    private VillagerProfession profession = VillagerProfession.NONE;


    private NPC(String id) {
        this.id = id;
    }

    public static NPC load(String id) {
        NPC obj = new NPC(id);

        if (obj.loadAttributesFromConfig()) {
            return obj;
        }
        else {
            return null;
        }
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
        mob.setVillagerData(mob.getVillagerData().setProfession(profession));

        mob.setCustomName(Component.literal(displayname));
        addTagsFromAttributes(mob);

        level.addFreshEntity(mob);

        mob.moveTo(pos.getCenter());


    }

    private void addTagsFromAttributes(Villager mob) {
        mob.getPersistentData().putBoolean("envi.npc", true);
        mob.getPersistentData().putString("envi.id", id);
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
        mob.getPersistentData().putString("envi.profession", profession.name());

    }

    private boolean loadAttributesFromConfig() {
        Config data = ServerConfig.NPCS.get().get(id);
        if (data == null) {
            return false;}

        this.requiredQuests = data.get("required");
        this.quests = data.get("quests");
        this.displayname = data.get("displayname");

        this.profession = getProfessionFromValue(data.get("profession"));

        return id != null && requiredQuests != null && displayname != null && quests != null && profession != null;
    }

    private VillagerProfession getProfessionFromValue(String value) {
        for (VillagerProfession prof: ForgeRegistries.VILLAGER_PROFESSIONS.getValues()) {
            if (prof.name().equals(value.toLowerCase())) {
                return prof;
            }
        }
        return null;
    }

    public String getDisplayname() {
        return displayname;
    }
    public String getId() {
        return id;
    }
    public ArrayList<String> getRequiredQuests() {
        return requiredQuests;
    }
    public VillagerProfession getProfession() {
        return profession;
    }
    public ArrayList<String> getQuests() {
        return quests;
    }

}
