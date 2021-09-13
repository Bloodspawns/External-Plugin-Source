package net.runelite.client.plugins.testplugin;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Test Plugin", description = "The testing grounds for everything")
@Slf4j
public class TestPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private OverlayManager overlayManager;
	@Inject private TestOverlay testOverlay;
	@Inject private TestConfig config;
	@Inject private EventBus eventBus;

	@Provides TestConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TestConfig.class);
	}

	@Getter private int timer = 0;

	@Override
	protected void startUp()
	{
//		overlayManager.add(testOverlay);
//		log.debug(String.valueOf(config.txtOutline()));
		timer = 0;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(testOverlay);
		timer = 0;
	}

	RuneLiteObject runeLiteObject;
	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (config.clockEnable())
		{
			if (timer == 0)
			{
				timer = config.clockTime();
			}
			else
			{
				timer--;
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e)
	{
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied e)
	{
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
	}

	@Getter private GameObject bankChest = null;
	@Getter private GameObject bankDesk = null;
	@Getter private GameObject bankSink = null;

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
	}
}