package net.dries007.mclink.mixin;

import net.minecraft.server.ServerConfigList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerConfigList.class)
public interface ServerConfigListAccessor
{
	@Invoker
	boolean callContains(Object object);
}
