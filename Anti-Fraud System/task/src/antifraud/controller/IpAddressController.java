package antifraud.controller;

import antifraud.mapper.IPAddressMapper;
import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import antifraud.model.response.StatusResponse;
import antifraud.service.IPAddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class IpAddressController {
    IPAddressService ipAddressService;

    @PostMapping(value = "/suspicious-ip")
    IPAddressResultResponse registerIP(@RequestBody @Valid IPAddressRequest req) {
        log.info("----------POST /suspicious-ip "+req);

        if (!ipAddressService.validateIPAddress(req.getIp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }

        var IPEntity = IPAddressMapper.mapIPDTOToEntity(req);
        var status = ipAddressService.createIP(IPEntity);

        if (!status) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "IP already exist!");
        }

        return IPAddressMapper.mapIPToIPDTO(IPEntity);
    }

    @GetMapping(value = "/suspicious-ip")
    List<IPAddressResultResponse> getListOfIP() {
        var addressList = ipAddressService.getListOfAddresses();
        var listResponse = new ArrayList<IPAddressResultResponse>();

        for (IPAddress address : addressList) {
            var currIP = IPAddressMapper.mapIPToIPDTO(address);
            listResponse.add(currIP);
        }

        return listResponse;
    }


    @DeleteMapping(value = "/suspicious-ip/{ip}")
    StatusResponse deleteIPAddress(@PathVariable String ip) {
        log.info("----------DELETE /suspicious-ip "+ip);

        if (!ipAddressService.validateIPAddress(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }

        var address = ipAddressService.findByAddress(ip);
        boolean status = false;
        if (address != null) status = ipAddressService.deleteAddress(address);
        if (!status) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found!");
        }
        var message = "IP " + ip + " successfully removed!";
        return new StatusResponse(message);
    }

}
