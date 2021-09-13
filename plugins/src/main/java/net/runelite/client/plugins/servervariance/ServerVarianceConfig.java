package net.runelite.client.plugins.servervariance;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("servervariance")
public interface ServerVarianceConfig extends Config
{
	@ConfigItem(
			keyName = "color",
			name = "Color overlay red on outliers",
			description = ""
	)
	default boolean colorOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "relative",
			name = "Show relative variance to ideal (600ms)",
			description = ""
	)
	default boolean showRelative()
	{
		return false;
	}
}
