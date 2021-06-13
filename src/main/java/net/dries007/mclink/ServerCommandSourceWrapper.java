package net.dries007.mclink;

import net.dries007.mclink.binding.FormatCode;
import net.dries007.mclink.binding.ISender;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class ServerCommandSourceWrapper implements ISender
{
	private final ServerCommandSource scs;

	ServerCommandSourceWrapper(ServerCommandSource scs)
	{
		this.scs = scs;
	}

	@Override
	public @NotNull String getName()
	{
		return scs.getName() == null ? "Console" : scs.getName();
	}

	@Override
	public void sendMessage(String s)
	{
		scs.sendFeedback(new LiteralText(s), false);
	}

	@Override
	public void sendMessage(String s, FormatCode formatCode)
	{
		LiteralText text = new LiteralText(s);
		Formatting format = Formatting.byCode(formatCode.c);
		text.setStyle(text.getStyle().withFormatting(format));
		scs.sendFeedback(text, false);
	}
}
