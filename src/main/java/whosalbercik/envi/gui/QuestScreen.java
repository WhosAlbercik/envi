package whosalbercik.envi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AirItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import whosalbercik.envi.handlers.ModPacketHandler;
import whosalbercik.envi.networking.IconClickedC2SPacket;
import whosalbercik.envi.registry.obj.Quest;
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

        if (slotClicked.getItem().getItem() instanceof AirItem) return;

        LocalPlayer p = Minecraft.getInstance().player;
        Quest questClicked = QuestRegistry.getQuest(slotClicked.getItem().getOrCreateTag().getString("envi.id"));

        if (questClicked == null) {
            return;
        }

        ModPacketHandler.sendToServer(new IconClickedC2SPacket(questClicked.getId()));

    }
}
