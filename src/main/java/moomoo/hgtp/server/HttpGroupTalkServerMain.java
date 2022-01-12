package moomoo.hgtp.server;

import moomoo.hgtp.server.config.ConfigManager;
import moomoo.hgtp.server.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGroupTalkServerMain {
    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkServerMain.class);

    public static void main(String[] args) {

        log.debug("HttpGroupTalkServerMain Start.");
        ConfigManager.getInstance();

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();

    }
}
