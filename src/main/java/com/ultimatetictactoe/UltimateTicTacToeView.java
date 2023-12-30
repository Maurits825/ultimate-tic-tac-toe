package com.ultimatetictactoe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.WorldPoint;

public class UltimateTicTacToeView
{
	@Inject
	RuneliteObjectController rlObjController;

	private final int wallModelId = 45818;

	private final int tileObjectId1 = 45510;
	private final int tileObjectId2 = 45432;

	private final int playerMoveTileId = 45488;

	private final int validTileObjectId = 46441;

	private final int GRID_SIZE = 9;
	private final int GRID_OFFSET = 5;

	private final List<RuneLiteObject> walls = new ArrayList<>();
	private final List<RuneLiteObject> tiles = new ArrayList<>();

	private final List<RuneLiteObject> playerOneTiles = new ArrayList<>();
	private final List<RuneLiteObject> playerTwoTiles = new ArrayList<>();

	private final RuneLiteObject[][] validTiles = new RuneLiteObject[GRID_SIZE][GRID_SIZE];

	private final List<List<RuneLiteObject>> allRuneliteObjectList = Arrays.asList(walls, tiles, playerOneTiles, playerTwoTiles);

	public void initialize(WorldPoint playerWorldPosition)
	{
		clearAll();

		WorldPoint topLeftCorner = playerWorldPosition
			.dx(-GRID_OFFSET + 1)
			.dy(GRID_OFFSET - 1);

		//drawWalls(wallObjectId, playerWorldPosition);
		drawTiles(topLeftCorner);
		spawnValidTiles(topLeftCorner);
	}

	public void drawPlayerOneMove(Color playerOneColor, WorldPoint worldPoint)
	{
		playerOneTiles.add(rlObjController.spawnRuneLiteObject(playerMoveTileId, worldPoint, playerOneColor, 33));
	}

	public void clearAll()
	{
		for (List<RuneLiteObject> runeLiteObjectList : allRuneliteObjectList)
		{
			for (RuneLiteObject obj : runeLiteObjectList)
			{
				obj.setActive(false);
			}
			runeLiteObjectList.clear();
		}
	}

	private void drawWalls(int wallObjectId, WorldPoint playerWorldPosition)
	{
		WorldPoint wallStartPoint = playerWorldPosition
			.dx(-GRID_OFFSET)
			.dy(GRID_OFFSET);

		for (int x = 0; x < GRID_SIZE + 2; x++)
		{
			walls.add(rlObjController.spawnRuneLiteObject(wallObjectId, wallStartPoint.dx(x)));
		}

		for (int x = 0; x < GRID_SIZE + 2; x++)
		{
			walls.add(rlObjController.spawnRuneLiteObject(wallObjectId, wallStartPoint.dx(x).dy(-GRID_OFFSET * 2)));
		}

		for (int y = 0; y < GRID_SIZE; y++)
		{
			walls.add(rlObjController.spawnRuneLiteObject(wallObjectId, wallStartPoint.dy(-y - 1)));
		}

		for (int y = 0; y < GRID_SIZE; y++)
		{
			walls.add(rlObjController.spawnRuneLiteObject(wallObjectId, wallStartPoint.dy(-y - 1).dx(GRID_OFFSET * 2)));
		}
	}

	private void drawTiles(WorldPoint topLeftCorner)
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
				tiles.add(rlObjController.spawnRuneLiteObject(tileObjectId, topLeftCorner.dy(-y).dx(x)));
			}
		}
	}

	private void spawnValidTiles(WorldPoint topLeftCorner)
	{
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				validTiles[x][y] = rlObjController.spawnRuneLiteObject(
					validTileObjectId, topLeftCorner.dy(-y).dx(x), false,
					0, 50, 0, 9496
				);
			}
		}
	}
}
