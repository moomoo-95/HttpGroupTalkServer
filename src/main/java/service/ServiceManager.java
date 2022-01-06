package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static final String HOST = "192.168.2.163";
    private static final int DELAY_TIME = 1000;
    private static final int MIN_PORT = 5000;
    private static final int MAX_PORT = 7000;
    private static final int SEND_BUF = 1048576;
    private static final int RECV_BUF = 1048576;

    private static ServiceManager serviceManager = null;

    private boolean isQuit = false;

    public ServiceManager() {
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }
        return serviceManager;
    }

    public void loop() {
        if (!start()) {
            log.error("() () () Fail to start service");
        }

        while (!isQuit) {
            try {
                Thread.sleep(DELAY_TIME);
            } catch (Exception e) {
                log.error("ServiceManager.loop ", e);
            }
        }
    }

    public boolean start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.error("Process is about to quit (Ctrl+C)");
            this.isQuit = true;
            this.stop();
        }));

        return true;
    }

    public void stop() {
        // nothing
    }
}
