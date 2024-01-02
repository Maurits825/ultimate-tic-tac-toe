package com.ultimatetictactoe;

import com.google.inject.Provides;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.PLAYER1_MOVE_MESSAGE;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.PLAYER2_MOVE_MESSAGE;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Ultimate tic-tac-toe"
)
public class UltimateTicTacToePlugin extends Plugin
{
	@Inject
	private UltimateTicTacToeConfig config;

	@Inject
	private UltimateTicTacToeOverlay overlay;

	@Inject
	private UltimateTicTacToeView view;

	@Inject
	@Getter
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
		resetGame();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		resetGame();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() == ChatMessageType.PUBLICCHAT)
		{
			String message = Text.sanitize(Text.removeTags(event.getMessage())).toLowerCase();
			WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
			if (event.getName().equals(config.playerOne()) && message.contains(PLAYER1_MOVE_MESSAGE))
			{
				playerOneMove(playerLocation);
			}
			else if (event.getName().equals(config.playerTwo()) && message.contains(PLAYER2_MOVE_MESSAGE))
			{
				//TODO have to get location of other player here?
				playerTwoMove(playerLocation);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (config.isBotEnabled() && model.getCurrentState() == UltimateTicTacToeModel.State.PLAYER2_MOVE)
		{
			List<Point> validPoints = model.getValidTiles();
			Point move = validPoints.get(ThreadLocalRandom.current().nextInt(0, validPoints.size()));
			playerTwoMove(UltimateTicTacToeUtils.getWorldPointFromGrid(move, model.getTopLeftCornerWorld()));
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			resetGame();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (!client.isMenuOpen())
		{
			client.createMenuEntry(-1)
				.setOption("Play")
				.setTarget(ColorUtil.wrapWithColorTag(PLAYER1_MOVE_MESSAGE, config.playerOneColor()))
				.setType(MenuAction.RUNELITE)
				.onClick(c -> playerOneMove(client.getSelectedSceneTile().getWorldLocation()));

			client.createMenuEntry(-2)
				.setOption("Play")
				.setTarget(ColorUtil.wrapWithColorTag(PLAYER2_MOVE_MESSAGE, config.playerTwoColor()))
				.setType(MenuAction.RUNELITE)
				.onClick(c -> playerTwoMove(client.getSelectedSceneTile().getWorldLocation()));
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
			resetGame();
		}
	}

	private void startGame()
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		model.initialize(playerLocation);
		view.initialize(playerLocation, config.playerOneColor(), config.playerTwoColor());
		view.drawValidTiles(model.getValidTiles());
	}

	private void resetGame()
	{
		model.reset();
		clientThread.invokeLater(() ->
		{
			view.clearAll();
			return true;
		});
	}

	private void playerOneMove(WorldPoint playerLocation)
	{
		boolean isValidMove = model.playerOneMove(playerLocation);
		if (isValidMove)
		{
			view.drawPlayerOneMove(playerLocation);
			updateViewPostMove();
		}
	}

	private void playerTwoMove(WorldPoint playerLocation)
	{
		boolean isValidMove = model.playerTwoMove(playerLocation);
		if (isValidMove)
		{
			view.drawPlayerTwoMove(playerLocation);
			updateViewPostMove();
		}
	}

	private void updateViewPostMove()
	{
		view.drawValidTiles(model.getValidTiles());
		view.drawPlayerOneWins(model.getPlayerOneWins());
		view.drawPlayerTwoWins(model.getPlayerTwoWins());
	}

	@Provides
	UltimateTicTacToeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(UltimateTicTacToeConfig.class);
	}
}
