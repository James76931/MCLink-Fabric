package net.dries007.mclink.events;

import java.util.UUID;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AsyncPlayerConnectCallback
{
	Event<AsyncPlayerConnectCallback> EVENT = EventFactory.createArrayBacked(AsyncPlayerConnectCallback.class,
		(listeners) -> (name, uuid) ->
		{
			for (AsyncPlayerConnectCallback listener : listeners)
			{
				listener.invoke(name, uuid);
			}
		});

	void invoke(String name, UUID uuid);
}
