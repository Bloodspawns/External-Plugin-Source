package net.runelite.client.plugins.vengeancetracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("VengeanceTracker")
public interface VengeanceTrackerConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "overlaySelf",
            name = "Show vengeance icon on player",
            description = "Ye idk what you expect the name says all"
    )
    default boolean overlaySelf()
    {
        return false;
    }
}
