package com.ultimatetictactoe;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

public class UltimateTicTacToeModel
{
	//TODO have a data/constant class so view also uses this
	private final int GRID_SIZE = 9;
	private final int GRID_OFFSET = 5;

	enum State
	{
		PLAYER1_WIN,
		PLAYER2_WIN,
		PLAYER1_MOVE,
		PLAYER2_MOVE,
	}

	//0 is empty, 1 is player, 2 is player 2?
	private int[][] board;

	@Getter
	private State currentState;
	private Point currentGrid;

	private Point topLeftCorner;

	public void initialize(WorldPoint playerWorldPosition)
	{
		WorldPoint topLeftCornerWorld = playerWorldPosition
			.dx(-GRID_OFFSET + 1)
			.dy(GRID_OFFSET - 1);
		topLeftCorner = new Point(topLeftCornerWorld.getX(), topLeftCornerWorld.getY());

		board = new int[GRID_SIZE][GRID_SIZE];
		currentState = State.PLAYER1_MOVE;
		currentGrid = null; //first move can be played anywhere?
	}

	public boolean playerOneMove(WorldPoint worldLocation)
	{
		return move(State.PLAYER1_MOVE, getGridCoordinates(worldLocation));
	}

	public boolean playerOneMove(Point point)
	{
		return move(State.PLAYER1_MOVE, point);
	}

	public boolean playerTwoMove(WorldPoint worldLocation)
	{
		return move(State.PLAYER2_MOVE, getGridCoordinates(worldLocation));
	}

	public boolean playerTwoMove(Point point)
	{
		return move(State.PLAYER2_MOVE, point);
	}

	public List<Point> getValidTiles()
	{
		List<Point> validPoints = new ArrayList<>();
		for (int x = 0; x < GRID_SIZE; x++)
		{
			for (int y = 0; y < GRID_SIZE; y++)
			{
				Point point = new Point(x, y);
				if (isValidMove(currentState, point))
				{
					validPoints.add(point);
				}
			}
		}

		return validPoints;
	}

	private boolean move(State playerMove, Point point)
	{
		if (!isValidMove(playerMove, point))
		{
			return false;
		}

		//play the move
		if (currentState == State.PLAYER1_MOVE)
		{
			board[point.getX()][point.getY()] = 1;
			currentState = State.PLAYER2_MOVE;
		}
		else
		{
			board[point.getX()][point.getY()] = 2;
			currentState = State.PLAYER1_MOVE;
		}

		if (currentGrid == null)
		{
			currentGrid = new Point(point.getX() / 3, point.getY() / 3);
		}

		currentGrid = new Point(point.getX() - (currentGrid.getX() * 3), point.getY() - (currentGrid.getY() * 3));
		return true;
	}

	private boolean isValidMove(State playerMove, Point point)
	{
		if (playerMove != currentState)
		{
			return false;
		}

		//current grid is null -> first move -> any point is valid
		if (currentGrid == null)
		{
			return true;
		}

		//check if move is in current grid
		Point gridLocation = new Point(point.getX() / 3, point.getY() / 3);
		if (!gridLocation.equals(currentGrid))
		{
			return false;
		}

		if (board[point.getX()][point.getY()] != 0)
		{
			return false;
		}

		return true;
	}

	private Point getGridCoordinates(WorldPoint worldPoint)
	{
		return new Point(worldPoint.getX() - topLeftCorner.getX(), topLeftCorner.getY() - worldPoint.getY());
	}
}
