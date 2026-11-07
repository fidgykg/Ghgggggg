package com.iraqimoney;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * شاشة الشوب: صندوق 9x3 من جهة السيرفر فقط، كل خانة سلعة بسعرها بالدينار.
 */
public class ShopScreenHandler extends GenericContainerScreenHandler {

    public record Entry(Item item, int count, int price) {}

    /** عدّل القائمة هنا: (السلعة، الكمية، السعر بالدينار). الحد 27 سلعة. */
    public static final List<Entry> ENTRIES = List.of(
            new Entry(Items.BREAD, 16, 1000),
            new Entry(Items.COOKED_BEEF, 16, 2500),
            new Entry(Items.OAK_LOG, 32, 1000),
            new Entry(Items.COAL, 16, 1500),
            new Entry(Items.IRON_INGOT, 8, 5000),
            new Entry(Items.GOLD_INGOT, 8, 10000),
            new Entry(Items.ENDER_PEARL, 1, 5000),
            new Entry(Items.EXPERIENCE_BOTTLE, 8, 10000),
            new Entry(Items.GOLDEN_APPLE, 1, 10000),
            new Entry(Items.DIAMOND, 1, 25000),
            new Entry(Items.NETHERITE_INGOT, 1, 50000)
    );

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, buildShopInventory());
    }

    private ShopScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory shop) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, shop, 3);
    }

    private static SimpleInventory buildShopInventory() {
        SimpleInventory inv = new SimpleInventory(27);
        for (int i = 0; i < ENTRIES.size() && i < 27; i++) {
            Entry e = ENTRIES.get(i);
            ItemStack stack = new ItemStack(e.item(), e.count());
            stack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                    Text.literal("السعر: " + String.format("%,d", e.price()) + " دينار").formatted(Formatting.GOLD),
                    Text.literal("اضغط للشراء").formatted(Formatting.GRAY)
            )));
            inv.setStack(i, stack);
        }
        return inv;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // خانات الشوب: شراء فقط، ما نسمح بسحب السلعة
        if (slotIndex >= 0 && slotIndex < 27) {
            if (actionType == SlotActionType.PICKUP
                    && slotIndex < ENTRIES.size()
                    && player instanceof ServerPlayerEntity sp) {
                buy(sp, ENTRIES.get(slotIndex));
            }
            return;
        }
        // نمنع الشفت-كليك والسحب المتعدد والدبل-كليك حتى ما يتلاعبون بالشوب
        if (actionType == SlotActionType.QUICK_MOVE
                || actionType == SlotActionType.PICKUP_ALL
                || actionType == SlotActionType.QUICK_CRAFT) {
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private static void buy(ServerPlayerEntity player, Entry e) {
        if (!Money.pay(player.getInventory(), e.price())) {
            player.sendMessage(Text.literal("ما عندك فلوس كافية!").formatted(Formatting.RED), true);
            return;
        }
        player.getInventory().offerOrDrop(new ItemStack(e.item(), e.count()));
        player.sendMessage(Text.literal("تم الشراء ✔").formatted(Formatting.GREEN), true);
    }
}
