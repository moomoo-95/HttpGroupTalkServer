package moomoo.hgtp.server.service.scheduler.handler;

import moomoo.hgtp.server.service.scheduler.base.ScheduleUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionMonitor extends ScheduleUnit {
    private static final Logger log = LoggerFactory.getLogger(SessionMonitor.class);

    public SessionMonitor(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        sessionCount();
    }

    private void sessionCount() {
        // Session Average
        log.debug("[USER: {}] [ROOM: {}]", sessionManager.getUserInfoSize(), sessionManager.getRoomInfoSize());
    }
}
