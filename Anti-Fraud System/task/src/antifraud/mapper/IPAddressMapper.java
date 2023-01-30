package antifraud.mapper;

import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;
import org.springframework.stereotype.Component;

@Component
public class IPAddressMapper {

    public IPAddress mapIPDTOToEntity(IPAddressRequest req) {
        var address = new IPAddress();
        address.setAddress(req.getIp());

        return address;
    }

    public IPAddressResultResponse mapIPToIPDTO(IPAddress ipEntity) {
        return new IPAddressResultResponse(ipEntity.getId(),ipEntity.getAddress());
    }

}
