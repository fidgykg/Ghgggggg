package com.iraqimoney;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    /** قيمة كل ورقة بالدينار */
    public static final Map<Item, Integer> VALUES = new LinkedHashMap<>();
    /** الفئات مرتبة من الأكبر للأصغر (تستخدم للباقي) */
    public static final List<Item> DESC = new ArrayList<>();
    private static final List<Item> ALL = new ArrayList<>();

    public static final Item DINAR_250   = register("dinar_250", 250);
    public static final Item DINAR_500   = register("dinar_500", 500);
    public static final Item DINAR_1000  = register("dinar_1000", 1000);
    public static final Item DINAR_5000  = register("dinar_5000", 5000);
    public static final Item DINAR_10000 = register("dinar_10000", 10000);
    public static final Item DINAR_25000 = register("dinar_25000", 25000);
    public static final Item DINAR_50000 = register("dinar_50000", 50000);

    private static Item register(String name, int value) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(IraqiMoney.MOD_ID, name));
        Item item = new Item(new Item.Settings().registryKey(key));
        Registry.register(Registries.ITEM, key, item);
        ALL.add(item);
        DESC.add(0, item);
        VALUES.put(item, value);
        return item;
    }

    public static final ItemGroup GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(DINAR_10000))
            .displayName(Text.translatable("itemGroup.iraqimoney"))
            .entries((context, entries) -> ALL.forEach(entries::add))
            .build();

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(IraqiMoney.MOD_ID, "main"), GROUP);
    }
}
