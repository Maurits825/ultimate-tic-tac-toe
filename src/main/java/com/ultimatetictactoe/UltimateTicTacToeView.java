package com.ultimatetictactoe;

import static com.ultimatetictactoe.UltimateTicTacToeConstant.GRID_OFFSET;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.GRID_SIZE;
import static com.ultimatetictactoe.UltimateTicTacToeConstant.SMALL_GRID_SIZE;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.WorldPoint;

public class UltimateTicTacToeView
{
	@Inject
	RuneliteObjectController rlObjController;

	private final int tileObjectId1 = 45510;
	private final int tileObjectId2 = 45432;

	private final int playerMoveTileId = 45488;

	private final int validTileObjectId = 46441;

	private final RuneLiteObject[][] tiles = new RuneLiteObject[GRID_SIZE][GRID_SIZE];

	private final RuneLiteObject[][] playerOneTiles = new RuneLiteObject[GRID_SIZE][GRID_SIZE];
	private final RuneLiteObject[][] playerTwoTiles = new RuneLiteObject[GRID_SIZE][GRID_SIZE];

	private final RuneLiteObject[][] validTiles = new RuneLiteObject[GRID_SIZE][GRID_SIZE];

	private final List<RuneLiteObject[][]> allRuneliteObjectList = Arrays.asList(tiles, playerOneTiles, playerTwoTiles, validTiles);

	private WorldPoint topLeftCornerWorld;

	private Color playerOneColor;
	private Color playerTwoColor;

	public void initialize(WorldPoint playerWorldPosition, Color playerOneColor, Color playerTwoColor)
	{
		this.playerOneColor = playerOneColor;
		this.playerTwoColor = playerTwoColor;

		clearAll();

		topLeftCornerWorld = playerWorldPosition
			.dx(-GRID_OFFSET + 1)
			.dy(GRID_OFFSET - 1);

		spawnTiles();
		spawnValidTiles();
		spawnPlayerMoveTiles();
	}

	public void drawValidTiles(List<Point> validPoints)
	{
		hideAllValidTiles();

		for (Point point : validPoints)
		{
			validTiles[point.getX()][point.getY()].setActive(true);
		}
	}

	public void drawPlayerOneMove(WorldPoint worldPoint)
	{
		Point point = UltimateTicTacToeUtils.getGridPointFromWorld(worldPoint, topLeftCornerWorld);
		playerOneTiles[point.getX()][point.getY()].setActive(true);
	}

	public void drawPlayerTwoMove(WorldPoint worldPoint)
	{
		Point point = UltimateTicTacToeUtils.getGridPointFromWorld(worldPoint, topLeftCornerWorld);
		playerTwoTiles[point.getX()][point.getY()].setActive(true);
	}

	public void drawPlayerOneWins(List<Point> gridWins)
	{
		for (Point gridPoint : gridWins)
		{
			setActivePlayerGrid(playerTwoTiles, gridPoint, false);
			setActivePlayerGrid(playerOneTiles, gridPoint, true);
		}
	}

	public void drawPlayerTwoWins(List<Point> gridWins)
	{
		for (Point gridPoint : gridWins)
		{
			setActivePlayerGrid(playerOneTiles, gridPoint, false);
			setActivePlayerGrid(playerTwoTiles, gridPoint, true);
		}
	}

	public void clearAll()
	{
		for (RuneLiteObject[][] runeLiteObjectList : allRuneliteObjectList)
		{
			for (int x = 0; x < GRID_SIZE; x++)
			{
				for (int y = 0; y < GRID_SIZE; y++)
				{
					if (runeLiteObjectList[x][y] != null)
					{
						runeLiteObjectList[x][y].setActive(false);
					}
				}
			}
		}
	}

	private void setActivePlayerGrid(RuneLiteObject[][] playerTiles, Point gridPoint, boolean setActive)
	{
		for (int x = 0; x < SMALL_GRID_SIZE; x++)
		{
			for (int y = 0; y < SMALL_GRID_SIZE; y++)
			{
				playerTiles[gridPoint.getX() * SMALL_GRID_SIZE + x][gridPoint.getY() * SMALL_GRID_SIZE + y].setActive(setActive);
			}
		}
	}

	private void hideAllValidTiles()
	{
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				if (validTiles[x][y] != null)
				{
					validTiles[x][y].setActive(false);
				}
			}
		}
	}

	private void spawnTiles()
	{
		int tileObjectId;
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				if ((x / 3 + y / 3) % 2 == 0)
				{
					tileObjectId = tileObjectId1;
				}
				else
				{
					tileObjectId = tileObjectId2;
				}
				tiles[x][y] = rlObjController.spawnRuneLiteObject(tileObjectId, topLeftCornerWorld.dy(-y).dx(x));
			}
		}
	}

	private void spawnPlayerMoveTiles()
	{
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				playerOneTiles[x][y] = rlObjController.spawnRuneLiteObject(
					playerMoveTileId, topLeftCornerWorld.dy(-y).dx(x), false, playerOneColor, 33);

				playerTwoTiles[x][y] = rlObjController.spawnRuneLiteObject(
					playerMoveTileId, topLeftCornerWorld.dy(-y).dx(x), false, playerTwoColor, 33);
			}
		}
	}

	private void spawnValidTiles()
	{
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				validTiles[x][y] = rlObjController.spawnRuneLiteObject(
					validTileObjectId, topLeftCornerWorld.dy(-y).dx(x), false,
					0, 50, 0, 9496
				);
			}
		}
	}
}
