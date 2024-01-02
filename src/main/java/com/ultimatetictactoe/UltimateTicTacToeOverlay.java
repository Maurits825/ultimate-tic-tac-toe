package com.ultimatetictactoe;

import static com.ultimatetictactoe.UltimateTicTacToeConstant.PLAYER1_MOVE_MESSAGE;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.PLAYER2_MOVE_MESSAGE;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class UltimateTicTacToeOverlay extends OverlayPanel
{
	private Client client;
	private UltimateTicTacToePlugin plugin;
	private UltimateTicTacToeConfig config;

	@Inject
	UltimateTicTacToeOverlay(UltimateTicTacToePlugin plugin, Client client, UltimateTicTacToeConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.TOP_LEFT);
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, "Start", "new game"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		String title = "Ultimate tic-tac-toe";
		Color color = Color.GREEN;

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(title)
			.color(color)
			.build());

		String statusText = "-";
		switch (plugin.getModel().getCurrentState())
		{
			case PLAYER1_MOVE:
				statusText = "Player 1 - " + PLAYER1_MOVE_MESSAGE;
				break;
			case PLAYER2_MOVE:
				statusText = "Player 2 - " + PLAYER2_MOVE_MESSAGE;
				break;
			case PLAYER1_WIN:
				statusText = "Player 1 Win!";
				break;
			case PLAYER2_WIN:
				statusText = "Player 2 Win!";
				break;
			case DRAW:
				statusText = "Draw";
				break;
			default:
				break;
		}
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(statusText)
			.build());

		return super.render(graphics);
	}
}
