package me.zegs.packbench;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.net.MalformedURLException;
import java.net.URL;

public class PackCommand {

    private static URL quickLoad = null;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher){
        dispatcher.register(literal("pack")
                .then(literal("load")
                        .then(argument("url", greedyString())
                                .executes(context -> pack(context.getSource(), context.getArgument("url", String.class))))
                )
                .then(literal("unload")
                        .executes(context -> unload(context.getSource()))
                )
                .then(literal("reload")
                        .executes(context -> reload(context.getSource()))
                )
                .then(literal("quickload")
                        .executes(context -> quickLoad(context.getSource()))
                )
        );
    }

    public static int pack(FabricClientCommandSource source, String url) {
        source.sendFeedback(Text.literal("Testing..."));
        URL download_url;
        try {
//                    download_url = new URL("https://packs.mcpack.site/resourcepack.zip");
            download_url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e.getMessage()));
            return 0;
        }
        MinecraftClient.getInstance().getResourcePackProvider()
                .download(download_url,
                        "", true);
        return 1;
    }

    public static int unload(FabricClientCommandSource source) {
        MinecraftClient.getInstance().getResourcePackProvider().clear();
        return 1;
    }

    public static int reload(FabricClientCommandSource source) {
        long startTime = System.nanoTime();
        MinecraftClient.getInstance().reloadResourcesConcurrently()
                .thenAccept((_a) -> {
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    source.sendFeedback(Text.literal("Reloaded resources in " + duration + "ms"));
                });
        return 1;
    }

    public static int quickLoad(FabricClientCommandSource source) {
        if (quickLoad == null) {
            source.sendFeedback(Text.literal("No quickload URL set"));
            return 0;
        }
        MinecraftClient.getInstance().getResourcePackProvider()
                .download(quickLoad,
                        "", true);
        return 1;
    }

    public static void setQuickLoad(URL url) {
        PackCommand.quickLoad = url;
    }


}