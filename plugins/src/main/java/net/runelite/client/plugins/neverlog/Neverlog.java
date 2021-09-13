//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.runelite.client.plugins.neverlog;

import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Never Log",
	description = "Presses backspace at random interval to prevent you from logging out."
)
@Slf4j
public class Neverlog extends Plugin
{

	@Inject
	private Client client;
	private final Random random = new Random();
	private long selectedIdle;
	private static final int LOGOUT_IDLE = 15000;
	private static final int MIN_IDLE = LOGOUT_IDLE - 8000;
	private static final int MAX_IDLE = LOGOUT_IDLE - 1500; // 30 seconds off 5 mins;
	private static final int[] KEY_CODES = new int[1];
	private static final char[] KEY_CHARS = new char[1];

	protected void startUp()
	{
		selectedIdle = this.randomDelay();

		KEY_CODES[0] = '\b';
		KEY_CHARS[0] = '\b';
//		KEY_CHARS[1]= ' ';
//		KEY_CODES[1]= ' ';
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (checkIdleLogout())
		{
			selectedIdle = randomDelay();
			SwingUtilities.invokeLater(this::pressKey);
		}
	}

	private boolean checkIdleLogout()
	{
		int currentIdle = Math.min(client.getMouseIdleTicks(), client.getKeyboardIdleTicks());

		return currentIdle >= selectedIdle;
	}

	private int randomDelay()
	{
		return random.nextInt(MAX_IDLE - MIN_IDLE) + MIN_IDLE;
	}

	private void pressKey()
	{
//		int index = random.nextInt(KEY_CODES.length);
		int index = 0;
		int keyCode = KEY_CODES[index];
		char keyChar = KEY_CHARS[index];
		KeyEvent press = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
		client.getCanvas().dispatchEvent(press);
		KeyEvent typed = new KeyEvent(client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, keyChar);
		client.getCanvas().dispatchEvent(typed);
		KeyEvent release = new KeyEvent(client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
		client.getCanvas().dispatchEvent(release);
	}
}
