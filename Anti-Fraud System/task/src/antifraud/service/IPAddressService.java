package antifraud.service;

import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import antifraud.repository.IPAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IPAddressService {
    private final IPAddressRepository ipAddressRepository;

    public boolean validateIPAddress(String ipAddress) {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        return validator.isValidInet4Address(ipAddress);
    }

    public IPAddress mapIPDTOToEntity(IPAddressRequest req) {
        var address = new IPAddress();
        address.setAddress(req.getIp());

        return address;
    }

    public boolean createIP(IPAddress ipEntity) {
        if (ipAddressRepository.findByIP(ipEntity.getAddress()) != null) {
            return false;
        }
        ipAddressRepository.save(ipEntity);
        log.info("Registered address with ip "+ipEntity.getAddress());
        return true;
    }

    public IPAddressResultResponse mapIPToIPDTO(IPAddress ipEntity) {
        return new IPAddressResultResponse(ipEntity.getId(),ipEntity.getAddress());
    }

    public List<IPAddress> getListOfAddresses() {
        return ipAddressRepository.findAll();
    }

    public IPAddress findByAddress(String address){
        return ipAddressRepository.findByIP(address);
    }

    public boolean deleteAddress(IPAddress address){
        boolean status = false;
        try {
            ipAddressRepository.delete(address);
            status = true;
        }catch (OptimisticLockingFailureException ex) {
            log.error("An error while delete ip address "+address.getAddress(),ex);
        }
        return status;
    }

}
