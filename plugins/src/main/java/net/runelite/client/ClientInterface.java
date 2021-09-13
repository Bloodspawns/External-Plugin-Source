package net.runelite.client;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.api.SpritePixels;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

@Slf4j
public class ClientInterface
{
	private static Method getRotation = null;
	private static Method getInteracting = null;
	private static Method setLoginScreenBackgroundPixels = null;
	private static Method rightSpriteOverwrite = null;
	private static Method leftSpriteOverwrite = null;
	private static HashSet<String> getNpcsToHide = null;
	private static HashSet<String> getNpcsToHideOnDeath = null;
	private static HashSet<Integer> getNpcsByAnimationToHideOnDeath = null;
	private static HashSet<Integer> getNpcsByIdToHideOnDeath = null;
	private static Method setDeadNPCsHidden = null;

	// !!!Deprecated. Use Actor.getCurrentOrientation instead.
	// The rotation of the actor object. This is different from the orientation which is the world space rotation.
	public static int getRotation(Actor actor)
	{
		if (getRotation == null)
		{
			try
			{
				getRotation = actor.getClass().getMethod("getRotation");
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getRotation for class {}", actor.getClass());
				return -1;
			}
		}

		try
		{
			return (int) getRotation.invoke(actor);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method getRotation for class {}", actor.getClass());
		}
		return -1;
	}

	// Set if the given renderable object will be hidden or not
	public static void setHidden(Renderable renderable, boolean hidden)
	{
		Method setHidden = null;
		try
		{
			setHidden = renderable.getClass().getMethod("setHidden", boolean.class);
		}
		catch (NoSuchMethodException e)
		{
			log.debug("Couldn't find method setHidden for class {}", renderable.getClass());
			return;
		}

		try
		{
			setHidden.invoke(renderable, hidden);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method setHidden for class {}", renderable.getClass());
		}
	}

	// Get if the given renderable object will be hidden or not
	public static boolean getHidden(Renderable renderable)
	{
		Method getHidden = null;
		try
		{
			getHidden = renderable.getClass().getMethod("getHidden");
		}
		catch (NoSuchMethodException e)
		{
			log.debug("Couldn't find method getHidden for class {}", renderable.getClass());
			return false;
		}

		try
		{
			return (boolean)getHidden.invoke(renderable);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method getHidden for class {}", renderable.getClass());
		}
		return false;
	}

	// Get the Actor object for which the given projectile is interacting with
	public static Actor getInteracting(Projectile p)
	{
		if (getInteracting == null)
		{
			try
			{
				getInteracting = p.getClass().getMethod("getInteracting");
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getInteracting for class {}", p.getClass());
				return null;
			}
		}

		try
		{
			return (Actor) getInteracting.invoke(p);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method getInteracting for class {}", p.getClass());
		}
		return null;
	}

	// Set the login background sprite
	public static void setLoginScreenBackgroundPixels(Client client, SpritePixels spritePixels)
	{
		if (setLoginScreenBackgroundPixels == null)
		{
			try
			{
				setLoginScreenBackgroundPixels = client.getClass().getMethod("setLoginScreenBackgroundPixels", SpritePixels.class);
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method setLoginScreenBackgroundPixels for class {}", client.getClass());
				return;
			}
		}

		try
		{
			setLoginScreenBackgroundPixels.invoke(client, spritePixels);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method setLoginScreenBackgroundPixels for class {}", client.getClass());
		}
	}

	// Force the left side of the login screen sprite to be refreshed
	public static void leftSpriteOverwrite(Client client)
	{
		if (leftSpriteOverwrite == null)
		{
			try
			{
				leftSpriteOverwrite = client.getClass().getMethod("leftSpriteOverwrite");
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method leftSpriteOverwrite for class {}", client.getClass());
				return;
			}
		}

		try
		{
			leftSpriteOverwrite.invoke(client);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method leftSpriteOverwrite for class {}", client.getClass());
		}
	}

	// Force the right side of the login screen sprite to be refreshed
	public static void rightSpriteOverwrite(Client client)
	{
		if (rightSpriteOverwrite == null)
		{
			try
			{
				rightSpriteOverwrite = client.getClass().getMethod("rightSpriteOverwrite");
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method rightSpriteOverwrite for class {}", client.getClass());
				return;
			}
		}

		try
		{
			rightSpriteOverwrite.invoke(client);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method rightSpriteOverwrite for class {}", client.getClass());
		}
	}

	// Set if any npc which dies should be hidden immediately
	public static void setDeadNPCsHidden(Client client, boolean hidden)
	{
		if (setDeadNPCsHidden == null)
		{
			try
			{
				setDeadNPCsHidden = client.getClass().getMethod("setDeadNPCsHidden", boolean.class);
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method setDeadNPCsHidden for class {}", client.getClass());
				return;
			}
		}

		try
		{
			setDeadNPCsHidden.invoke(client, hidden);
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			log.debug("Couldn't call method setDeadNPCsHidden for class {}", client.getClass());
		}
	}

	// Get the set of npc names which will be hidden at all times
	public static HashSet<String> getNpcsToHide(Client client)
	{
		if (getNpcsToHide == null)
		{
			try
			{
				Method m = client.getClass().getMethod("getNpcsToHide");

				try
				{
					getNpcsToHide = (HashSet<String>) m.invoke(client);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					log.debug("Couldn't call method getNpcsToHide for class {}", client.getClass());
					return null;
				}
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getNpcsToHide for class {}", client.getClass());
				return null;
			}
		}
		return getNpcsToHide;
	}

	// Get the set of npc names which will be hidden when they die
	public static HashSet<String> getNpcsToHideOnDeath(Client client)
	{
		if (getNpcsToHideOnDeath == null)
		{
			try
			{
				Method m = client.getClass().getMethod("getNpcsToHideOnDeath");

				try
				{
					getNpcsToHideOnDeath = (HashSet<String>) m.invoke(client);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					log.debug("Couldn't call method getNpcsToHideOnDeath for class {}", client.getClass());
					return null;
				}
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getNpcsToHideOnDeath for class {}", client.getClass());
				return null;
			}
		}
		return getNpcsToHideOnDeath;
	}

	// Get the set of npc animation ids which will be hidden when the animation occurs
	public static HashSet<Integer> getNpcsByAnimationToHideOnDeath(Client client)
	{
		if (getNpcsByAnimationToHideOnDeath == null)
		{
			try
			{
				Method m = client.getClass().getMethod("getNpcsByAnimationToHideOnDeath");

				try
				{
					getNpcsByAnimationToHideOnDeath = (HashSet<Integer>) m.invoke(client);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					log.debug("Couldn't call method getNpcsByAnimationToHideOnDeath for class {}", client.getClass());
					return null;
				}
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getNpcsByAnimationToHideOnDeath for class {}", client.getClass());
				return null;
			}
		}
		return getNpcsByAnimationToHideOnDeath;
	}

	// Get the set of npc ids which will be hidden when they die
	public static HashSet<Integer> getNpcsByIdToHideOnDeath(Client client)
	{
		if (getNpcsByIdToHideOnDeath == null)
		{
			try
			{
				Method m = client.getClass().getMethod("getNpcsByIdToHideOnDeath");

				try
				{
					getNpcsByIdToHideOnDeath = (HashSet<Integer>) m.invoke(client);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					log.debug("Couldn't call method getNpcsByIdToHideOnDeath for class {}", client.getClass());
					return null;
				}
			}
			catch (NoSuchMethodException e)
			{
				log.debug("Couldn't find method getNpcsByIdToHideOnDeath for class {}", client.getClass());
				return null;
			}
		}
		return getNpcsByIdToHideOnDeath;
	}
}