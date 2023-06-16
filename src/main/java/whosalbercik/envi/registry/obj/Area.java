package whosalbercik.envi.registry.obj;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import whosalbercik.envi.registry.QuestRegistry;

import java.util.Optional;

public class Area extends RegistryObject<Area>{

    private Double startingX;
    private Double startingZ;

    private Double endingX;
    private Double endingZ;

    private Optional<BlockPos> teleportTo;
    private Optional<Quest> unlockQuest;

    private Entering entering;
    private Breaking breaking;
    private Placing placing;

    protected Area(String id, Class<Area> type, ForgeConfigSpec.ConfigValue<Config> config) {
        super(id, Area.class, config);
    }

    @Override
    protected boolean loadAttributesFromConfig(Config config) {
        startingX = (double) getPosFromString(config.get("starting")).getX();
        startingZ = (double) getPosFromString(config.get("starting")).getZ();

        endingX = (double) getPosFromString(config.get("ending")).getX();
        endingZ = (double) getPosFromString(config.get("ending")).getZ();

        entering = Entering.valueOf(config.get("entering"));
        breaking = Breaking.valueOf(config.get("breaking"));
        placing = Placing.valueOf(config.get("placing"));

        if (entering != Entering.ENTERABLE) {
            teleportTo = Optional.of(getPosFromString(config.get("teleportTo")).above());
        }
        else teleportTo = Optional.empty();

        if (entering == Entering.AFTERQUEST || breaking == Breaking.AFTERQUEST || placing != Placing.AFTERQUEST) {
            unlockQuest = Optional.of(QuestRegistry.getQuest(config.get("unlockQuest")));
        } else unlockQuest = Optional.empty();

        return startingX != null && startingZ != null && endingX != null && endingZ != null && entering != null && breaking != null && placing != null && teleportTo != null && unlockQuest != null;
    }

    public boolean isInArea(BlockPos pos) {
        return pos.getX() >= startingX && pos.getX() <= endingX && pos.getZ() >= startingZ && pos.getZ() <= endingZ;
    }

    public boolean canEnter(ServerPlayer p) {
        switch (entering) {
            case UNENTERABLE -> {
                return false;
            }
            case AFTERQUEST -> {
                return unlockQuest.get().isCompletedBy(p);
            }
            default -> {
                return true;
            }
        }
    }

    public boolean canPlace(ServerPlayer p) {
        switch (placing) {
            case UNPLACABLE -> {
                return false;
            }
            case AFTERQUEST -> {
                return unlockQuest.get().isCompletedBy(p);
            }
            default -> {
                return true;
            }
        }
    }

    public boolean canBreak(ServerPlayer p) {
        switch (breaking) {
            case UNBREAKABLE -> {
                return false;
            }
            case AFTERQUEST -> {
                return unlockQuest.get().isCompletedBy(p);
            }
            default -> {
                return true;
            }
        }
    }


    public BlockPos getPosFromString(String str) {
        return new BlockPos(Double.parseDouble(str.split(",")[0]), Double.parseDouble(str.split(",")[1]), Double.parseDouble(str.split(",")[2]));
    }
    public Optional<BlockPos> getTeleportTo() {
        return teleportTo;
    }

    public Optional<Quest> getUnlockQuest() {
        return unlockQuest;
    }

    public Entering getEntering() {
        return entering;
    }

    public Breaking getBreaking() {
        return breaking;
    }

    public Placing getPlacing() {
        return placing;
    }

    public enum Entering {
        ENTERABLE,
        AFTERQUEST,
        UNENTERABLE,
    }
    public enum Breaking {
        BREAKABLE,
        AFTERQUEST,
        UNBREAKABLE,
    }

    public enum Placing {
        PLACABLE,
        AFTERQUEST,
        UNPLACABLE
    }
}
