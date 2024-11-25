package com.fcastro.purchaseservice.supermarket;

import com.fcastro.commons.exception.ResourceNotFoundException;
import com.fcastro.security.authorization.AuthorizationClient;
import com.fcastro.security.exception.AccessControlNotDefinedException;
import com.fcastro.security.modelclient.AccessControlDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupermarketService {

    private final SupermarketRepository repository;
    private final ModelMapper modelMapper;
    private final AuthorizationClient authorizationHandler;

    public SupermarketService(SupermarketRepository repository, ModelMapper modelMapper, AuthorizationClient authorizationHandler) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.authorizationHandler = authorizationHandler;
    }

    public Optional<SupermarketDto> get(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    public List<SupermarketDto> getAll(String email, Long accountGroupId) {
        List<AccessControlDto> accessControlList;
        if (accountGroupId != null) {
            accessControlList = authorizationHandler.listAccessControlStrict(email, Supermarket.class.getSimpleName(), accountGroupId);
        } else {
            accessControlList = authorizationHandler.listAccessControl(email, Supermarket.class.getSimpleName(), null, null, null);
        }
        return getAll(accessControlList);
    }

    private List<SupermarketDto> getAll(List<AccessControlDto> accessControlList) {
        var supermarketIds = accessControlList.stream().map(AccessControlDto::getClazzId).collect(Collectors.toSet());
        var list = repository.findAllByIds(supermarketIds).stream().map(this::convertToDTO).collect(Collectors.toList());
        list = embedAccountGroup(list, accessControlList);
        return list;
    }

    //It finds and attaches AccountGroup to each Supermarket in the list
    private List<SupermarketDto> embedAccountGroup(List<SupermarketDto> list, List<AccessControlDto> accessControlList) {
        return list.stream().map((pantry) -> embedAccountGroup(pantry, accessControlList)).collect(Collectors.toList());
    }

    //It finds and attaches AccountGroup to the Supermarket
    private SupermarketDto embedAccountGroup(SupermarketDto dto, List<AccessControlDto> accessControlList) {
        accessControlList.stream()
                .filter((accessControl) -> accessControl.getClazzId() == dto.getId())
                .findFirst()
                .ifPresentOrElse(
                        accessControl -> dto.setAccountGroup(accessControl.getAccountGroup()),
                        () -> {
                            throw new AccessControlNotDefinedException("Unable to embed AccountGroup to Supermarke [" + dto.getId() + ": " + dto.getName());
                        }
                );
        return dto;
    }

    public SupermarketDto save(SupermarketDto dto) {
        if (dto.getAccountGroup() == null || dto.getAccountGroup().getId() == 0)
            throw new AccessControlNotDefinedException("Supermarket must be associated to an Account Group");


        var supermarktExists = dto.getId() > 0 ? repository.findById(dto.getId()) : Optional.empty();

        var entity = repository.save(convertToEntity(dto));

        //Add to access control only when adding a new Supermarket
        if (!supermarktExists.isPresent()) {
            authorizationHandler.saveAccessControl(Supermarket.class.getSimpleName(), entity.getId(), dto.getAccountGroup().getId());
        }

        var storedDto = convertToDTO(entity);
        storedDto.setAccountGroup(dto.getAccountGroup());

        return storedDto;
    }

    @Transactional
    public void delete(long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supermarket not found"));

        repository.deleteById(id);

        authorizationHandler.deleteAccessControl(Supermarket.class.getSimpleName(), id);
    }

    private SupermarketDto convertToDTO(Supermarket entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, SupermarketDto.class);
    }

    private Supermarket convertToEntity(SupermarketDto dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Supermarket.class);
    }
}
