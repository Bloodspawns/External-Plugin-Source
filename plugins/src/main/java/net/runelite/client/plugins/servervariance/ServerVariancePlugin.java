package net.runelite.client.plugins.servervariance;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@PluginDescriptor(
		name = "Server Variance",
		description = "Shows time measured between game ticks",
		tags = {"ping, variance, time"}
)
public class ServerVariancePlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ServerVarianceOverlay overlay;

	private Instant lastUpdate;

	@Getter
	private long deltaTime = -1;

	@Provides
	ServerVarianceConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ServerVarianceConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case LOADING:
			case LOGGED_IN:
				return;
		}
		deltaTime = -1;
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		if (lastUpdate != null)
		{
			deltaTime = Duration.between(lastUpdate, Instant.now()).toMillis();
		}

		lastUpdate = Instant.now();
	}
}
