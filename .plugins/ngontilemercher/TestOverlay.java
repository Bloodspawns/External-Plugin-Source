package net.runelite.client.plugins.testplugin;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
public class TestOverlay extends Overlay
{
	private final Client client;
	private final TestConfig config;

	private final TestPlugin plugin;

	public static final int LOCAL_COORD_BITS = 7;
	public static final int LOCAL_TILE_SIZE = 1 << LOCAL_COORD_BITS; // 128 - size of a tile in local coordinates
	public static final int LOCAL_HALF_TILE_SIZE = LOCAL_TILE_SIZE / 2;
	public static final int SCENE_SIZE = Constants.SCENE_SIZE; // in tiles

	@Inject
	private TestOverlay(Client client, TestPlugin plugin, TestConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}
		Player p = client.getLocalPlayer();

		renderConcaveAoe(graphics, client, LocalPoint.fromWorld(client, p.getWorldLocation()), p.getWorldArea(), 13);
		return null;
	}

	static class Int2
	{
		int x, y;

		Int2(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode()
		{
			int result = 17;
			result = 31 * result + x;
			result = 31 * result + y;
			return result;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || getClass() != o.getClass())
			{
				return false;
			}
			Int2 int2 = (Int2) o;
			return x == int2.x &&
				y == int2.y;
		}

		@Override
		public String toString()
		{
			return "Int2{" +
				"x=" + x +
				", y=" + y +
				'}';
		}
	}

	private static Int2[] findBoundaryVertices(HashMap<Int2, HashSet<Int2>> dcel)
	{
		ArrayList<Int2> vertices = new ArrayList<>();
		for (Int2 vertex : dcel.keySet())
		{
			if (dcel.get(vertex).size() < 4)
			{
				vertices.add(vertex);
			}
		}
		return vertices.toArray(Int2[]::new);
	}

	private static Int2 findFirstBoundaryVertex(Int2 vertex, HashMap<Int2, HashSet<Int2>> dcel)
	{
		if (dcel.containsKey(vertex))
		{
			if (dcel.get(vertex).size() < 4)
			{
				return vertex;
			}
		}

		HashSet<Int2> visited = new HashSet<>();
		while (!visited.contains(vertex))
		{
			visited.add(vertex);

			if (dcel.containsKey(vertex))
			{
				for (Int2 b : dcel.get(vertex))
				{
					if (dcel.get(b).size() < 4)
					{
						return b;
					}
					vertex = b;
					break;
				}
			}
		}
		return null;
	}

	private static void putInDCEL(HashMap<Int2, HashSet<Int2>> dcel, Int2 src, Int2 dest)
	{
		if (!dcel.containsKey(src))
		{
			dcel.put(src, new HashSet<>());
		}
		dcel.get(src).add(dest);
	}

	private static HashMap<Int2, HashSet<Int2>> constructDCEL(CollisionData[] collisionMaps, int x, int y, int radius)
	{
		HashMap<Int2, HashSet<Int2>> dcel = new HashMap<>();

		for (int dx = -radius; dx <= radius; dx++)
		{
			for (int dy = -radius; dy <= radius; dy++)
			{
				final int checkX = (x >> 7) + dx;
				final int checkY = (y >> 7) + dy;

				if ((collisionMaps[0].getFlags()[checkX][checkY] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) > 0)
				{
					continue;
				}

				Int2 sw = new Int2(dx, dy);
				Int2 nw = new Int2(dx, dy + 1);
				Int2 se = new Int2(dx + 1, dy);
				Int2 ne = new Int2(dx + 1, dy + 1);

				putInDCEL(dcel, sw, nw);
				putInDCEL(dcel, nw, ne);
				putInDCEL(dcel, ne, se);
				putInDCEL(dcel, se, sw);
			}
		}
		return dcel;
	}

	// true iff straight or right turn
	private static boolean makesRightTurn(Int2 a, Int2 b, Int2 c)
	{
		int x1 = a.x - b.x;
		int y1 = a.y - b.y;
		int x2 = c.x - b.x;
		int y2 = c.y - b.y;
		int d = x1 * y2 - x2 * y1;
		return d >= 0;
	}

	private static boolean validNgon(HashMap<Int2, HashSet<Int2>> dcel, ArrayList<Int2> ngon)
	{
		if (ngon.size() == 0)
		{
			return false;
		}
		for (int i = 0; i < ngon.size() - 1; i++)
		{
			Int2 a = ngon.get(i);
			Int2 b = ngon.get(i + 1);
			if (dcel.containsKey(a))
			{
				if (!dcel.get(a).contains(b))
				{
					return false;
				}
			}
		}
		Int2 a = ngon.get(ngon.size() - 1);
		Int2 b = ngon.get(0);
		if (dcel.containsKey(a))
		{
			return dcel.get(a).contains(b);
		}
		return true;
	}

	private static ArrayList<Int2> constructNgon(HashMap<Int2, HashSet<Int2>> dcel, HashSet<Int2> visited, HashSet<Int2> boundary_vertices, Int2 vertex)
	{
		ArrayList<Int2> polygon = new ArrayList<>();
		Int2 last = null;
		Int2 start = vertex;

		// infinite loop safeguard
		do
		{
			boundary_vertices.remove(vertex);
			visited.add(vertex);
			polygon.add(vertex);

			if (dcel.containsKey(vertex))
			{
				Int2 bestNext = null;
				for (Int2 b : dcel.get(vertex))
				{
					if (dcel.containsKey(b))
					{

						boolean isHalfEdge = !dcel.get(b).contains(vertex);

						if (isHalfEdge)
						{
							// we select best based on right turn
							if (last == null || bestNext == null)
							{
								bestNext = b;
							}
							else
							{
								if (makesRightTurn(last, vertex, b))
								{
									bestNext = b;
								}
							}
						}
					}
				}

				if (bestNext == null)
				{
					// somehow our polygon doesnt have a defined boundary
					return null;
				}
				else
				{
					last = vertex;
					vertex = bestNext;
				}
			}
		}
		while (!vertex.equals(start) && last != vertex);
		return polygon;
	}

	private static Polygon ngonToScreenPoly(Client client, ArrayList<Int2> ngon, int x, int y, int plane)
	{
		int[] xs = new int[ngon.size()];
		int[] ys = new int[ngon.size()];
		boolean invalid = false;
		for (int i = 0; i < ngon.size(); i++)
		{
			Int2 _v = ngon.get(i);
			int _x = x + _v.x * LOCAL_TILE_SIZE;
			int _y = y + _v.y * LOCAL_TILE_SIZE;
			int height = getHeight(client, _x, _y, plane);
			Point p = Perspective.localToCanvas(client, _x, _y, height);
			if (p == null)
			{
				invalid = true;
				break;
			}
			xs[i] = p.getX();
			ys[i] = p.getY();
		}
		if (!invalid)
		{
			return new Polygon(xs, ys, xs.length);
		}
		return null;
	}

	private static void renderConcaveAoe(Graphics2D graphics, Client client, LocalPoint local, WorldArea worldArea, int rad)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		CollisionData[] collisionMaps = client.getCollisionMaps();

		if (collisionMaps == null)
		{
			return;
		}

		final int x = local.getX() - LOCAL_HALF_TILE_SIZE, y = local.getY() - LOCAL_HALF_TILE_SIZE;

		HashMap<Int2, HashSet<Int2>> dcel = constructDCEL(collisionMaps, local.getX(), local.getY(), rad);

		if (dcel.size() == 0)
		{
			return;
		}

		HashSet<Int2> boundary_vertices = new HashSet<>(Arrays.asList(findBoundaryVertices(dcel)));
		if (boundary_vertices.size() == 0)
		{
			return;
		}
		// could be 1 or more in the radius
		ArrayList<ArrayList<Int2>> polygons = new ArrayList<>();

		// vertices we've visited
		HashSet<Int2> visited = new HashSet<>();
		// We want to start with the center polygon
		Int2 vertex = findFirstBoundaryVertex(new Int2(0, 0), dcel);
		if (vertex == null)
		{
			vertex = boundary_vertices.stream().findFirst().get();
		}

		while (boundary_vertices.size() > 0)
		{

			ArrayList<Int2> ngon = constructNgon(dcel, visited, boundary_vertices, vertex);

			if (ngon != null && ngon.size() > 3)
			{
				if (validNgon(dcel, ngon))
				{
					polygons.add(ngon);
				}
			}

			if (boundary_vertices.size() > 0)
			{
				vertex = boundary_vertices.stream().findFirst().get();
			}
		}

		// convert tile poly to canvas polys
		ArrayList<Polygon> polys = new ArrayList<>();
		int plane = client.getPlane();
		for (ArrayList<Int2> ngon : polygons)
		{
			Polygon p = ngonToScreenPoly(client, ngon, x, y, plane);
			if (p != null)
			{
				polys.add(p);
			}
		}

		for (Polygon poly : polys)
		{
			OverlayUtil.renderPolygon(graphics, poly, Color.CYAN);
		}
	}

	private static int getHeight(@Nonnull Client client, int localX, int localY, int plane)
	{
		int sceneX = localX >> LOCAL_COORD_BITS;
		int sceneY = localY >> LOCAL_COORD_BITS;
		if (sceneX >= 0 && sceneY >= 0 && sceneX < SCENE_SIZE && sceneY < SCENE_SIZE)
		{
			int[][][] tileHeights = client.getTileHeights();

			int x = localX & (LOCAL_TILE_SIZE - 1);
			int y = localY & (LOCAL_TILE_SIZE - 1);
			int var8 = x * tileHeights[plane][sceneX + 1][sceneY] + (LOCAL_TILE_SIZE - x) * tileHeights[plane][sceneX][sceneY] >> LOCAL_COORD_BITS;
			int var9 = tileHeights[plane][sceneX][sceneY + 1] * (LOCAL_TILE_SIZE - x) + x * tileHeights[plane][sceneX + 1][sceneY + 1] >> LOCAL_COORD_BITS;
			return (LOCAL_TILE_SIZE - y) * var8 + y * var9 >> LOCAL_COORD_BITS;
		}

		return 0;
	}
}
