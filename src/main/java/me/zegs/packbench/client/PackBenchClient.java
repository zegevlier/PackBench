package me.zegs.packbench.client;

import com.mojang.brigadier.CommandDispatcher;
import me.zegs.packbench.PackBench;
import me.zegs.packbench.PackCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PackBenchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher,
                                                          CommandRegistryAccess registryAccess) -> {
            PackCommand.register(dispatcher);
        });
        PackBench.LOGGER.info("Loading PackBench!");
    }
}
