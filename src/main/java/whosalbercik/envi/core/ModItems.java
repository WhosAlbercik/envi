package whosalbercik.envi.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.items.CoordinateCopier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ENVI.MODID);

    public static final RegistryObject<CoordinateCopier> COORD_COPIER = ITEMS.register("coordinate_copier", () -> new CoordinateCopier(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
}
