import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geocoding/geocoding.dart';
import 'package:flutter_google_places/flutter_google_places.dart';
import 'package:google_maps_webservice/places.dart';
import 'package:google_api_headers/google_api_headers.dart';

class MapScreen extends StatefulWidget {
  const MapScreen({super.key});

  @override
  State<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends State<MapScreen> {
  final Completer<GoogleMapController> _controller = Completer();
  GoogleMapController? _mapController;
  final _places = GoogleMapsPlaces(apiKey: "AIzaSyB_LksOsISmhw0Xg-DjJYCRReG9ia_g7T0");

  static const CameraPosition _kGooglePlex = CameraPosition(
    target: LatLng(37.5665, 126.9780), // Default to Seoul
    zoom: 14.4746,
  );

  LatLng? _selectedLatLng;
  String? _selectedAddress;

  Future<void> _handleSearch() async {
    Prediction? p = await PlacesAutocomplete.show(
      context: context,
      apiKey: "AIzaSyB_LksOsISmhw0Xg-DjJYCRReG9ia_g7T0",
      mode: Mode.overlay, // or Mode.fullscreen
      language: "ko",
      components: [Component(Component.country, "kr")],
    );

    if (p != null) {
      PlacesDetailsResponse detail = await _places.getDetailsByPlaceId(p.placeId!);
      final lat = detail.result.geometry!.location.lat;
      final lng = detail.result.geometry!.location.lng;

      _mapController?.animateCamera(CameraUpdate.newCameraPosition(
        CameraPosition(target: LatLng(lat, lng), zoom: 16),
      ));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('위치 선택'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: _handleSearch,
          )
        ],
      ),
      body: Stack(
        children: [
          GoogleMap(
            mapType: MapType.normal,
            initialCameraPosition: _kGooglePlex,
            onMapCreated: (GoogleMapController controller) {
              _controller.complete(controller);
              _mapController = controller;
            },
            onCameraMove: (CameraPosition position) {
              _selectedLatLng = position.target;
            },
            onCameraIdle: () async {
              if (_selectedLatLng != null) {
                try {
                  List<Placemark> placemarks = await placemarkFromCoordinates(
                    _selectedLatLng!.latitude,
                    _selectedLatLng!.longitude,
                  );
                  if (placemarks.isNotEmpty) {
                    final placemark = placemarks.first;
                    setState(() {
                      _selectedAddress = 
                          '${placemark.street}'.isNotEmpty ? '${placemark.street}' : '${placemark.name}';
                    });
                  }
                } catch (e) {
                  // Handle error
                }
              }
            },
          ),
          const Center(
            child: Icon(
              Icons.location_pin,
              color: Colors.red,
              size: 50,
            ),
          ),
          Positioned(
            bottom: 20,
            left: 20,
            right: 20,
            child: Column(
              children: [
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(12.0),
                    child: Text(
                      _selectedAddress ?? '지도를 움직여 위치를 선택하세요',
                      style: const TextStyle(fontSize: 16),
                    ),
                  ),
                ),
                const SizedBox(height: 10),
                ElevatedButton(
                  onPressed: () {
                    if (_selectedLatLng != null && _selectedAddress != null) {
                      Navigator.of(context).pop({
                        'latlng': _selectedLatLng,
                        'address': _selectedAddress,
                      });
                    }
                  },
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 15),
                    minimumSize: const Size(double.infinity, 50),
                  ),
                  child: const Text('이 위치로 설정', style: TextStyle(fontSize: 18)),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
