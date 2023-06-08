package whosalbercik.envi.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import whosalbercik.envi.registry.TradeRegistry;
import whosalbercik.envi.registry.obj.Trade;


public class TradeScreen extends AbstractContainerScreen<TradeMenu> implements MenuAccess<TradeMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int containerRows;

    public TradeScreen(TradeMenu tradeMenu, Inventory inventory, Component title) {
        super(tradeMenu, inventory, title);
        this.passEvents = false;
        this.containerRows = tradeMenu.getRowCount();
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
    protected void slotClicked(Slot slot, int slotIndex, int p_97780_, ClickType clickType) {
        if (slot == null) {
            return;
        }

        ItemStack clicked = slot.getItem();

        if (clicked.getOrCreateTag().getInt("envi.tradeMultiplier") != 0) {
            Trade trade = TradeRegistry.getTrade(clicked.getTag().getString("envi.id"));

            int multiplier = clicked.getTag().getInt("envi.tradeMultiplier");

            if (trade.getCompleteLimit() < Minecraft.getInstance().player.getPersistentData().getInt("envi.questCount." + trade.getId()) + multiplier && trade.getCompleteLimit() != -1) {
                Minecraft.getInstance().player.closeContainer();
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("Completing this transaction would break the usage limit of this trade!").withStyle(ChatFormatting.RED));
                return;
            }


            if (trade.hasRequiredItems(Minecraft.getInstance().player, multiplier)) {
                trade.complete(Minecraft.getInstance().player, multiplier);
            } else {
                Minecraft.getInstance().player.closeContainer();
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(trade.notCompletedQuestBook(multiplier))));
            }

        }
    }
}
