package me.zegs.packbench.mixins;

import me.zegs.packbench.PackBench;
import me.zegs.packbench.PackEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ResourceReloadLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(at = @At("HEAD"), method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;")
    void onReloadResources(boolean bl, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        PackBench.log(new PackEvent(PackEvent.EventType.LOAD_START, String.valueOf(bl)));
    }

    @Inject(at = @At("RETURN"), method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;")
    void onReloadResourcesEnd(boolean bl, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        PackBench.log(new PackEvent(PackEvent.EventType.LOAD_END, String.valueOf(bl)));
    }
}
