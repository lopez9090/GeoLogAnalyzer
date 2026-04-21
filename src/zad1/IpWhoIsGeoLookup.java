/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


import java.time.ZoneId;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpWhoIsGeoLookup implements GeoLookup {

  private final HttpClient client = HttpClient.newHttpClient();

  @Override
  public GeoInfo lookup(String ip) throws GeoLookupException {
    try {
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("https://ipwho.is/" + ip))
              .GET()
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return parseGeoInfo(response.body());

    } catch (Exception e) {
      throw new GeoLookupException("Błąd podczas pobierania danych dla IP: " + ip, e);
    }
  }

  public GeoInfo parseGeoInfo(String json) throws GeoLookupException {
    if (json == null || !json.contains("\"success\":true")) {
      throw new GeoLookupException("API ipwho.is zwróciło błąd lub brakuje danych.");
    }

    String countryCode = findValue(json, "\"country_code\":\"([^\"]+)\"");
    String timezoneId = findValue(json, "\"timezone\":\\{.*?\"id\":\"([^\"]+)\"");

    if (countryCode == null || timezoneId == null) {
      throw new GeoLookupException("Niekompletne dane JSON.");
    }

    return new GeoInfo(countryCode, ZoneId.of(timezoneId));
  }

  private String findValue(String text, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(text);
    return matcher.find() ? matcher.group(1) : null;
  }
}

