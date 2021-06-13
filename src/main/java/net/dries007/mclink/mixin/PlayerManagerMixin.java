package net.dries007.mclink.mixin;

import net.dries007.mclink.events.PlayerConnectCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
	@Inject(at = @At("RETURN"), method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void onPlayerConnect(ClientConnection cc, ServerPlayerEntity spe, CallbackInfo ci)
	{
		PlayerConnectCallback.EVENT.invoker().invoke(spe);
	}
}