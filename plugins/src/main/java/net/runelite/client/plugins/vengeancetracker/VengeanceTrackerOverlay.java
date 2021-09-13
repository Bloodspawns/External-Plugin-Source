package net.runelite.client.plugins.vengeancetracker;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

public class VengeanceTrackerOverlay extends Overlay
{
    private VengeanceTrackerPlugin plugin;

    private VengeanceTrackerConfig config;

    @Inject
    private Client client;

    @Inject
    public VengeanceTrackerOverlay(VengeanceTrackerPlugin plugin, VengeanceTrackerConfig config)
    {
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        for (Player p : client.getPlayers())
        {
            if (!config.overlaySelf() && p.equals(client.getLocalPlayer()))
            {
                continue;
            }
            if (plugin.getVengeancedPlayers().contains(Text.sanitize(p.getName() == null ? "" : p.getName())))
            {
                int zOffset = Math.min(p.getLogicalHeight(), 140);
                BufferedImage sprite = plugin.getSprite();
                if (sprite != null)
                {
                    Point point = p.getCanvasImageLocation(sprite, zOffset);
                    if (point != null)
                    {
                        OverlayUtil.renderImageLocation(graphics2D, point, plugin.getSprite());
                    }
                }
            }
        }
        return null;
    }
}
