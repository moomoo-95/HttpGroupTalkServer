package moomoo.hgtp.server;

import moomoo.hgtp.server.service.AppInstance;
import moomoo.hgtp.server.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpGroupTalkServerMain {
    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkServerMain.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Fail to argument &config path");
            return;
        }

        log.debug("HttpGroupTalkServerMain Start.");
        AppInstance appInstance = AppInstance.getInstance();
        appInstance.setConfigManager(args[0]);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();

    }
}
