/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class OptionsLoader {
  public GeoTimeOptions load(String fileName) throws Exception {
    Yaml yaml = new Yaml();
    try (InputStream inputStream = Files.newInputStream(Paths.get(fileName))) {

      Map<String, Object> data = yaml.load(inputStream);
      String serverZoneId = (String) data.get("serverZoneId");

      if (serverZoneId == null || serverZoneId.isBlank()) {

        throw new IllegalArgumentException("serverZoneId is required and cannot be empty");

      }

      @SuppressWarnings("unchecked")

      List<String> logLines = (List<String>) data.getOrDefault("logLines", List.of());

      return new GeoTimeOptions(serverZoneId, logLines);
    }
  }
}
