/**
 *
 *  @author Myziak Jan s31003
 *
 */

package zad1;


public interface GeoLookup {
  GeoInfo lookup(String ip) throws GeoLookupException;
}
