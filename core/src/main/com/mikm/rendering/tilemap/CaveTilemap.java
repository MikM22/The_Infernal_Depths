package com.mikm.rendering.tilemap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mikm.Vector2Int;
import com.mikm.rendering.screens.Application;
import com.mikm.rendering.screens.CaveScreen;
import com.mikm.rendering.tilemap.ruleCell.RuleCell;
import com.mikm.rendering.tilemap.ruleCell.RuleCellMetadata;
import com.mikm.rendering.tilemap.ruleCell.RuleCellMetadataReader;
import com.mikm.rendering.tilemap.ruleCell.RuleCellTiledMapTileLayer;

import java.util.Arrays;
import java.util.Random;

public class CaveTilemap {
    public static final int mapWidth = 80, mapHeight = 60;
    final static int randomFillPercent = 50;
    //must be rounded to tenths
    private final float randomRockSpawnPercent = .5f;
    private final long seed = 21;

    private final RuleCell ruleCell;
    private final TextureRegion[] wallImages;
    private final TextureRegion floorImage;
    private final TextureRegion[][] rockImages;

    private final Random random;
    private static boolean[][] ruleCellPositions;

    private boolean useWallCell1 = false;


    public CaveTilemap(CaveScreen caveScreen) {
        ruleCell = createCaveRuleCell(caveScreen.caveTileset);
        wallImages = Arrays.copyOfRange(caveScreen.caveTileset[2], 0, 3 + 1);
        floorImage = ruleCell.spritesheet[2][4];
        rockImages = caveScreen.rockImages;
        random = new Random();
    }

    private RuleCell createCaveRuleCell(TextureRegion[][] caveTileset) {
        RuleCellMetadataReader metadataReader = new RuleCellMetadataReader();
        RuleCellMetadata metadata = metadataReader.createMetadataFromFile("images/caveTiles.meta.txt");
        return new RuleCell(caveTileset, metadata);
    }

    public TiledMap createTiledMap() {
        RuleCellTiledMapTileLayer ruleCellLayer = createRuleCellTiledMapTileLayer();
        TiledMapTileLayer uncollidableLayer = createUncollidableLayer();
        TiledMapTileLayer rockLayer = createRockLayer();

        return createMapFromLayers(ruleCellLayer, uncollidableLayer, rockLayer);
    }

    private TiledMap createMapFromLayers(RuleCellTiledMapTileLayer ruleCellLayer, TiledMapTileLayer uncollidableLayer, TiledMapTileLayer rockLayer) {
        TiledMap tiledMap = new TiledMap();
        MapLayers mapLayers = tiledMap.getLayers();
        mapLayers.add(uncollidableLayer);
        mapLayers.add(ruleCellLayer);
        mapLayers.add(rockLayer);
        return tiledMap;
    }

    private RuleCellTiledMapTileLayer createRuleCellTiledMapTileLayer() {
        RuleCellPositionGenerator ruleCellPositionGenerator = new RuleCellPositionGenerator(random);
        ruleCellPositions = ruleCellPositionGenerator.createRuleCellPositions();

        return createRuleCellLayerFromRuleCellPositions();
    }

    private RuleCellTiledMapTileLayer createRuleCellLayerFromRuleCellPositions() {
        RuleCellTiledMapTileLayer ruleCellLayer = new RuleCellTiledMapTileLayer(mapWidth, mapHeight, Application.defaultTileWidth, Application.defaultTileHeight);
        for (int y = mapHeight - 1; y >= 0; y--) {
            for (int x = 0; x < mapWidth; x++) {
                if (ruleCellPositions[y][x]) {
                    ruleCellLayer.setRuleCell(x, y, ruleCell);
                }
            }
        }
        ruleCellLayer.updateRuleCells();
        return ruleCellLayer;
    }

    private TiledMapTileLayer createUncollidableLayer() {
        TiledMapTileLayer uncollidableLayer = new TiledMapTileLayer(mapWidth, mapHeight, Application.defaultTileWidth, Application.defaultTileHeight);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(floorImage);
        cell.setTile(tile);
        for (int y = mapHeight - 1; y >= 0; y--) {
            for (int x = 0; x < mapWidth; x++) {
                uncollidableLayer.setCell(x, y, cell);
                fillUncollidableLayerWithWallsAt(y, x, uncollidableLayer);
            }
        }
        return uncollidableLayer;
    }

    private void fillUncollidableLayerWithWallsAt(int y, int x, TiledMapTileLayer uncollidableLayer) {
        TiledMapTileLayer.Cell[] wallCell = new TiledMapTileLayer.Cell[4];
        for (int i = 0; i < 4; i++) {
            wallCell[i] = new TiledMapTileLayer.Cell();
            wallCell[i].setTile(new StaticTiledMapTile(wallImages[i]));
        }
        boolean isOutOfBounds = (y+1 > mapHeight - 1);
        if (isOutOfBounds) {
            return;
        }
        if (ruleCellPositions[y+1][x]) {
            uncollidableLayer.setCell(x, y, getCorrectWallCell(y, x, wallCell));
        }
    }

    private TiledMapTileLayer.Cell getCorrectWallCell(int y, int x, TiledMapTileLayer.Cell[] wallCell) {
        if (x - 1 < 0 || !ruleCellPositions[y+1][x-1]) {
            if (x+1 > mapWidth - 1 || !ruleCellPositions[y+1][x+1]) {
                return wallCell[2];
            }
            return wallCell[0];
        }
        if (x+1 > mapWidth - 1 || !ruleCellPositions[y+1][x+1]) {
            return wallCell[3];
        }
        useWallCell1 = !useWallCell1;
        if (useWallCell1) {
            return wallCell[1];
        } else {
            return wallCell[2];
        }
    }

    private TiledMapTileLayer createRockLayer() {
        TiledMapTileLayer rockLayer = new TiledMapTileLayer(mapWidth, mapHeight, Application.defaultTileWidth, Application.defaultTileHeight);
        TiledMapTileLayer.Cell[] rockCells = new TiledMapTileLayer.Cell[3];

        for (int i = 0; i < 3; i++) {
            rockCells[i] = new TiledMapTileLayer.Cell();
            rockCells[i].setTile(new StaticTiledMapTile(rockImages[0][i]));
        }

        for (int y = mapHeight - 1; y >= 0; y--) {
            for (int x = 0; x < mapWidth; x++) {
                boolean inOpenTile = !ruleCellPositions[y][x] && y + 1 <= mapHeight - 1 && !ruleCellPositions[y+1][x];
                if (inOpenTile) {
                    if (random.nextInt(1000) < 10 * randomRockSpawnPercent) {
                        TiledMapTileLayer.Cell randomRockCell = rockCells[random.nextInt(3)];
                        rockLayer.setCell(x, y, randomRockCell);
                    }
                }
            }
        }
        return rockLayer;
    }

    public static boolean isRuleCellAtPosition(int x, int y) {
        Vector2Int tilePos = new Vector2Int(x/Application.defaultTileWidth, y/Application.defaultTileHeight);
        boolean outOfBounds = (tilePos.x < 0 || tilePos.x > mapWidth - 1 || tilePos.y < 0 || tilePos.y > mapHeight - 1);
        if (outOfBounds) {
            return true;
        }
        return ruleCellPositions[tilePos.y][tilePos.x];
    }
}