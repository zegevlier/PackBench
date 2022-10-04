package me.zegs.packbench;

import com.mojang.brigadier.CommandDispatcher;
import me.zegs.packbench.mixins.MinecraftClientAccessor;
import me.zegs.packbench.mixins.ReloadableResourceManagerImplInvoker;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.resource.ResourcePack;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PackCommand {

    private static URL quickLoad = null;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
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
                .then(literal("bench")
                        .executes(context -> bench(context.getSource()))
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

    private static final CompletableFuture<Unit> COMPLETED_UNIT_FUTURE;

    static {
        COMPLETED_UNIT_FUTURE = CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    public static int reload(FabricClientCommandSource source) {
        long startTime = System.nanoTime();
        MinecraftClient mc = MinecraftClient.getInstance();

        mc.getResourcePackManager().scanPacks();
        List<ResourcePack> list = mc.getResourcePackManager().createResourcePacks();
//        if (!force) {
//            this.resourceReloadLogger.reload(ResourceReloadLogger.ReloadReason.MANUAL, list);
//        }
        source.sendFeedback(Text.literal("Prepared reload in " + (System.nanoTime() - startTime) / 1000000 + "ms"));
        ((ReloadableResourceManagerImplInvoker) mc.getResourceManager())
                .invokeReload(Util.getMainWorkerExecutor(), mc, COMPLETED_UNIT_FUTURE, list)
                .whenComplete().thenAccept((a) -> {
                    source.sendFeedback(Text.literal("Started world reload in " + (System.nanoTime() - startTime) / 1000000 + "ms"));
                    mc.worldRenderer.reload();
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    source.sendFeedback(Text.literal("Reloaded resources in " + duration + "ms"));
                });
//        mc.setOverlay(new SplashOverlay(mc, , (throwable) -> {
//
////            mc.resourceReloadLogger.finish();
//        }, true));


        return 1;
    }

    public static int bench(FabricClientCommandSource source) {
        PackBench.setLogging(true);

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getResourcePackManager().scanPacks();
        List<ResourcePack> list = mc.getResourcePackManager().createResourcePacks();
        ((MinecraftClientAccessor) mc).getResourceReloadLogger().reload(ResourceReloadLogger.ReloadReason.MANUAL, list);

        ((ReloadableResourceManagerImplInvoker) mc.getResourceManager())
                .invokeReload(Util.getMainWorkerExecutor(), mc, COMPLETED_UNIT_FUTURE, list)
                .whenComplete().thenAccept((a) -> {
                    ((MinecraftClientAccessor) mc).getResourceReloadLogger().finish();
                    List<PackEvent> events = PackBench.getEvents();
                    PackBench.LOGGER.info("Events: " + events.size());
                    long base_time = events.get(0).getTimestamp();
                    for (PackEvent event : events) {
                        source.sendFeedback(Text.literal(event.getType().toString() + " - " +
                                (event.getTimestamp() - base_time) / 1000000 + "ms: " + event.getMessage()));
                    }
                    PackBench.clearEvents();
                    PackBench.setLogging(false);
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
