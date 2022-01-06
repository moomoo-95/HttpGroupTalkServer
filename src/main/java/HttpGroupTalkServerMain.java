import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ServiceManager;

public class HttpGroupTalkServerMain {
    private static final Logger log = LoggerFactory.getLogger(HttpGroupTalkServerMain.class);

    public static void main(String[] args) {

        log.debug("HttpGroupTalkServerMain Start.");
        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();

    }
}
