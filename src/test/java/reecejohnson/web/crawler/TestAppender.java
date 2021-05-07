package reecejohnson.web.crawler;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestAppender extends AppenderBase<ILoggingEvent> {
    static List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        events.add(eventObject);
    }

    public List<ILoggingEvent> findLogsContaining(final String message) {
        return events.stream()
                .filter(log -> log.getFormattedMessage().contains(message))
                .collect(Collectors.toList());
    }
}
