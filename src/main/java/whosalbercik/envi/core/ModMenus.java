package whosalbercik.envi.core;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import whosalbercik.envi.ENVI;
import whosalbercik.envi.gui.QuestMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ENVI.MODID);

    public static final RegistryObject<MenuType<QuestMenu>> QUEST_MENU = MENUS.register("quest_menu", () -> new MenuType<QuestMenu>(QuestMenu::new));


}
