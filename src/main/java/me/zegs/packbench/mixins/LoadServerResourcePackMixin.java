package me.zegs.packbench.mixins;

import me.zegs.packbench.PackBench;
import me.zegs.packbench.PackCommand;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Mixin(ClientBuiltinResourcePackProvider.class)
public class LoadServerResourcePackMixin {

    @Inject(at = @At("RETURN"), method = "download")
    void loadServerPack(URL url, String packSha1, boolean closeAfterDownload, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        PackBench.LOGGER.info("Server resource pack loaded!");
        PackBench.LOGGER.info("URL: " + url);
        PackBench.LOGGER.info("SHA1: " + packSha1);
        PackBench.LOGGER.info("Close after download: " + closeAfterDownload);
        PackCommand.setQuickLoad(url);
    }
}
