package com.example.tarea_03
// Este archivo maneja todo lo relacionado con el mapa, ubicación del usuario
// y cálculo de distancia entre el usuario y puntos específicos.

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
package com.example.tarea_03

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val BASILICA_PLAYLIST_URI = "spotify:playlist:37i9dQZF1DXcBWIGoYBMm1"
private const val TEC_PLAYLIST_URI = "spotify:playlist:37i9dQZF1DX0XUsuxWHRQd"

@Composable
fun MapScreen(context: Context) {
    val cameraPositionState = rememberCameraPositionState()
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val basilicaLocation = LatLng(9.8644, -83.9197)
    val tecLocation = LatLng(9.8556, -83.9120)
    val triggeredPlaces = remember { mutableStateSetOf<String>() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                locationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(locationPermission)
        }
    }

    LaunchedEffect(Unit) {
        getUserLocation(context) { userLocation ->
            val distanciaBasilica = calcularDistancia(
                userLocation.latitude,
                userLocation.longitude,
                basilicaLocation.latitude,
                basilicaLocation.longitude
            )

            val distanciaTEC = calcularDistancia(
                userLocation.latitude,
                userLocation.longitude,
                tecLocation.latitude,
                tecLocation.longitude
            )

            if (distanciaBasilica < 500 && !triggeredPlaces.contains("basilica")) {
                triggeredPlaces.add("basilica")
                Toast.makeText(
                    context,
                    "Estás a ${distanciaBasilica.toInt()} metros de la Basílica",
                    Toast.LENGTH_LONG
                ).show()
                SpotifyManager.openPlaylist(context, BASILICA_PLAYLIST_URI)
            }

            if (distanciaTEC < 500 && !triggeredPlaces.contains("tec")) {
                triggeredPlaces.add("tec")
                Toast.makeText(
                    context,
                    "Estás a ${distanciaTEC.toInt()} metros del TEC",
                    Toast.LENGTH_LONG
                ).show()
                SpotifyManager.openPlaylist(context, TEC_PLAYLIST_URI)
            }

            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
            )
        }
    }

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        locationPermission
    ) == PackageManager.PERMISSION_GRANTED

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        )
    ) {
        Marker(
            state = MarkerState(position = basilicaLocation),
            title = "Basílica de los Ángeles",
            snippet = "Cartago",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
            onClick = {
                Toast.makeText(
                    context,
                    "Hiciste clic en la Basílica",
                    Toast.LENGTH_SHORT
                ).show()
                SpotifyManager.openPlaylist(context, BASILICA_PLAYLIST_URI)
                true
            }
        )

        Marker(
            state = MarkerState(position = tecLocation),
            title = "TEC Cartago",
            snippet = "Instituto Tecnológico de Costa Rica",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
            onClick = {
                Toast.makeText(
                    context,
                    "Hiciste clic en el TEC",
                    Toast.LENGTH_SHORT
                ).show()
                SpotifyManager.openPlaylist(context, TEC_PLAYLIST_URI)
                true
            }
        )
    }
}

fun getUserLocation(context: Context, onLocation: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            onLocation(userLatLng)
        }
    }
}

fun calcularDistancia(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val radioTierra = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) *
        Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return radioTierra * c
}
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun MapScreen(context: Context) {

    // Controla la posición de la cámara del mapa (dónde está centrado el mapa)
    val cameraPositionState = rememberCameraPositionState()

    // Permiso que necesitamos para acceder a la ubicación del usuario
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    // Coordenadas de puntos importantes que queremos mostrar en el mapa
    val basilicaLocation = LatLng(9.8644, -83.9197)   // Basílica de Cartago
    val tecLocation = LatLng(9.8556, -83.9120)        // TEC Cartago

    // Launcher que permite solicitar permisos en tiempo de ejecución
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    /*
    Este bloque se ejecuta cuando se crea la pantalla.
    Verifica si el permiso de ubicación ya fue otorgado.
    Si no lo fue, solicita el permiso al usuario.
     */
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                locationPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(locationPermission)
        }
    }

    /*
    Este bloque obtiene la ubicación actual del usuario.
    Luego:
    1. Calcula la distancia entre el usuario y el punto customLocation.
    2. Si está a menos de 100 metros, dispara un evento (Toast).
    3. Centra el mapa en la ubicación del usuario.
     */
    LaunchedEffect(Unit) {
        getUserLocation(context) { userLocation ->

            val distanciaBasilica = calcularDistancia(
                userLocation.latitude,
                userLocation.longitude,
                basilicaLocation.latitude,
                basilicaLocation.longitude
            )

            val distanciaTEC = calcularDistancia(
                userLocation.latitude,
                userLocation.longitude,
                tecLocation.latitude,
                tecLocation.longitude
            )

            // Geofencing simplificado
            if (distanciaBasilica < 500) {
                Toast.makeText(
                    context,
                    "Estás a ${distanciaBasilica.toInt()} metros de la Basílica",
                    Toast.LENGTH_LONG
                ).show()
                // AQUÍ SE DEBERÍA LLAMAR A LA FUNCIÓN QUE ABRE O REPRODUCE
                // LA PLAYLIST DE SPOTIFY ASOCIADA A ESTE PUNTO DEL MAPA.
            }

            if (distanciaTEC < 500) {
                Toast.makeText(
                    context,
                    "Estás a ${distanciaTEC.toInt()} metros del TEC",
                    Toast.LENGTH_LONG
                ).show()
                // AQUÍ SE DEBERÍA LLAMAR A LA FUNCIÓN QUE ABRE O REPRODUCE
                // LA PLAYLIST DE SPOTIFY ASOCIADA A ESTE PUNTO DEL MAPA.
            }

            // Mueve la cámara del mapa hacia la ubicación del usuario
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
            )
        }
    }

    /*
    GoogleMap es el componente que dibuja el mapa en pantalla.
    fillMaxSize() hace que el mapa ocupe toda la pantalla.
    isMyLocationEnabled muestra el punto azul de la ubicación del usuario.
     */
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        )
    ) {

        // Marcador en la Basílica de los Ángeles
        Marker(
            state = MarkerState(position = basilicaLocation),
            title = "Basílica de los Ángeles",
            snippet = "Cartago",
            onClick = {

                Toast.makeText(
                    context,
                    "Hiciste clic en la Basílica",
                    Toast.LENGTH_SHORT
                ).show()
                // AQUÍ SE PODRÍA LLAMAR A LA FUNCIÓN QUE REPRODUCE
                // LA PLAYLIST DE SPOTIFY RELACIONADA CON ESTE LUGAR.
                true
            }
        )

        // Marcador en el TEC de Cartago
        Marker(
            state = MarkerState(position = tecLocation),
            title = "TEC Cartago",
            snippet = "Instituto Tecnológico de Costa Rica",
            onClick = {

                Toast.makeText(
                    context,
                    "Hiciste clic en el TEC",
                    Toast.LENGTH_SHORT
                ).show()
                // AQUÍ SE PODRÍA LLAMAR A LA FUNCIÓN QUE REPRODUCE
                // LA PLAYLIST DE SPOTIFY RELACIONADA CON ESTE LUGAR.
                true
            }
        )
    }
}

/*
Esta función obtiene la última ubicación conocida del usuario
utilizando el sistema de localización de Android (FusedLocationProviderClient).

Parámetros:
context -> contexto de la aplicación
onLocation -> función callback que devuelve la ubicación obtenida
 */
fun getUserLocation(context: Context, onLocation: (LatLng) -> Unit) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Verifica nuevamente que el permiso fue concedido
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    // Obtiene la última ubicación conocida
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            onLocation(userLatLng)
        }
    }
}

/*
Función que calcula la distancia entre dos puntos geográficos
utilizando la fórmula de Haversine.

Esta fórmula se usa para calcular distancias en la superficie de la Tierra
teniendo en cuenta su curvatura.

Parámetros:
lat1, lon1 -> coordenadas del punto 1
lat2, lon2 -> coordenadas del punto 2

Retorna:
Distancia en metros
 */
fun calcularDistancia(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {

    val radioTierra = 6371000.0 // radio promedio de la Tierra en metros

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) *
            Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) *
            Math.sin(dLon / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    // distancia final sobre la superficie de la Tierra
    return radioTierra * c
}
