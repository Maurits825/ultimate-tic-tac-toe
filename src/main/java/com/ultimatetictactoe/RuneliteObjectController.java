package com.ultimatetictactoe;

import java.awt.Color;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class RuneliteObjectController
{
	@Inject
	private Client client;

	public RuneLiteObject spawnRuneLiteObject(int objectId, WorldPoint point)
	{
		return spawnRuneLiteObject(
			objectId, point, true, 0, 0, 0, null, 0, -1
		);
	}

	public RuneLiteObject spawnRuneLiteObject(int objectId, WorldPoint point, boolean setActive, Color color, int colorIndex)
	{
		return spawnRuneLiteObject(
			objectId, point, setActive, 0, 0, 0, color, colorIndex, -1
		);
	}

	public RuneLiteObject spawnRuneLiteObject(
		int objectId, WorldPoint point, boolean setActive,
		int translateX, int translateY, int translateZ,
		int animationId
	)
	{
		return spawnRuneLiteObject(
			objectId, point, setActive, translateX, translateY, translateZ, null, 0, animationId
		);
	}

	private RuneLiteObject spawnRuneLiteObject(int objectId, WorldPoint point, boolean setActive, int translateX, int translateY, int translateZ, Color color, int colorIndex, int animationId)
	{
		RuneLiteObject obj = client.createRuneLiteObject();

		ModelData modelData = client.loadModelData(objectId)
			.cloneVertices()
			.translate(translateX, translateY, translateZ)
			.cloneColors();

		if (color != null)
		{
			modelData.recolor(modelData.getFaceColors()[colorIndex], JagexColor.rgbToHSL(color.getRGB(), 1.0d));
		}

		obj.setModel(modelData.light());

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		obj.setLocation(lp, client.getPlane());

		if (animationId != -1)
		{
			obj.setAnimation(client.loadAnimation(animationId));
			obj.setShouldLoop(true);
		}

		if (setActive)
		{
			obj.setActive(true);
		}

		return obj;
	}
}
