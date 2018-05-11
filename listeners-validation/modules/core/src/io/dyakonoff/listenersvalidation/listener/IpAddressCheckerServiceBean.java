package io.dyakonoff.listenersvalidation.listener;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.net.InetAddress;

@Service(IpAddressCheckerService.NAME)
public class IpAddressCheckerServiceBean implements IpAddressCheckerService {

    @Inject
    private Logger log;

    @Override
    public boolean checkIpAddrIsReacheble(String ipAddress, int timeoutMs) {
        boolean reachable = false;
        try{
            InetAddress address = InetAddress.getByName(ipAddress);
            reachable = address.isReachable(timeoutMs);

            log.debug("host " + ipAddress + " reachable = " + reachable);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return reachable;
    }
}