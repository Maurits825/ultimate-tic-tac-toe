package com.ultimatetictactoe;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Ultimate tic-tac-toe"
)
public class UltimateTicTacToePlugin extends Plugin
{
	private final String playerOneMove = "x";
	private final String playerTwoMove = "x";

	private final int wallModelId = 45818;

	private final int tileId1 = 45510;
	private final int tileId2 = 45432;

	private final int playerMoveTileId = 45488;

	@Inject
	private Client client;

	@Inject
	private UltimateTicTacToeConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private UltimateTicTacToeOverlay overlay;

	@Inject
	private RuneliteObjectController runeliteObjectController;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);

		clientThread.invokeLater(() ->
		{
			resetGame();
			return true;
		});

	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.PUBLICCHAT)
		{
			String message = Text.sanitize(Text.removeTags(event.getMessage())).toLowerCase();
			if (event.getName().equals(config.playerOne()) && message.contains(playerOneMove))
			{
				//player one move
				//TODO
				// get the tile of this player now, this is the location of the move
				// validate move -> some feedback?
				// play the move
				log.debug("Spawning player one obj");
				runeliteObjectController.drawPlayerOneMove(playerMoveTileId, config.playerOneColor(), client.getLocalPlayer().getWorldLocation());
			}

			if (event.getName().equals(config.playerTwo()) && message.contains(playerTwoMove))
			{
				//player two move

			}
		}
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked overlayMenuClicked)
	{
		OverlayMenuEntry overlayMenuEntry = overlayMenuClicked.getEntry();
		if (overlayMenuEntry.getMenuAction() == MenuAction.RUNELITE_OVERLAY
			&& overlayMenuClicked.getEntry().getOption().equals("Start")
			&& overlayMenuClicked.getOverlay() == overlay)
		{
			startGame();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("ultimatetictactoe"))
		{
//			clientThread.invokeLater(() ->
//			{
//				runeliteObjectController.drawGrid(wallModelId, config.tileId(), client.getLocalPlayer().getWorldLocation());
//				return true;
//			});
		}
	}

	private void startGame()
	{
		log.debug("Starting Game");
		runeliteObjectController.drawGrid(wallModelId, tileId1, tileId2, client.getLocalPlayer().getWorldLocation());
	}

	private void resetGame()
	{
		runeliteObjectController.clearAll();
	}

	@Provides
	UltimateTicTacToeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(UltimateTicTacToeConfig.class);
	}
}
