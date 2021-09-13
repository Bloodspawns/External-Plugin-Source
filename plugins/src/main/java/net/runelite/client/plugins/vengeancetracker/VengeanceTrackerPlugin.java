package net.runelite.client.plugins.vengeancetracker;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GraphicID;
import net.runelite.api.Player;
import net.runelite.api.SpriteID;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "Vengeance Tracker",
        description = "Tracks vengeance cast on other players"
)
@Slf4j
public class VengeanceTrackerPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private VengeanceTrackerOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Getter
    private HashSet<String> vengeancedPlayers;
    private HashMap<String, Integer> vengeancedPlayersPopCycle;
    private static final String OVERHEAD_MESSAGE = "Taste vengeance!";

    private BufferedImage vengeOtherSprite;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private ClientThread clientThread;

    @Provides
    VengeanceTrackerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VengeanceTrackerConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        vengeancedPlayers = new HashSet<>();
        vengeancedPlayersPopCycle = new HashMap<>();
        overlayManager.add(overlay);
    }

    public BufferedImage getSprite()
    {
        if (vengeOtherSprite == null)
        {
            vengeOtherSprite =  spriteManager.getSprite(SpriteID.SPELL_VENGEANCE_OTHER, 0);
        }
        return vengeOtherSprite;
    }

    @Override
    protected void shutDown() throws Exception
    {
        super.shutDown();
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath)
    {
        Actor actor = actorDeath.getActor();
        if (actor instanceof Player)
        {
            Player player = (Player) actor;

            String name = player.getName();
            if (name != null)
            {
                name = Text.sanitize(name);
                vengeancedPlayers.remove(name);
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick)
    {
        for (Player p : client.getPlayers())
        {
            if (p.getGraphic() == GraphicID.VENGEANCE_OTHER || p.getGraphic() == GraphicID.VENGEANCE)
            {
                String name = p.getName();
                if (name != null)
                {
                    String sName = Text.sanitize(name);
                    if (!vengeancedPlayersPopCycle.containsKey(sName) || vengeancedPlayersPopCycle.get(sName) < client.getTickCount() - 5)
                    {
                        vengeancedPlayers.add(sName);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged overheadTextChanged)
    {
        if (overheadTextChanged.getOverheadText().equals(OVERHEAD_MESSAGE))
        {
            String name = overheadTextChanged.getActor().getName();
            if (name != null)
            {
                vengeancedPlayers.remove(Text.sanitize(name));
                vengeancedPlayersPopCycle.put(Text.sanitize(name), client.getTickCount());
            }
        }
    }
}
