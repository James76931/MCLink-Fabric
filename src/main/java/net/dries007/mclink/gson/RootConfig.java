package net.dries007.mclink.gson;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RootConfig implements ConfigObject
{
	public GeneralConfig general = new GeneralConfig();
	public ServicesConfig services = new ServicesConfig();

	public static class GeneralConfig implements ConfigObject
	{
		public String kickMessage = "This is an MCLink protected server. Link your accounts via https://mclink.dries007.net and make sure you are subscribed to the right people";
		public String errorMessage = "MCLink could not verify your status. Please contact a server admin.";
		public String closedMessage = "This server is currently closed for the public";
		public boolean showStatus = true;
		public boolean closed = false;
		public boolean freeToJoin = false;
		public int timeout = 30;

		@Override
		public String getString(String name, String def)
		{
			return switch (name.toLowerCase())
				{
					case "kickmessage" -> kickMessage;
					case "errormessage" -> errorMessage;
					case "closedmessage" -> closedMessage;
					default -> throw new IllegalArgumentException(name);
				};
		}

		@Override
		public boolean getBoolean(String name, boolean def)
		{
			return switch (name.toLowerCase())
				{
					case "showstatus" -> showStatus;
					case "closed" -> closed; // i rest my case
					case "freetojoin" -> freeToJoin;
					default -> throw new IllegalArgumentException(name);
				};
		}

		@Override
		public int getInt(String name, int def, int min, int max)
		{
			if (name.equals("timeout"))
			{
				timeout = Math.max(Math.min(timeout, max), min);
				return timeout;
			}

			throw new IllegalArgumentException(name);
		}
	}

	public static class ServicesConfig implements ConfigObject
	{
		@SerializedName("GameWisp")
		public List<String> gamewisp = List.of();

		@SerializedName("Patreon")
		public List<String> patreon = List.of();

		@SerializedName("Twitch")
		public List<String> twitch = List.of();

		@Override
		public List<String> getStringList(String name)
		{
			return switch (name.toLowerCase())
				{
					case "gamewisp" -> gamewisp;
					case "patreon" -> patreon;
					case "twitch" -> twitch;
					default -> throw new IllegalArgumentException(name);
				};
		}
	}
}
