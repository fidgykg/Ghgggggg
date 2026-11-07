package com.iraqimoney;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class Money {
    private Money() {}

    /** مجموع الفلوس العراقية اللي مع اللاعب */
    public static int total(PlayerInventory inv) {
        int total = 0;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            Integer v = ModItems.VALUES.get(s.getItem());
            if (v != null) total += v * s.getCount();
        }
        return total;
    }

    /** يخصم السعر ويرجع الباقي بأوراق. يرجع false إذا الفلوس ما تكفي */
    public static boolean pay(PlayerInventory inv, int price) {
        int total = total(inv);
        if (total < price) return false;

        for (int i = 0; i < inv.size(); i++) {
            if (ModItems.VALUES.containsKey(inv.getStack(i).getItem())) {
                inv.setStack(i, ItemStack.EMPTY);
            }
        }

        int change = total - price;
        for (Item note : ModItems.DESC) {
            int value = ModItems.VALUES.get(note);
            int n = change / value;
            change %= value;
            while (n > 0) {
                int c = Math.min(n, 64);
                inv.offerOrDrop(new ItemStack(note, c));
                n -= c;
            }
        }
        return true;
    }
}
