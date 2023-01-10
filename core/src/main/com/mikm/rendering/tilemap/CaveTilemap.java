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

import java.util.ArrayList;

public class CaveTilemap {
    public static final int MAP_WIDTH = 200, MAP_HEIGHT = 200;
    final static int FILL_CELL_PERCENT_CHANCE = 52;

    private final RuleCell ruleCell;
    private TextureRegion[] wallImages;
    private TextureRegion floorImage;

    private final boolean[][] ruleCellPositions;
    private boolean useWallCell1 = false;

    private final CaveTilemapEntitySpawner spawner;

    public CaveTilemap(CaveScreen caveScreen) {
        ruleCell = createCaveRuleCell(caveScreen.caveTileset);
        createImages(caveScreen);

        RuleCellPositionGenerator ruleCellPositionGenerator = new RuleCellPositionGenerator();
        ruleCellPositions = ruleCellPositionGenerator.createRuleCellPositions();
        spawner = new CaveTilemapEntitySpawner(caveScreen, ruleCellPositions);
    }

    private void createImages(CaveScreen caveScreen) {
        wallImages = new TextureRegion[5];
        System.arraycopy(caveScreen.caveTileset[2], 0, wallImages, 0, 4);
        wallImages[4] = caveScreen.caveTileset[1][2];
        floorImage = ruleCell.spritesheet[2][4];
    }

    private RuleCell createCaveRuleCell(TextureRegion[][] caveTileset) {
        RuleCellMetadataReader metadataReader = new RuleCellMetadataReader();
        RuleCellMetadata metadata = metadataReader.createMetadataFromFile("images/caveTiles.meta.txt");
        return new RuleCell(caveTileset, metadata);
    }

    //Called by CaveScreen
    public TiledMap createTiledMap() {
        RuleCellTiledMapTileLayer ruleCellLayer = createRuleCellLayerFromRuleCellPositions();
        TiledMapTileLayer uncollidableLayer = createUncollidableLayer();
        spawner.createRockLayer();

        return createMapFromLayers(ruleCellLayer, uncollidableLayer);
    }

    private TiledMap createMapFromLayers(RuleCellTiledMapTileLayer ruleCellLayer, TiledMapTileLayer uncollidableLayer) {
        TiledMap tiledMap = new TiledMap();
        MapLayers mapLayers = tiledMap.getLayers();
        mapLayers.add(uncollidableLayer);
        mapLayers.add(ruleCellLayer);
        return tiledMap;
    }

    private RuleCellTiledMapTileLayer createRuleCellLayerFromRuleCellPositions() {
        RuleCellTiledMapTileLayer ruleCellLayer = new RuleCellTiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, Application.TILE_WIDTH, Application.TILE_HEIGHT);
        for (int y = MAP_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (ruleCellPositions[y][x]) {
                    ruleCellLayer.setRuleCell(x, y, ruleCell);
                }
            }
        }
        ruleCellLayer.updateRuleCells();
        return ruleCellLayer;
    }

    private TiledMapTileLayer createUncollidableLayer() {
        TiledMapTileLayer uncollidableLayer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, Application.TILE_WIDTH, Application.TILE_HEIGHT);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(floorImage);
        cell.setTile(tile);
        for (int y = MAP_HEIGHT - 1; y >= 0; y--) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                uncollidableLayer.setCell(x, y, cell);
                fillUncollidableLayerWithWallsAt(y, x, uncollidableLayer);
            }
        }
        return uncollidableLayer;
    }

    private void fillUncollidableLayerWithWallsAt(int y, int x, TiledMapTileLayer uncollidableLayer) {
        TiledMapTileLayer.Cell[] wallCell = new TiledMapTileLayer.Cell[5];
        for (int i = 0; i < 5; i++) {
            wallCell[i] = new TiledMapTileLayer.Cell();
            wallCell[i].setTile(new StaticTiledMapTile(wallImages[i]));
        }
        boolean isOutOfBounds = (y+1 > MAP_HEIGHT - 1);
        if (isOutOfBounds) {
            return;
        }
        if (ruleCellPositions[y+1][x]) {
            uncollidableLayer.setCell(x, y, getCorrectWallCell(y, x, wallCell));
        }
    }

    private TiledMapTileLayer.Cell getCorrectWallCell(int y, int x, TiledMapTileLayer.Cell[] wallCell) {
        if (x - 1 < 0 || !ruleCellPositions[y+1][x-1]) {
            if (x+1 > MAP_WIDTH - 1 || !ruleCellPositions[y+1][x+1]) {
                return wallCell[4];
            }
            return wallCell[0];
        }
        if (x+1 > MAP_WIDTH - 1 || !ruleCellPositions[y+1][x+1]) {
            return wallCell[3];
        }
        useWallCell1 = !useWallCell1;
        if (useWallCell1) {
            return wallCell[1];
        } else {
            return wallCell[2];
        }
    }

    public ArrayList<Vector2Int> getOpenTilePositions() {
        return spawner.openTilePositions;
    }

    public boolean[][] getCollidableTilePositions() {
        return spawner.getCollidableTilePositions();
    }

    public void spawnEnemies() {
        spawner.spawnEnemies();
    }
}