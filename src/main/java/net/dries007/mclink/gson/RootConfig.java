package net.dries007.mclink.gson;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootConfig implements ConfigObject
{
	public GeneralConfig general = new GeneralConfig();
	public Map<String, List<String>> services = new HashMap<>();

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
}
