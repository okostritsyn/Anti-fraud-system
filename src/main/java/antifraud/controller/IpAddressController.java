package antifraud.controller;

import antifraud.mapper.IPAddressMapper;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import antifraud.model.response.StatusResponse;
import antifraud.service.IPAddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class IpAddressController {
    IPAddressService ipAddressService;

    @PostMapping(value = "/suspicious-ip")
    IPAddressResultResponse registerIP(@RequestBody @Valid IPAddressRequest req) {
        var IPEntity = ipAddressService.registerSuspiciousIP(req);
        return IPAddressMapper.mapIPToIPDTO(IPEntity);
    }

    @GetMapping(value = "/suspicious-ip")
    List<IPAddressResultResponse> getListOfIP() {
        return ipAddressService.getListOfAddresses();
    }


    @DeleteMapping(value = "/suspicious-ip/{ip}")
    StatusResponse deleteIPAddress(@PathVariable String ip) {
        ipAddressService.deleteIPByAddress(ip);
        var message = "IP " + ip + " successfully removed!";
        return new StatusResponse(message);
    }

}
