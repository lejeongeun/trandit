import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../transportation_request.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';

class RequestScreen extends StatefulWidget {
  const RequestScreen({super.key});

  @override
  State<RequestScreen> createState() => _RequestScreenState();
}

class _RequestScreenState extends State<RequestScreen> {
  final _formKey = GlobalKey<FormState>();
  DateTime? _selectedDate;
  TimeOfDay? _selectedTime;
  String _departure = '';
  String _destination = '';
  String? _selectedTruckType = '1t';
  bool _isForkliftNeeded = false;
  int _workerCount = 0;
  bool _isLoading = false;
  LatLng? _departureLatLng;
  LatLng? _arrivalLatLng;
  String? _departureAddressText;
  String? _arrivalAddressText;
  final TextEditingController _departureController = TextEditingController();
  final TextEditingController _arrivalController = TextEditingController();

  @override
  void dispose() {
    _departureController.dispose();
    _arrivalController.dispose();
    super.dispose();
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now(),
      firstDate: DateTime.now(),
      lastDate: DateTime(2101),
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  Future<void> _selectTime(BuildContext context) async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime ?? TimeOfDay.now(),
    );
    if (picked != null && picked != _selectedTime) {
      setState(() {
        _selectedTime = picked;
      });
    }
  }

  Future<LatLng?> _pickLocationOnMap(BuildContext context, LatLng initialPosition) async {
    LatLng? selectedPosition = initialPosition;
    TextEditingController addressController = TextEditingController();
    CameraPosition cameraPosition = CameraPosition(target: initialPosition, zoom: 15);
    GoogleMapController? mapController;
    Future<void> moveToCurrentLocation() async {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('위치 서비스가 비활성화되어 있습니다.')),
        );
        return;
      }
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('위치 권한이 필요합니다.')),
          );
          return;
        }
      }
      if (permission == LocationPermission.deniedForever) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('위치 권한이 영구적으로 거부되었습니다. 설정에서 허용해 주세요.')),
        );
        return;
      }
      Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
      final myLatLng = LatLng(position.latitude, position.longitude);
      mapController?.animateCamera(CameraUpdate.newLatLng(myLatLng));
      selectedPosition = myLatLng;
    }
    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        content: SizedBox(
          width: 300,
          height: 500,
          child: Column(
            children: [
              Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: addressController,
                      decoration: const InputDecoration(
                        hintText: '주소 검색',
                        contentPadding: EdgeInsets.symmetric(horizontal: 8),
                      ),
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.search),
                    onPressed: () async {
                      String address = addressController.text.trim();
                      if (address.isNotEmpty) {
                        try {
                          List<Location> locations = await locationFromAddress(address);
                          if (locations.isNotEmpty) {
                            final loc = locations[0];
                            final newLatLng = LatLng(loc.latitude, loc.longitude);
                            mapController?.animateCamera(
                              CameraUpdate.newLatLng(newLatLng),
                            );
                            selectedPosition = newLatLng;
                          }
                        } catch (e) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('주소를 찾을 수 없습니다.')),
                          );
                        }
                      }
                    },
                  ),
                  IconButton(
                    icon: const Icon(Icons.my_location),
                    tooltip: '내 위치로 이동',
                    onPressed: moveToCurrentLocation,
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Expanded(
                child: GoogleMap(
                  initialCameraPosition: cameraPosition,
                  onMapCreated: (controller) {
                    mapController = controller;
                  },
                  onTap: (LatLng pos) {
                    selectedPosition = pos;
                  },
                  markers: selectedPosition != null
                      ? {Marker(markerId: MarkerId('selected'), position: selectedPosition!)}
                      : {},
                ),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(selectedPosition),
            child: const Text('선택'),
          ),
        ],
      ),
    );
    return selectedPosition;
  }

  Future<void> _updateAddressText({required bool isDeparture, required LatLng latLng}) async {
    try {
      List<Placemark> placemarks = await placemarkFromCoordinates(latLng.latitude, latLng.longitude, localeIdentifier: 'ko');
      if (placemarks.isNotEmpty) {
        final p = placemarks.first;
        final address = [
          if (p.street != null && p.street!.isNotEmpty) p.street,
          if (p.locality != null && p.locality!.isNotEmpty) p.locality,
          if (p.administrativeArea != null && p.administrativeArea!.isNotEmpty) p.administrativeArea,
          if (p.country != null && p.country!.isNotEmpty) p.country,
        ].whereType<String>().join(' ');
        setState(() {
          if (isDeparture) {
            _departureAddressText = address;
            _departureController.text = address;
          } else {
            _arrivalAddressText = address;
            _arrivalController.text = address;
          }
        });
      }
    } catch (e) {
      // 주소 변환 실패 시 무시
    }
  }

  Future<void> _submitRequest() async {
    if (!_formKey.currentState!.validate()) return;
    _formKey.currentState!.save();
    if (_selectedDate == null || _selectedTime == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('날짜와 시간을 모두 선택하세요.')),
      );
      return;
    }
    if (_departureLatLng == null || _arrivalLatLng == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('출발지와 도착지 위치를 모두 지도에서 선택하세요.')),
      );
      return;
    }
    setState(() { _isLoading = true; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final accessToken = prefs.getString('accessToken');
      if (accessToken == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('로그인이 필요합니다.')),
        );
        return;
      }
      // 날짜+시간 합치기
      final DateTime departureDateTime = DateTime(
        _selectedDate!.year, _selectedDate!.month, _selectedDate!.day,
        _selectedTime!.hour, _selectedTime!.minute,
      );
      // 차량 타입 매핑
      String vehicleType;
      switch (_selectedTruckType) {
        case '1t': vehicleType = 'ONE_TON_TRUCK'; break;
        case '2.5t': vehicleType = 'THREE_TON_TRUCK'; break;
        case '3.5t': vehicleType = 'FIVE_TON_TRUCK'; break;
        case '5t': vehicleType = 'FIVE_TON_TRUCK'; break;
        default: vehicleType = 'ONE_TON_TRUCK';
      }
      // API 주소 환경별 분기
      String getApiBaseUrl() {
        if (identical(0, 0.0)) return "http://localhost:8080"; // fallback for web
        try {
          if (Theme.of(context).platform == TargetPlatform.android) {
            return "http://10.0.2.2:8080";
          }
        } catch (_) {}
        return "http://localhost:8080";
      }
      final url = Uri.parse('${getApiBaseUrl()}/api/request');
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $accessToken',
        },
        body: json.encode({
          'departureAddress': _departure,
          'arrivalAddress': _destination,
          'departureLat': _departureLatLng?.latitude,
          'departureLng': _departureLatLng?.longitude,
          'arrivalLat': _arrivalLatLng?.latitude,
          'arrivalLng': _arrivalLatLng?.longitude,
          'departureTime': departureDateTime.toIso8601String(),
          'vehicleType': vehicleType,
          'needForkLift': _isForkliftNeeded,
          'workerCount': _workerCount,
        }),
      );
      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('운송 요청이 등록되었습니다.')),
        );
        Navigator.of(context).pop();
      } else {
        final errorData = json.decode(response.body);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(errorData['message'] ?? '등록 실패: 알 수 없는 오류')),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('오류 발생: $e')),
      );
    } finally {
      setState(() { _isLoading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('운송 요청 등록'),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16.0),
          children: <Widget>[
            _buildSectionTitle(context, '출발 및 도착 정보'),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    TextFormField(
                      controller: _departureController,
                      decoration: const InputDecoration(labelText: '출발지', prefixIcon: Icon(Icons.my_location)),
                      validator: (value) => value!.isEmpty ? '출발지를 입력하세요' : null,
                      onSaved: (value) => _departure = value!,
                    ),
                    const SizedBox(height: 8),
                    ElevatedButton(
                      onPressed: () async {
                        final pos = await _pickLocationOnMap(context, _departureLatLng ?? LatLng(37.5665, 126.9780));
                        if (pos != null) {
                          setState(() => _departureLatLng = pos);
                          _updateAddressText(isDeparture: true, latLng: pos);
                        }
                      },
                      child: Text(_departureLatLng == null ? '출발지 지도에서 선택' : '출발지 선택됨'),
                    ),
                    if (_departureLatLng != null && _departureAddressText != null)
                      Padding(
                        padding: const EdgeInsets.only(top: 4.0, left: 8.0, right: 8.0),
                        child: Text(
                          '선택된 출발지 주소: $_departureAddressText',
                          style: const TextStyle(fontSize: 13, color: Colors.blueGrey),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _arrivalController,
                      decoration: const InputDecoration(labelText: '도착지', prefixIcon: Icon(Icons.flag)),
                      validator: (value) => value!.isEmpty ? '도착지를 입력하세요' : null,
                      onSaved: (value) => _destination = value!,
                    ),
                    const SizedBox(height: 8),
                    ElevatedButton(
                      onPressed: () async {
                        final pos = await _pickLocationOnMap(context, _arrivalLatLng ?? LatLng(37.5665, 126.9780));
                        if (pos != null) {
                          setState(() => _arrivalLatLng = pos);
                          _updateAddressText(isDeparture: false, latLng: pos);
                        }
                      },
                      child: Text(_arrivalLatLng == null ? '도착지 지도에서 선택' : '도착지 선택됨'),
                    ),
                    if (_arrivalLatLng != null && _arrivalAddressText != null)
                      Padding(
                        padding: const EdgeInsets.only(top: 4.0, left: 8.0, right: 8.0),
                        child: Text(
                          '선택된 도착지 주소: $_arrivalAddressText',
                          style: const TextStyle(fontSize: 13, color: Colors.blueGrey),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                  ],
                ),
              ),
            ),
            _buildSectionTitle(context, '날짜 및 시간'),
            Card(
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
                child: Column(
                  children: [
                    ListTile(
                      leading: const Icon(Icons.calendar_today),
                      title: Text(_selectedDate == null ? '날짜를 선택하세요' : DateFormat('yyyy년 MM월 dd일').format(_selectedDate!)),
                      trailing: const Icon(Icons.arrow_drop_down),
                      onTap: () => _selectDate(context),
                    ),
                    const Divider(height: 1),
                    ListTile(
                      leading: const Icon(Icons.access_time),
                      title: Text(_selectedTime == null ? '시간을 선택하세요' : _selectedTime!.format(context)),
                      trailing: const Icon(Icons.arrow_drop_down),
                      onTap: () => _selectTime(context),
                    ),
                  ],
                ),
              ),
            ),
            _buildSectionTitle(context, '화물 상세 정보'),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    DropdownButtonFormField<String>(
                      value: _selectedTruckType,
                      decoration: const InputDecoration(labelText: '차량 종류', prefixIcon: Icon(Icons.local_shipping)),
                      items: ['1t', '2.5t', '3.5t', '5t'].map((String value) {
                        return DropdownMenuItem<String>(value: value, child: Text(value));
                      }).toList(),
                      onChanged: (newValue) => setState(() => _selectedTruckType = newValue),
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      decoration: const InputDecoration(labelText: '필요 인부 (명)', prefixIcon: Icon(Icons.people)),
                      keyboardType: TextInputType.number,
                      validator: (value) => value!.isEmpty ? '필요 인부 수를 입력하세요' : null,
                      onSaved: (value) => _workerCount = int.parse(value!),
                    ),
                    SwitchListTile(
                      title: const Text('지게차 필요'),
                      value: _isForkliftNeeded,
                      onChanged: (bool value) => setState(() => _isForkliftNeeded = value),
                      secondary: const Icon(Icons.construction),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              icon: const Icon(Icons.search),
              label: _isLoading ? const Text('등록 중...') : const Text('맞춤 기사님 찾기'),
              onPressed: _isLoading ? null : _submitRequest,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionTitle(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.only(top: 24.0, bottom: 8.0, left: 8.0),
      child: Text(title, style: Theme.of(context).textTheme.titleLarge?.copyWith(color: Theme.of(context).primaryColor)),
    );
  }
}
