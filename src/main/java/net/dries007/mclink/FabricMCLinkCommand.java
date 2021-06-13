package net.dries007.mclink;

import com.mojang.brigadier.CommandDispatcher;
import net.dries007.mclink.binding.FormatCode;
import net.dries007.mclink.binding.ICommand;
import net.dries007.mclink.binding.IMinecraft;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.server.command.ServerCommandSource;

public class FabricMCLinkCommand
{
	private static IMinecraft mc;
	private static ICommand command;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, IMinecraft mc, ICommand command)
	{
		FabricMCLinkCommand.mc = mc;
		FabricMCLinkCommand.command = command;

		dispatcher.register(
			literal("mclink")
				.requires(scs -> scs.hasPermissionLevel(2))
				.then(literal("close")
					.executes(scs -> handle(scs.getSource(), "close"))
				)
				.then(literal("open")
					.executes(scs -> handle(scs.getSource(), "open"))
				)
				.then(literal("reload")
					.executes(scs -> handle(scs.getSource(), "reload"))
				)
				.then(literal("status")
					.executes(scs -> handle(scs.getSource(), "status"))
				)
		);
	}

	private static int handle(ServerCommandSource scs, String subcommand)
	{
		ServerCommandSourceWrapper wrapper = new ServerCommandSourceWrapper(scs);
		try
		{
			command.run(mc, wrapper, new String[]{subcommand});
			return 1;
		}
		catch (ICommand.CommandException ex)
		{
			wrapper.sendMessage("Caught error:", FormatCode.RED);
			Throwable e = ex;
			do wrapper.sendMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
			while ((e = e.getCause()) != null);
			return 1;
		}
	}
}
