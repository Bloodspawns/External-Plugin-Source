package net.runelite.client.plugins.testplugin;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

import java.awt.Color;

@ConfigGroup("testplugin")
public interface TestConfig extends Config
{
	@ConfigItem(
			name = "Clock Enable",
			keyName = "clockEnable",
			description = "",
			position = 0
	)
	default boolean clockEnable() { return false; }

	@Range(
			max = 127,
			min = 1
	)
	@ConfigItem(
			name = "Clock Time",
			keyName = "clockTime",
			description = "",
			position = 1
	)
	@Units(Units.TICKS)
	default int clockTime() { return 4; }

	@ConfigItem(
			name = "Goblin MES",
			keyName = "ToB Purple Crab MES setup for Goblins",
			description = "",
			position = 2
	)
	default boolean goblinMES() { return false; }

	@ConfigItem(
			name = "Player Runway",
			keyName = "Nightmare Runway setup for players",
			description = "",
			position = 3
	)
	default boolean playerRunway() { return false; }

	@ConfigItem(
			name = "Player Arrow",
			keyName = "Nightmare Arrow setup for players",
			description = "",
			position = 4
	)
	default boolean playerArrow() { return false; }

	@ConfigItem(
			name = "LocalPlayer Overlay",
			keyName = "localPlayerOverlay",
			description = "",
			position = 5
	)
	default boolean localPlayerOverlay() { return false; }

	@ConfigItem(
			name = "Projectiles Overlay",
			keyName = "projectilesOverlay",
			description = "",
			position = 6
	)
	default boolean projectilesOverlay() { return false; }

	@ConfigItem(
			name = "Projectile Interacting",
			keyName = "projectilesInteracting",
			description = "",
			position = 7
	)
	default boolean projectilesInteracting() { return false; }

	@ConfigItem(
			name = "Text Outline",
			keyName = "txtOutline",
			description = "",
			position = 8
	)
	default boolean txtOutline() { return false; }

	@Alpha
	@ConfigItem(
			name = "Text Color",
			keyName = "txtColor",
			description = "",
			position = 9
	)
	default Color txtColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			name = "ClientTick MES",
			keyName = "clientTickMES",
			description = "",
			position = 9
	)
	default boolean clientTickMES() { return false; }

	@ConfigItem(
			name = "Test Offset",
			keyName = "testOffset",
			description = "",
			position = 10
	)
	@Units(Units.PIXELS)
	@Range(max = 300, min = -300)
	default int testOffset() { return 0; }

	@ConfigItem(
			name = "Region Tooltip",
			keyName = "regionToolTip",
			description = "",
			position = 9
	)
	default boolean regionToolTip() { return false; }
}
