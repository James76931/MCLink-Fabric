package net.dries007.mclink.gson;

import java.util.List;

public interface ConfigObject
{
	default int getInt(String name, int def, int min, int max)
	{
		throw new UnsupportedOperationException();
	}

	default String getString(String name, String def)
	{
		throw new UnsupportedOperationException();
	}

	default boolean getBoolean(String name, boolean def)
	{
		throw new UnsupportedOperationException();
	}

	default List<String> getStringList(String name)
	{
		throw new UnsupportedOperationException();
	}
}
