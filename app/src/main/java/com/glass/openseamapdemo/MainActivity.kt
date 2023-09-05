package com.glass.openseamapdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        Configuration.getInstance().userAgentValue = "com.glass.openseamapdemo"

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    MapViewWrapper(
        modifier = Modifier.fillMaxSize()
    ) { mapView ->
        // Set up the map view (center, zoom level, etc.)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.controller.setZoom(4.0)
        mapView.controller.setCenter(GeoPoint(51.509865, -0.118092)) // For example, center on London

        // Add OpenSeaMap overlay here
        val tileProvider = MapTileProviderBasic(mapView.context).apply {
            tileSource = OPEN_SEAMAP
        }
        val tileOverlay = TilesOverlay(tileProvider, mapView.context)
        mapView.overlays.add(tileOverlay)
    }
}

@Composable
fun MapViewWrapper(
    modifier: Modifier = Modifier,
    onMapViewCreated: (MapView) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK) // OpenStreetMap Base Layer
        }
    }

    AndroidView({ mapView }, modifier) {
        onMapViewCreated(it)
    }
}

val OPEN_SEAMAP = object : OnlineTileSourceBase(
    "OpenSeaMap",
    0,
    18,
    256,
    ".png",
    arrayOf("https://tiles.openseamap.org/seamark/")
) {
    override fun getTileURLString(pTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pTileIndex) + "/" +
                MapTileIndex.getX(pTileIndex) + "/" +
                MapTileIndex.getY(pTileIndex) + mImageFilenameEnding
    }
}