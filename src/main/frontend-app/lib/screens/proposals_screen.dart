import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../transportation_request.dart';
import '../proposal.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class ProposalsScreen extends StatefulWidget {
  const ProposalsScreen({super.key});

  @override
  State<ProposalsScreen> createState() => _ProposalsScreenState();
}

class _ProposalsScreenState extends State<ProposalsScreen> {
  List<dynamic> _requests = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _fetchRequests();
  }

  Future<void> _fetchRequests() async {
    setState(() { _isLoading = true; _error = null; });
    try {
      final prefs = await SharedPreferences.getInstance();
      final accessToken = prefs.getString('accessToken');
      if (accessToken == null) {
        setState(() { _error = '로그인이 필요합니다.'; _isLoading = false; });
        return;
      }
      String getApiBaseUrl() {
        if (identical(0, 0.0)) return "http://localhost:8080";
        try {
          if (Theme.of(context).platform == TargetPlatform.android) {
            return "http://10.0.2.2:8080";
          }
        } catch (_) {}
        return "http://localhost:8080";
      }
      final url = Uri.parse('${getApiBaseUrl()}/api/request');
      final response = await http.get(url, headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $accessToken',
      });
      if (response.statusCode == 200) {
        setState(() {
          _requests = json.decode(response.body);
          _isLoading = false;
        });
      } else {
        setState(() {
          _error = '요청 목록을 불러오지 못했습니다.';
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _error = '오류 발생: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('내 운송 요청 목록')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text(_error!))
              : _requests.isEmpty
                  ? const Center(child: Text('등록된 운송 요청이 없습니다.'))
                  : ListView.builder(
                      itemCount: _requests.length,
                      itemBuilder: (context, index) {
                        final req = _requests[index];
                        return Card(
                          margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          child: ListTile(
                            leading: Icon(Icons.local_shipping, color: Theme.of(context).primaryColor),
                            title: Text('${req['departureAddress']} → ${req['arrivalAddress']}'),
                            subtitle: Text('상태: ${req['status']}\n출발: ${req['departureTime'] ?? ''}'),
                            isThreeLine: true,
                            onTap: () => _showRequestDetail(context, req),
                          ),
                        );
                      },
                    ),
    );
  }

  void _showRequestDetail(BuildContext context, dynamic req) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('운송 요청 상세'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('출발지: ${req['departureAddress']}'),
            Text('도착지: ${req['arrivalAddress']}'),
            Text('출발일시: ${req['departureTime'] ?? ''}'),
            Text('차량: ${req['vehicleType']}'),
            Text('지게차 필요: ${req['needForkLift'] ? '예' : '아니오'}'),
            Text('인부: ${req['workerCount']}명'),
            Text('상태: ${req['status']}'),
            const SizedBox(height: 12),
            const Divider(),
            const Text('※ 기사님 제안/매칭/진행/완료 등은 추후 지원 예정입니다.', style: TextStyle(fontSize: 13, color: Colors.blueGrey)),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('닫기'),
          ),
        ],
      ),
    );
  }
}