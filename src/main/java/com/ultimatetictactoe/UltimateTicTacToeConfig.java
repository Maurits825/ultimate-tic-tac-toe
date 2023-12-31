package com.ultimatetictactoe;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ultimatetictactoe")
public interface UltimateTicTacToeConfig extends Config
{
	@ConfigItem(
		keyName = "playerOne",
		name = "Player 1",
		description = "Enter the RSN of player 1"
	)
	default String playerOne()
	{
		return "";
	}

	@ConfigItem(
		keyName = "playerOneColor",
		name = "Player 1 color",
		description = "Color of player 1 tile"
	)
	default Color playerOneColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "playerTwo",
		name = "Player 2",
		description = "Enter the RSN of player 2"
	)
	default String playerTwo()
	{
		return "";
	}

	@ConfigItem(
		keyName = "playerTwoColor",
		name = "Player 2 color",
		description = "Color of player 2 tile"
	)
	default Color playerTwoColor()
	{
		return Color.BLUE;
	}
}
