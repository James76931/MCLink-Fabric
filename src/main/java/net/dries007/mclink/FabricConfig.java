package net.dries007.mclink;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.dries007.mclink.api.APIException;
import net.dries007.mclink.common.CommonConfig;
import net.dries007.mclink.gson.RootConfig;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public class FabricConfig extends CommonConfig
{
	private static final String COMMENT_PREFIX = "_comment: ";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private RootConfig config;

	@Override
	protected String getString(String key, String def, String comment)
	{
		return config.general.getString(key, def);
	}

	@Override
	protected boolean getBoolean(String key, boolean def, String comment)
	{
		return config.general.getBoolean(key, def);
	}

	@Override
	protected int getInt(String key, int def, int min, int max, String comment)
	{
		return config.general.getInt(key, def, min, max);
	}

	@Override
	protected void addService(String name, String comment)
	{
		config.services.put(name, new ArrayList<>());
		setServiceComment(name, comment);
		saveConfig();
	}

	@Override
	protected void setServiceComment(String name, String comment)
	{
		// hack to get per-service comments
		List<String> serviceList = config.services.get(name);
		serviceList.removeIf(s -> s.startsWith(COMMENT_PREFIX));
		serviceList.add(0, COMMENT_PREFIX + comment);
		saveConfig();
	}

	@Override
	protected void setGlobalCommentServices(String comment)
	{
		// this is possible but it'd make the config look even uglier than it already does
	}

	@Override
	protected List<String>[] getServiceEntries(String name)
	{
		return config.services.get(name).stream()
			.filter(s -> !Strings.isNullOrEmpty(s) && !s.startsWith(COMMENT_PREFIX))
			.map(CommonConfig::splitArgumentString)
			.toArray((IntFunction<List<String>[]>) List[]::new);
	}

	@Override
	protected Set<String> getAllDefinedServices()
	{
		// this is an even bigger hack
		return config.services.keySet();
	}

	public void saveConfig()
	{
		try
		{
			Path configFolder = FabricLoader.getInstance().getConfigDir().resolve("mclink");
			if (Files.exists(configFolder) && !Files.isDirectory(configFolder))
			{
				Files.delete(configFolder);
			}
			Files.createDirectories(configFolder);
			Path configPath = configFolder.resolve("config.json");
			Files.writeString(configPath, GSON.toJson(config == null ? new RootConfig() : config));
		}
		catch (IOException ex)
		{
			throw new RuntimeException("Failed to save config", ex);
		}
	}

	@Override
	public @Nullable String reload() throws ConfigException, IOException, APIException
	{
		Path configFolder = FabricLoader.getInstance().getConfigDir().resolve("mclink");
		Path configPath = configFolder.resolve("config.json");
		if (!Files.exists(configPath))
		{
			Files.createDirectories(configFolder);
			Files.writeString(configPath, GSON.toJson(new RootConfig()));
		}

		config = GSON.fromJson(String.join("\n", Files.readAllLines(configPath)), RootConfig.class);

		return super.reload();
	}
}
