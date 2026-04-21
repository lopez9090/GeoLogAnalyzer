/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


import java.time.ZonedDateTime;

public record ResolvedLogEntry(
    LogEntry source,
    ZonedDateTime serverTime,
    ResolutionKind resolutionKind
) {}
