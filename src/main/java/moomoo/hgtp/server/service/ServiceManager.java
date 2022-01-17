package moomoo.hgtp.server.service;

import moomoo.hgtp.server.network.NetworkManager;
import moomoo.hgtp.server.protocol.hgtp.HgtpManager;
import moomoo.hgtp.server.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static final int DELAY_TIME = 1000;

    private static ServiceManager serviceManager = null;

    private HgtpManager hgtpManager;
    private NetworkManager networkManager;

    private boolean isQuit = false;

    public ServiceManager() {
        // nothing
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

        // HgtpManager
        hgtpManager = HgtpManager.getInstance();
        hgtpManager.startHgtp();

        // NetworkManager
        networkManager = NetworkManager.getInstance();
        networkManager.startSocket();

        SessionManager.getInstance();

        return true;
    }

    public void stop() {
        hgtpManager.stopHgtp();
        networkManager.stopSocket();

    }
}
