package net.runelite.client.plugins.specbar;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "(G) Spec Bar",
	description = "Shows the spec bar on weapons that do not have one",
	tags = {"special", "spec-bar", "special attack"},
	enabledByDefault = true
)
public class SpecBarPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	public SpecBarConfig config;

	@Provides
	SpecBarConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpecBarConfig.class);
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		final int specBarWidgetId = config.specbarid();
		Widget specbarWidget = client.getWidget(WidgetID.COMBAT_GROUP_ID, specBarWidgetId);
		if (specbarWidget != null)
		{
			specbarWidget.setHidden(false);
		}
	}
}