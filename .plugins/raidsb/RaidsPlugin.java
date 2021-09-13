/*
 * Copyright (c) 2018, Kamiel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.raidsb;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
	name = "Chambers Of Xeric Points",
	description = "Show Points information for the Chambers of Xeric raid",
	tags = {"combat", "raid", "overlay", "pve", "pvm", "bosses"}
)
@Slf4j
public class RaidsPlugin extends Plugin
{
	private static final String RAID_START_MESSAGE = "The raid has begun!";
	private static final String KC_MESSAGE = "Your completed Chambers of Xeric count is:";
	private static final String KC_MESSAGECM = "Your completed Chambers of Xeric Challenge Mode count is:";
	private static final String RAID_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";
	private static final String RAID_COMPLETE_MESSAGE2 = "Congratulations - your raid is complete! Duration:";
	private static final Pattern RAIDS_DURATION_PATTERN = Pattern.compile("Duration: (?<duration>[0-9:]+)");

	@Inject
	private SkillIconManager iconManager;
	@Inject
	private Client client;

	@Inject
	private net.runelite.client.plugins.raidsb.RaidsConfig config;

	private PointsPanel pointsPanel;
	private NavigationButton navButton2;

	@Inject
	private ClientToolbar clientToolbar;
	data raidToAdd = new data();
	int ticks = 0;

	@Provides
	net.runelite.client.plugins.raidsb.RaidsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RaidsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		ticks = 0;
		pointsPanel = new PointsPanel(this, config, client, iconManager);
		pointsPanel.init();
		if (config.soloPanel())
		{
			pointsPanel.setSolo(true);
			pointsPanel.updateSolo();
		}
		else
		{
			pointsPanel.setSolo(false);
			pointsPanel.update();
		}

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(this.getClass(), "instancereloadhelper.png");
		final BufferedImage iconPoint = ImageUtil.getResourceStreamFromClass(this.getClass(), "cox.png");

		navButton2 = NavigationButton.builder()
			.tooltip("CoX Points")
			.icon(iconPoint)
			.priority(9)
			.panel(pointsPanel)
			.build();
		if (config.ptsPanel())
		{
			clientToolbar.addNavigation(navButton2);
		}
	}


	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton2);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("raidsb"))
		{
			return;
		}
		if (config.ptsPanel())
		{
			if (config.soloPanel())
			{
				pointsPanel.updateSolo();
				pointsPanel.setSolo(true);
			}
			else
			{
				pointsPanel.update();
				pointsPanel.setSolo(false);
			}
			pointsPanel.revalidate();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		ticks++;
		if (ticks > 20)
		{
			if (config.soloPanel())
			{
				pointsPanel.updateSolo();
			}
			else
			{
				pointsPanel.update();
			}
			ticks = 0;
		}
		pointsPanel.updateTime();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String text = Text.removeTags(event.getMessage());
		if (raidToAdd != null)
		{
			raidToAdd.cm = false;
		}
		if (text.startsWith(KC_MESSAGE))
		{
			pointsPanel.raids.get(pointsPanel.raids.size() - 1).kc = Integer.parseInt(text.replaceAll("\\D+", ""));
			pointsPanel.raids.get(pointsPanel.raids.size() - 1).cm = false;

			if (config.soloPanel())
			{
				pointsPanel.updateSolo();
			}
			else
			{
				pointsPanel.update();
			}

			//raidToAdd.kc = Integer.parseInt(text.replaceAll("\\D+",""));
		}
		if (text.startsWith(KC_MESSAGECM))
		{
			pointsPanel.raids.get(pointsPanel.raids.size() - 1).kc = Integer.parseInt(text.replaceAll("\\D+", ""));
			pointsPanel.raids.get(pointsPanel.raids.size() - 1).cm = true;

			if (config.soloPanel())
			{
				pointsPanel.updateSolo();
			}
			else
			{
				pointsPanel.update();
			}

			//raidToAdd.kc = Integer.parseInt(text.replaceAll("\\D+",""));
		}
		if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			String message = Text.removeTags(event.getMessage());
			if (message.startsWith(RAID_START_MESSAGE))
			{
				raidToAdd.start = new Date();
			}
			if (message.startsWith(RAID_COMPLETE_MESSAGE))
			{
				raidToAdd.finish = new Date();
			}

			if (message.startsWith("Congratulations - your raid is complete!"))
			{
				Matcher matcher2 = RAIDS_DURATION_PATTERN.matcher(message);
				if (matcher2.find())
				{
					parseTime(matcher2);
				}
			}
		}
	}

	private static int timeStringToSeconds(String timeString)
	{
		String[] s = timeString.split(":");
		if (s.length == 2) // mm:ss
		{
			return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
		}
		else if (s.length == 3) // h:mm:ss
		{
			return Integer.parseInt(s[0]) * 60 * 60 + Integer.parseInt(s[1]) * 60 + Integer.parseInt(s[2]);
		}
		return Integer.parseInt(timeString);
	}

	private void parseTime(Matcher matcher)
	{
		int seconds = timeStringToSeconds(matcher.group("duration"));
		raidToAdd.timeTaken = seconds;
		raidToAdd.personal = client.getVar(Varbits.PERSONAL_POINTS);
		raidToAdd.total = client.getVar(Varbits.TOTAL_POINTS);
		raidToAdd.hr = (int) (((float) raidToAdd.personal / (float) raidToAdd.timeTaken) * 3600);
		if (config.ptsPanel())
		{
			log.info("RAID TIME: {}", seconds);
			pointsPanel.raids.add(raidToAdd);
			if (!pointsPanel.timer.started)
			{
				pointsPanel.timer.start(raidToAdd.timeTaken);
			}
			if (config.soloPanel())
			{
				pointsPanel.updateSolo();
			}
			else
			{
				pointsPanel.update();
			}
			raidToAdd = new data();
		}
	}
}
