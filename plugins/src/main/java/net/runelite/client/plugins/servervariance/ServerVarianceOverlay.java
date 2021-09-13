package net.runelite.client.plugins.servervariance;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ServerVarianceOverlay extends Overlay
{
	private ServerVariancePlugin plugin;
	private ServerVarianceConfig config;
	private final String postfix = "ms";
	private final long defaultVariance = 600;

	@Inject
	private ServerVarianceOverlay(ServerVariancePlugin plugin, ServerVarianceConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.TOP_LEFT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		graphics.setFont(FontManager.getRunescapeFont());
		FontMetrics fontMetrics = graphics.getFontMetrics();

		int defaultWidth = fontMetrics.stringWidth("000 ms");
		if (plugin.getDeltaTime() < 0)
		{
			return new Dimension(defaultWidth, fontMetrics.getHeight());
		}

		Color color = Color.YELLOW;
		if (config.colorOverlay() && Math.abs(plugin.getDeltaTime() - defaultVariance) > 100)
		{
			color = Color.RED;
		}

		long time = plugin.getDeltaTime();
		if (config.showRelative())
		{
			time = Math.abs(plugin.getDeltaTime() - defaultVariance);
		}
		String text = time + " " + postfix;
		int width = fontMetrics.stringWidth(text);
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, defaultWidth - width + 1, fontMetrics.getMaxAscent() + 1);

		graphics.setColor(color);
		graphics.drawString(text, defaultWidth - width, fontMetrics.getMaxAscent());
		return new Dimension(defaultWidth, fontMetrics.getHeight());
	}
}
