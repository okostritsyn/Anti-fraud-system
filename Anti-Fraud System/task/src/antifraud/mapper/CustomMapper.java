package antifraud.mapper;

import antifraud.model.Card;
import antifraud.model.IPAddress;
import antifraud.model.Transaction;
import antifraud.model.User;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.CardResponse;
import antifraud.model.response.IPAddressResultResponse;
import antifraud.model.response.TransactionResponse;
import antifraud.model.response.UserResultResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomMapper {
    CustomMapper INSTANCE = Mappers.getMapper(CustomMapper.class);

    User mapUserDTOToEntity(UserCreationRequest userDTO);
    @Mapping(target = "role", expression = "java(userEntity.getFirstRole())")
    UserResultResponse mapUserToUserDTO(User userEntity);

    @Mapping(target="address", source="req.ip")
    IPAddress mapIPDTOToEntity(IPAddressRequest req);
    @Mapping(target="ip", source="ipEntity.address")
    IPAddressResultResponse mapIPToIPDTO(IPAddress ipEntity);

    @Mapping(target="number", source="number")
    Card mapStringNumberToEntity(String number);
    CardResponse mapStolenCardToCardDTO(Card card);

    @Mapping(target="region", source="transactionRequest.region")
    @Mapping(target="dateOfCreation", source="transactionRequest.date")
    Transaction mapTransactionDTOToEntity(TransactionRequest transactionRequest);

    @Mapping(target="transactionId", source="transaction.id")
    @Mapping(target="result", expression = "java(transaction.getResult()==null?\"\":transaction.getResult().name())")
    @Mapping(target="feedback", source = "transaction.feedback",
            defaultValue = "")
    @Mapping(target="number", source = "transaction.card.number",
            defaultValue = "")
    @Mapping(target="date", source="transaction.dateOfCreation")
    TransactionResponse mapTransactionEntityToDTO(Transaction transaction);

}
