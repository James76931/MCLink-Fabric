package net.dries007.mclink;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import javax.sql.CommonDataSource;
import net.dries007.mclink.api.APIException;
import net.dries007.mclink.common.CommonConfig;
import net.dries007.mclink.gson.RootConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class FabricConfig extends CommonConfig
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final MinecraftServer server;
	private RootConfig config;

	public FabricConfig(MinecraftServer server)
	{
		this.server = server;
	}

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
	protected void addService(String name, String command)
	{
		// this is a hack
	}

	@Override
	protected void setServiceComment(String name, String comment)
	{
		// json doesnt support comments
	}

	@Override
	protected void setGlobalCommentServices(String comment)
	{
		// json doesnt support comments
	}

	@Override
	protected List<String>[] getServiceEntries(String name)
	{
		return config.services.getStringList(name).stream()
			.map(CommonConfig::splitArgumentString)
			.toArray((IntFunction<List<String>[]>) List[]::new);
	}

	@Override
	protected Set<String> getAllDefinedServices()
	{
		// this is an even bigger hack
		return Set.of("Twitch", "Patreon", "GameWisp");
	}

	@Override
	public @Nullable String reload() throws ConfigException, IOException, APIException
	{
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve("mclink").resolve("config.json");
		if (!Files.exists(configPath))
		{
			Files.createDirectories(configPath.resolve(".."));
			Files.writeString(configPath, GSON.toJson(new RootConfig()));
		}

		config = GSON.fromJson(String.join("\n", Files.readAllLines(configPath)), RootConfig.class);

		return super.reload();
	}
}
