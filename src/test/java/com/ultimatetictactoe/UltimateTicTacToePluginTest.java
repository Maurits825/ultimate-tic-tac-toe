package com.ultimatetictactoe;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class UltimateTicTacToePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(UltimateTicTacToePlugin.class);
		RuneLite.main(args);
	}
}