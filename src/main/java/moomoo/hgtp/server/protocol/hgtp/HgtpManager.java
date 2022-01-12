package moomoo.hgtp.server.protocol.hgtp;


import util.module.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HgtpManager {

    private static final int HGTP_THREAD_SIZE = 64;

    private static HgtpManager hgtpManager = null;

    private final ExecutorService executorService;
    private final ConcurrentCyclicFIFO<byte[]> hgtpQueue;


    public HgtpManager() {
        this.executorService = Executors.newFixedThreadPool(HGTP_THREAD_SIZE);
        this.hgtpQueue = new ConcurrentCyclicFIFO<>();
    }

    public static HgtpManager getInstance() {
        if (hgtpManager == null) {
            hgtpManager = new HgtpManager();
        }
        return hgtpManager;
    }

    public void startHgtp() {
        for (int index = 0; index < HGTP_THREAD_SIZE; index++) {
            executorService.execute(new HgtpConsumer(hgtpQueue));
        }
    }

    public void stopHgtp() {
        executorService.shutdown();
    }
}
