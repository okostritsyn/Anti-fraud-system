package antifraud.mapper;

import antifraud.model.IPAddress;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.response.IPAddressResultResponse;

public class IPAddressMapper {

    public static IPAddress mapIPDTOToEntity(IPAddressRequest req) {
        var address = new IPAddress();
        address.setAddress(req.getIp());

        return address;
    }

    public static IPAddressResultResponse mapIPToIPDTO(IPAddress ipEntity) {
        return new IPAddressResultResponse(ipEntity.getId(),ipEntity.getAddress());
    }

}
