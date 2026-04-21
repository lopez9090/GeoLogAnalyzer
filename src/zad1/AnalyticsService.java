/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public record AnalyticsService(
        LogParser logParser,
        TimestampRepairService timestampRepairService
) {

  public AnalysisReport analyze(GeoTimeOptions options, GeoLookup lookup) throws Exception {
    List<LogEntry> parsed = new ArrayList<>();
    int invalid = 0;

    for (String line : options.logLines()) {
      logParser.parseLine(line).ifPresentOrElse(parsed::add, () -> {});
    }
    invalid = options.logLines().size() - parsed.size();

    ZoneId srvZone = ZoneId.of(options.serverZoneId());
    List<ResolvedLogEntry> resolved = timestampRepairService.repair(parsed, srvZone);

    int gaps = 0, overlaps = 0, dropped = 0, geoFails = 0;
    List<String> droppedIds = new ArrayList<>();
    Map<String, Long> countries = new TreeMap<>();
    Map<String, Long> timezones = new TreeMap<>();
    long[] globalHist = new long[24];
    Map<String, long[]> tzHists = new HashMap<>();

    for (ResolvedLogEntry res : resolved) {
      if (res.resolutionKind() == ResolutionKind.AMBIGUOUS_DROPPED) {
        dropped++;
        droppedIds.add(res.source().requestId());
        continue;
      }

      if (res.resolutionKind() == ResolutionKind.GAP_REPAIRED) gaps++;
      if (res.resolutionKind() == ResolutionKind.OVERLAP_RESOLVED) overlaps++;

      try {
        GeoInfo geo = lookup.lookup(res.source().clientIp());
        countries.merge(geo.countryCode(), 1L, Long::sum);
        timezones.merge(geo.zoneId().getId(), 1L, Long::sum);

        ZonedDateTime senderTime = res.serverTime().withZoneSameInstant(geo.zoneId());
        int hour = senderTime.getHour();
        globalHist[hour]++;
        tzHists.computeIfAbsent(geo.zoneId().getId(), k -> new long[24])[hour]++;

      } catch (GeoLookupException e) {
        geoFails++;
      }
    }

    return new AnalysisReport(invalid, gaps, overlaps, dropped, geoFails,
            droppedIds, countries, timezones, globalHist, tzHists);
  }
}
