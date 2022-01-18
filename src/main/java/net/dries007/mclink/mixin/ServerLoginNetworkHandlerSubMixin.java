package net.dries007.mclink.mixin;

import com.mojang.authlib.GameProfile;
import net.dries007.mclink.events.AsyncPlayerConnectCallback;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerLoginNetworkHandler$1")
public class ServerLoginNetworkHandlerSubMixin
{
	@Final
	@Shadow
	ServerLoginNetworkHandler field_14176;

	@Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), method = "run()V")
	public void onPlayerJoinAsync(CallbackInfo ci)
	{
		//System.out.println("player joining async, synthetic field is: " + field_14176);
		GameProfile profile = ((ServerLoginNetworkHandlerAccessor) field_14176).getProfile();
		AsyncPlayerConnectCallback.EVENT.invoker().invoke(profile.getName(), profile.getId());
	}
}
