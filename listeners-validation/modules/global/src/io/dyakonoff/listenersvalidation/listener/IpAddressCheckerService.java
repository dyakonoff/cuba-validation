package io.dyakonoff.listenersvalidation.listener;


public interface IpAddressCheckerService {
    String NAME = "listenersvalidation_IpAddressCheckerService";

    boolean checkIpAddrIsReacheble(String ipAddress, int timeoutMs);
}