package com.mikm.rendering;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mikm.entities.player.Player;
import com.mikm.rendering.tilemap.CaveLevelGenerator;
import com.mikm.rendering.tilemap.ruleCell.RuleCell;
import com.mikm.rendering.tilemap.ruleCell.RuleCellMetadata;
import com.mikm.rendering.tilemap.ruleCell.RuleCellMetadataReader;

public class CaveScreen extends GameScreen {

    private final TextureRegion[][] caveTileset;
    private final Color caveWallColor = new Color(41/255f, 16/255f, 16/255f, 1);

    CaveScreen(Application application, AssetManager assetManager) {
        super(application, assetManager);

        Texture caveTilesetSpritesheet = assetManager.get("images/caveTiles.png", Texture.class);
        caveTileset = TextureRegion.split(caveTilesetSpritesheet, Application.defaultTileWidth, Application.defaultTileHeight);
        TextureRegion temporaryImage = caveTileset[2][1];

        createTiledMapRenderer();

        player = new Player(1000, 1000, temporaryImage);
        stage.addActor(player.group);
    }

    @Override
    public void render(float delta) {
        application.batch.begin();
        ScreenUtils.clear(caveWallColor);
        camera.position.set(new Vector3(player.x, player.y, 0));
        camera.update();
        tiledMapRenderer.setView(camera);
        super.drawAssets();
        application.batch.end();
    }



    @Override
    public void dispose() {
        super.dispose();

    }

    private void createTiledMapRenderer() {
        RuleCell ruleCell = createCaveRuleCell();
        CaveLevelGenerator caveLevelGenerator = new CaveLevelGenerator(ruleCell);
        tiledMap = caveLevelGenerator.createTiledMap();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1);
        tiledMapRenderer.setView(camera);
    }

    private RuleCell createCaveRuleCell() {
        RuleCellMetadataReader metadataReader = new RuleCellMetadataReader();
        RuleCellMetadata metadata = metadataReader.createMetadataFromFile("images/caveTiles.meta.txt");
        return new RuleCell(caveTileset, metadata);
    }
}
