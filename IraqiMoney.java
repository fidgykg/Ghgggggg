package com.iraqimoney;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class IraqiMoney implements ModInitializer {
    public static final String MOD_ID = "iraqimoney";

    @Override
    public void onInitialize() {
        ModItems.init();

        // الأمر /shop يفتح الشوب
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("shop").executes(ctx -> {
                    var player = ctx.getSource().getPlayerOrThrow();
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new ShopScreenHandler(syncId, inv),
                            Text.literal("الشوب | Shop")));
                    return 1;
                })));
    }
}
