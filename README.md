# Server Log Geo-Analyzer

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)

**Server Log Geo-Analyzer** is a specialized Java application designed to parse, validate, and analyze server log entries. It deeply focuses on resolving complex chronological anomalies (such as Daylight Saving Time gaps and overlaps) and enriches log data with client geolocation mapping to generate comprehensive usage reports based on the user's actual local time.

## 🚀 Key Features

* **Advanced Timestamp Repair:** Accurately resolves local time ambiguities (DST overlaps and gaps) using the `java.time.zone.ZoneRules` API.
* **IP Geolocation Integration:** Asynchronously queries the `ipwho.is` API via Java's native `HttpClient` to map IP addresses to country codes and precise timezones.
* **Robust Parsing:** Validates IPv4 addresses, HTTP methods, and parses raw log formats gracefully.
* **Comprehensive Analytics:** Generates detailed text-based reports including:
    * Global error/repair metrics.
    * Request counts aggregated by Country Code and Timezone.
    * 24-hour request histograms normalized to the **sender's local time**.
* **Modern Java Practices:** Built utilizing Java Records, Streams, Optional, and modern I/O operations.

## 🛠️ Tech Stack

* **Language:** Java 17+
* **Dependencies:** [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml) (for configuration loading)
* **External APIs:** [ipwho.is](https://ipwhois.io/) (for IP geolocation)

## 📦 Getting Started

### Prerequisites
* Java Development Kit (JDK) 17 or higher.
* `snakeyaml-2.6.jar` included in your classpath.

### Configuration
The application requires a configuration file named `GeoLogOptions.yaml` placed in your user home directory (`System.getProperty("user.home")`). 

**Example `GeoLogOptions.yaml`:**
```yaml
serverZoneId: "Europe/Warsaw"
logLines:
  - "req-001|2023-10-29T02:30:00|8.8.8.8|GET|/api/v1/data|200|45|1024"
  - "req-002|2023-10-29T02:45:00|1.1.1.1|POST|/api/v1/auth|201|120|2048"
```
*Note: The log format expected is `requestId|serverLocalTime|clientIp|method|endpoint|status|latencyMs|bytes`.*

### Execution
Run the `Main` class. The application will:
1. Load the configuration from `~/GeoLogOptions.yaml`.
2. Parse the log entries.
3. Repair any timezone-related timestamp anomalies based on the `serverZoneId`.
4. Fetch Geo/Timezone data for each client IP.
5. Print a comprehensive analytics report to the standard output.

## 📊 Sample Output

```text
SUMMARY
Metric                      Value
--------------------------  -----
Invalid lines               0
Repaired gap times          0
Resolved overlap entries    0
Dropped ambiguous entries   0
GeoLookup failures          0

AMBIGUOUS REQUEST IDS
(none)

COUNTRIES
Code Count
---- -----
US       2

TIMEZONES
Timezone                 Count
------------------------ -----
America/New_York             1
America/Los_Angeles          1

HOURS (sender)
Hour range Count
----------- -----
14:00-14:59     1
17:00-17:59     1
```

## 👨‍💻 Author
**Jan Myziak** * Index: s31003
* [GitHub Profile](https://github.com/lopez9090)
