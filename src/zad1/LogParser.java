/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class LogParser {

  public Optional<LogEntry> parseLine(String line) {
    if (line == null || line.isBlank()) {
      return Optional.empty();
    }

    String[] parts = line.split("\\|", -1);

    if (parts.length != 8) {
      return Optional.empty();
    }

    try {
      String requestId = parts[0];
      if (requestId.isBlank()) return Optional.empty();

      LocalDateTime serverLocalTime = LocalDateTime.parse(parts[1]);

      String clientIp = parts[2];
      if (!isValidIPv4(clientIp)) {
        return Optional.empty();
      }

      String method = parts[3];
      String endpoint = parts[4];
      if (method.isBlank() || endpoint.isBlank()) {
        return Optional.empty();
      }

      int status = Integer.parseInt(parts[5]);
      int latencyMs = Integer.parseInt(parts[6]);
      int bytes = Integer.parseInt(parts[7]);


      if (latencyMs < 0 || bytes < 0) {
        return Optional.empty();
      }

      return Optional.of(new LogEntry(
              requestId,
              serverLocalTime,
              clientIp,
              method,
              endpoint,
              status,
              latencyMs,
              bytes
      ));

    } catch (NumberFormatException | DateTimeParseException e) {
      return Optional.empty();
    }
  }

  private boolean isValidIPv4(String ip) {
    if (ip == null || ip.isBlank()) return false;

    String[] octets = ip.split("\\.");
    if (octets.length != 4) {
      return false;
    }

    try {
      for (String octet : octets) {
        int val = Integer.parseInt(octet);
        if (val < 0 || val > 255) {
          return false;
        }
      }
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}