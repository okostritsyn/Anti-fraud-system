package antifraud.service.impl;

import antifraud.exception.ConflictRegisterEntityException;
import antifraud.exception.EntityNotExist;
import antifraud.exception.ValidationDTOFailedException;
import antifraud.mapper.IPAddressMapper;
import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import antifraud.repository.IPAddressRepository;
import antifraud.service.IPAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IPAddressServiceImpl implements IPAddressService {
    private final IPAddressRepository ipAddressRepository;

    public void validateIPAddress(String ipAddress) {
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (!validator.isValidInet4Address(ipAddress)) {
            throw new ValidationDTOFailedException("IP validate failed!");
        }
    }
    public IPAddress registerSuspiciousIP(IPAddressRequest req) {
        validateIPAddress(req.getIp());
        if (ipAddressRepository.findByIP(req.getIp()).isPresent()) {
            throw new ConflictRegisterEntityException("IP already exist!");
        }
        var IPEntity = IPAddressMapper.mapIPDTOToEntity(req);

        createIP(IPEntity);

        return IPEntity;
    }

    public void deleteIPByAddress(String ip) {
        validateIPAddress(ip);
        var address = findByAddress(ip);
        boolean status = false;
        if (address.isPresent()) status = deleteAddress(address.get());
        if (!status) {
            throw new EntityNotExist("Address not found!");
        }
    }

    public List<IPAddressResultResponse> getListOfAddresses() {
        var addressList = ipAddressRepository.findAll();
        var listResponse = new ArrayList<IPAddressResultResponse>();

        for (IPAddress address : addressList) {
            var currIP = IPAddressMapper.mapIPToIPDTO(address);
            listResponse.add(currIP);
        }

        return listResponse;
    }

    public Optional<IPAddress> findByAddress(String address){
        return ipAddressRepository.findByIP(address);
    }

    private void createIP(IPAddress ipEntity) {
        ipAddressRepository.save(ipEntity);
        log.info("Registered address with ip "+ipEntity.getAddress());
    }

    private boolean deleteAddress(IPAddress address){
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
