package net.dries007.mclink;

import com.google.common.collect.ImmutableCollection;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.dries007.mclink.api.APIException;
import net.dries007.mclink.api.Authentication;
import net.dries007.mclink.binding.FormatCode;
import net.dries007.mclink.binding.IConfig;
import net.dries007.mclink.binding.IPlayer;
import net.dries007.mclink.common.JavaLogger;
import net.dries007.mclink.common.MCLinkCommon;
import net.dries007.mclink.common.Player;
import net.dries007.mclink.events.AsyncPlayerConnectCallback;
import net.dries007.mclink.events.PlayerConnectCallback;
import net.dries007.mclink.mixin.ServerConfigListAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class MCLink implements DedicatedServerModInitializer
{
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private MinecraftServer server;

	private final MCLinkCommon common = new MCLinkCommon()
	{
		@Override
		protected void authCompleteAsync(IPlayer iPlayer, ImmutableCollection<Authentication> immutableCollection, Marker marker)
		{
			if (marker != Marker.ALLOWED)
			{
				server.execute(() ->
				{
					ServerPlayerEntity player = server.getPlayerManager().getPlayer(iPlayer.getUuid());

					// player might've disconnected
					if (player == null)
					{
						return;
					}

					player.networkHandler.disconnect(Text.of(getConfig().getMessage(marker)));
				});
			}
		}

		@Override
		protected @Nullable String nameFromUUID(UUID uuid)
		{
			ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
			return player == null ? null : player.getName().getString();
		}

		@Override
		public void sendMessage(String s)
		{
			server.execute(() ->
			{
				server.getPlayerManager().getPlayerList().forEach(player ->
				{
					player.sendMessage(Text.of(s), false);
				});
			});
		}

		@Override
		public void sendMessage(String s, FormatCode formatCode)
		{
			sendMessage(s);
		}
	};

	@Override
	public void onInitializeServer()
	{
		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
		CommandRegistrationCallback.EVENT.register(this::registerCommands);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
		AsyncPlayerConnectCallback.EVENT.register(this::onAsyncPlayerConnect);
		PlayerConnectCallback.EVENT.register(this::onPlayerConnect);
	}

	private void onServerStarting(MinecraftServer server)
	{
		this.server = server;

		try
		{
			common.setModVersion("0.3.2");
			common.setMcVersion("1.17");
			common.setBranding("Fabric");
			common.setLogger(new JavaLogger(Logger.getLogger("MCLink")));
			common.setConfig(new FabricConfig());
			common.setSide(MCLinkCommon.Side.SERVER);

			common.init();
		}
		catch (IConfig.ConfigException | APIException | IOException e)
		{
			common.getLogger().error("WARNING! Something went wrong initializing... People won't be able to join.");
			common.getLogger().catching(e);
		}
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> scs, boolean _ignored)
	{
		common.registerCommands(cmd -> FabricMCLinkCommand.register(scs, common, cmd));
	}

	private void onAsyncPlayerConnect(String name, UUID uuid)
	{
		GameProfile profile = new GameProfile(uuid, name);
		boolean op = ((ServerConfigListAccessor) server.getPlayerManager().getOpList()).callContains(profile);
		boolean wl = ((ServerConfigListAccessor) server.getPlayerManager().getWhitelist()).callContains(profile);
		System.out.println("user: " + name + " opped? " + op + " whitelist? " + wl);

		common.checkAuthStatusAsync(new Player(null, name, uuid), op, wl, r -> executor.schedule(r, 0, TimeUnit.MILLISECONDS));
	}

	private void onPlayerConnect(ServerPlayerEntity player)
	{
		common.login(new Player(new ServerCommandSourceWrapper(player.getCommandSource()), player.getGameProfile().getName(), player.getUuid()), server.getPlayerManager().isOperator(player.getGameProfile()));
	}

	private void onServerStopping(MinecraftServer server)
	{
		common.deInit();
	}
}
