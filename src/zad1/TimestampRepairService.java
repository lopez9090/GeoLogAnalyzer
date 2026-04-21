/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.List;

public class TimestampRepairService {

  public List<ResolvedLogEntry> repair(List<LogEntry> entries, ZoneId serverZone) {
    List<ResolvedLogEntry> result = new ArrayList<>();
    ZoneRules rules = serverZone.getRules();

    int i = 0;
    while (i < entries.size()) {
      LogEntry entry = entries.get(i);
      LocalDateTime ldt = entry.serverLocalTime();
      ZoneOffsetTransition transition = rules.getTransition(ldt);

      if (transition != null && transition.isGap()) {
        ZonedDateTime repairedTime = ldt.atZone(serverZone);
        result.add(new ResolvedLogEntry(entry, repairedTime, ResolutionKind.GAP_REPAIRED));
        i++;
      }
      else {
        List<ZoneOffset> validOffsets = rules.getValidOffsets(ldt);

        if (validOffsets.size() == 1) {
          result.add(new ResolvedLogEntry(entry, ldt.atZone(serverZone), ResolutionKind.OK));
          i++;
        }
        else {
          int startBlock = i;
          int endBlock = i;

          while (endBlock < entries.size() &&
                  rules.getValidOffsets(entries.get(endBlock).serverLocalTime()).size() > 1) {
            endBlock++;
          }

          int pivotIndex = -1;
          for (int j = startBlock; j < endBlock - 1; j++) {
            if (entries.get(j).serverLocalTime().isAfter(entries.get(j + 1).serverLocalTime())) {
              pivotIndex = j;
              break;
            }
          }

          if (pivotIndex != -1) {
            for (int j = startBlock; j <= pivotIndex; j++) {
              ZonedDateTime time = entries.get(j).serverLocalTime().atOffset(validOffsets.get(0)).toZonedDateTime();
              result.add(new ResolvedLogEntry(entries.get(j), time, ResolutionKind.OVERLAP_RESOLVED));
            }
            for (int j = pivotIndex + 1; j < endBlock; j++) {
              ZonedDateTime time = entries.get(j).serverLocalTime().atOffset(validOffsets.get(1)).toZonedDateTime();
              result.add(new ResolvedLogEntry(entries.get(j), time, ResolutionKind.OVERLAP_RESOLVED));
            }
          } else {
            for (int j = startBlock; j < endBlock; j++) {
              result.add(new ResolvedLogEntry(entries.get(j), null, ResolutionKind.AMBIGUOUS_DROPPED));
            }
          }
          i = endBlock;
        }
      }
    }
    return result;
  }
}
