package whosalbercik.envi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import whosalbercik.envi.obj.Quest;
import whosalbercik.envi.registry.QuestRegistry;

@OnlyIn(Dist.CLIENT)
public class QuestScreen extends AbstractContainerScreen<QuestMenu> implements MenuAccess<QuestMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int containerRows;

    public QuestScreen(QuestMenu questMenu, Inventory inventory, Component title) {
        super(questMenu, inventory, title);
        this.passEvents = false;
        this.containerRows = questMenu.getRowCount();
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void render(PoseStack stack, int pPartialInt, int mouseX, float mouseY) {
        this.renderBackground(stack);
        super.render(stack, pPartialInt, mouseX, mouseY);
        this.renderTooltip(stack, pPartialInt, mouseX);
    }

    protected void renderBg(PoseStack stack, float pPartialInt, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        this.blit(stack, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void slotClicked(Slot slotClicked, int slotIndex, int p_97780_, ClickType type) {
        assert slotClicked != null;


        LocalPlayer p = Minecraft.getInstance().player;
        Quest questClicked = QuestRegistry.getQuest(slotClicked.getItem().getOrCreateTag().getString("envi.id"));


        // if clicked quest that is in progress
        if (p.getPersistentData().contains("envi.currentQuest") && p.getPersistentData().getString("envi.currentQuest").equals(questClicked.getId())) {
;
            p.closeContainer();


            // complete quest
            if (questClicked.hasRequiredItems(p)) {

                questClicked.complete(p);

                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(questClicked.completedQuestBook())));


            } else {
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(questClicked.notCompletedQuestBook())));
            }
            return;
        }
        // quest already completed
        else if (p.getPersistentData().getList("envi.completedQuests", 8) != null && p.getPersistentData().getList("envi.completedQuests", 8).contains(StringTag.valueOf(questClicked.getId()))) {
            return;
        }

        // set new Quest
        else {

            p.closeContainer();
            Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(questClicked.getBook())));

            questClicked.makeCurrent(p);
            return;
        }




    }
}
