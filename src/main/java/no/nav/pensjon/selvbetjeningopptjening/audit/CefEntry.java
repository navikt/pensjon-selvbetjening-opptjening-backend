package no.nav.pensjon.selvbetjeningopptjening.audit;

import org.slf4j.event.Level;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * CEF = ArcSight Common Event Format
 * Note that no character escaping is performed here, since the logged info does not require this.
 */
public class CefEntry {

    private static final int CEF_VERSION = 0;
    private static final String PREAMBLE = "CEF:";
    private static final String SEPARATOR = "|";
    private static final String DEVICE_VENDOR = "pensjon-selvbetjening";
    private static final String DEVICE_PRODUCT = "pensjon-selvbetjening-opptjening";
    private static final String DEVICE_VERSION = "1.0";
    private final long timestamp;
    private final Level level;
    private final String deviceEventClassId;
    private final String name;
    private final String deviceAction;
    private final String sourceUserId;
    private final String destinationUserId;

    public CefEntry(long timestamp,
                    Level level,
                    String deviceEventClassId,
                    String name,
                    String deviceAction,
                    String sourceUserId,
                    String destinationUserId) {
        this.timestamp = timestamp;
        this.level = level;
        this.deviceEventClassId = deviceEventClassId;
        this.name = name;
        this.deviceAction = deviceAction;
        this.sourceUserId = sourceUserId;
        this.destinationUserId = destinationUserId;
    }

    public String format() {
        List<String> elements = List.of(
                PREAMBLE + CEF_VERSION,
                DEVICE_VENDOR,
                DEVICE_PRODUCT,
                DEVICE_VERSION,
                deviceEventClassId,
                name,
                severity(),
                extension());

        return String.join(SEPARATOR, elements);
    }

    private String severity() {
        return level == Level.INFO ? "INFO" : "WARN";
    }

    private String extension() {
        return "end=" + timestamp +
                " suid=" + sourceUserId +
                " duid=" + destinationUserId +
                " act=" + deviceAction;
    }
}
