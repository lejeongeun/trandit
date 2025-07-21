class TransportationRequest {
  final DateTime? dateTime;
  final String departure;
  final String destination;
  final String truckType;
  final bool isForkliftNeeded;
  final int workerCount;

  TransportationRequest({
    required this.dateTime,
    required this.departure,
    required this.destination,
    required this.truckType,
    required this.isForkliftNeeded,
    required this.workerCount,
  });
}
