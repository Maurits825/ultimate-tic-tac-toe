package com.ultimatetictactoe;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.events.ChatMessage;
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
	private final String playerTwoMove = "o";

	@Inject
	private UltimateTicTacToeConfig config;

	@Inject
	private UltimateTicTacToeOverlay overlay;

	@Inject
	private UltimateTicTacToeView view;

	@Inject
	@Getter //TODO maybe just make a function, we need the overlay to access *this* model, maybe @singleton on model?
	private UltimateTicTacToeModel model;

	@Inject
	private RuneliteObjectController runeliteObjectController;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

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
				boolean isValid = model.playerOneMove(client.getLocalPlayer().getWorldLocation());
				if (isValid)
				{
					log.debug("Player 1 move!");
					view.drawPlayerMove(config.playerOneColor(), client.getLocalPlayer().getWorldLocation());
					view.drawValidTiles(model.getValidTiles());
				}
			}

			if (event.getName().equals(config.playerTwo()) && message.contains(playerTwoMove))
			{
				boolean isValid = model.playerTwoMove(client.getLocalPlayer().getWorldLocation());
				if (isValid)
				{
					log.debug("Player 2 move!");
					view.drawPlayerMove(config.playerTwoColor(), client.getLocalPlayer().getWorldLocation());
					view.drawValidTiles(model.getValidTiles());
				}
			}

			if (event.getName().equals(config.playerOne()) && message.contains("s"))
			{
				RuneLiteObject obj = runeliteObjectController.spawnRuneLiteObject(config.customModelId(), client.getLocalPlayer().getWorldLocation());
				obj.setAnimation(client.loadAnimation(9496));
				obj.setShouldLoop(true);
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
		model.initialize(client.getLocalPlayer().getWorldLocation());
		view.initialize(client.getLocalPlayer().getWorldLocation());
		view.drawValidTiles(model.getValidTiles());
	}

	private void resetGame()
	{
		view.clearAll();
	}

	@Provides
	UltimateTicTacToeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(UltimateTicTacToeConfig.class);
	}
}
