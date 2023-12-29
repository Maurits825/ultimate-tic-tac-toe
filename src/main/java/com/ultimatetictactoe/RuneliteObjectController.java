package com.ultimatetictactoe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class RuneliteObjectController
{
	@Inject
	private Client client;

	private final int GRID_SIZE = 9;
	private final int GRID_OFFSET = 5;

	private final List<RuneLiteObject> walls = new ArrayList<>();
	private final List<RuneLiteObject> tiles = new ArrayList<>();

	private final List<RuneLiteObject> playerOneTiles = new ArrayList<>();
	private final List<RuneLiteObject> playerTwoTiles = new ArrayList<>();

	private final List<List<RuneLiteObject>> allRuneliteObjectList = Arrays.asList(walls, tiles, playerOneTiles, playerTwoTiles);

	public void drawGrid(int wallObjectId, int tileObjectId1, int tileObjectId2, WorldPoint playerWorldPosition)
	{
		clearAll();

		//drawWalls(wallObjectId, playerWorldPosition);
		drawTiles(tileObjectId1, tileObjectId2, playerWorldPosition);
	}

	public void drawPlayerOneMove(int playerOneId, Color playerOneColor, WorldPoint worldPoint)
	{
		playerOneTiles.add(spawnRuneLiteObject(playerOneId, worldPoint, playerOneColor, 33));
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
			walls.add(spawnRuneLiteObject(wallObjectId, wallStartPoint.dx(x)));
		}

		for (int x = 0; x < GRID_SIZE + 2; x++)
		{
			walls.add(spawnRuneLiteObject(wallObjectId, wallStartPoint.dx(x).dy(-GRID_OFFSET * 2)));
		}

		for (int y = 0; y < GRID_SIZE; y++)
		{
			walls.add(spawnRuneLiteObject(wallObjectId, wallStartPoint.dy(-y - 1)));
		}

		for (int y = 0; y < GRID_SIZE; y++)
		{
			walls.add(spawnRuneLiteObject(wallObjectId, wallStartPoint.dy(-y - 1).dx(GRID_OFFSET * 2)));
		}
	}

	private void drawTiles(int tileObjectId1, int tileObjectId2, WorldPoint playerWorldPosition)
	{
		WorldPoint wallStartPoint = playerWorldPosition
			.dx(-GRID_OFFSET + 1)
			.dy(GRID_OFFSET - 1);

		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				int tileObjectId;

				if ((x / 3 + y / 3) % 2 == 0)
				{
					tileObjectId = tileObjectId1;
				}
				else
				{
					tileObjectId = tileObjectId2;
				}
				tiles.add(spawnRuneLiteObject(tileObjectId, wallStartPoint.dy(-y).dx(x)));
			}
		}
	}

	private RuneLiteObject spawnRuneLiteObject(int objectId, WorldPoint point)
	{
		RuneLiteObject obj = client.createRuneLiteObject();

		Model model = client.loadModel(objectId);
		obj.setModel(model);

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		obj.setLocation(lp, client.getPlane());
		obj.setActive(true);

		return obj;
	}

	private RuneLiteObject spawnRuneLiteObject(int objectId, WorldPoint point, Color color, int colorIndex)
	{
		RuneLiteObject obj = client.createRuneLiteObject();

		ModelData modelData = client.loadModelData(objectId).cloneColors();
		modelData.recolor(modelData.getFaceColors()[colorIndex], JagexColor.rgbToHSL(color.getRGB(), 1.0d));

		obj.setModel(modelData.light());

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		obj.setLocation(lp, client.getPlane());
		obj.setActive(true);

		return obj;
	}
}
