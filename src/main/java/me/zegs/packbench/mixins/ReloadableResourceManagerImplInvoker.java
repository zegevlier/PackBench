package me.zegs.packbench.mixins;

import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public interface ReloadableResourceManagerImplInvoker {
    @Invoker("reload")
    ResourceReload invokeReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs);

    @Accessor("activeManager")
    LifecycledResourceManager getActiveManager();

    @Accessor("reloaders")
    List<ResourceReloader> getReloaders();

}
