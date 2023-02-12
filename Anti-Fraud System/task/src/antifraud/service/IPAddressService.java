package antifraud.service;

import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import java.util.List;
import java.util.Optional;

public interface IPAddressService {
    void validateIPAddress(String ipAddress);
    IPAddress registerSuspiciousIP(IPAddressRequest req);

    void deleteIPByAddress(String ip);

    List<IPAddressResultResponse> getListOfAddresses();

    Optional<IPAddress> findByAddress(String address);
}
