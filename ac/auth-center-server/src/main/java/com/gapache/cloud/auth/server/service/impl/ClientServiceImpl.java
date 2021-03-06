package com.gapache.cloud.auth.server.service.impl;

import com.gapache.cloud.auth.server.dao.entity.ClientEntity;
import com.gapache.cloud.auth.server.dao.entity.ResourceEntity;
import com.gapache.cloud.auth.server.dao.repository.client.ClientRepository;
import com.gapache.cloud.auth.server.dao.repository.resource.ResourceRepository;
import com.gapache.cloud.auth.server.model.ClientDetailsImpl;
import com.gapache.cloud.auth.server.service.ClientService;
import com.gapache.cloud.auth.server.utils.DynamicPropertyUtils;
import com.gapache.commons.model.ThrowUtils;
import com.gapache.security.model.ClientDTO;
import com.gapache.security.model.SecurityError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author HuSen
 * @since 2020/7/31 5:24 下午
 */
@Service
public class ClientServiceImpl implements ClientService {

    @Resource
    private ClientRepository clientRepository;

    @Resource
    private ResourceRepository resourceRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ClientDetailsImpl findByClientId(String clientId) {
        ClientEntity clientEntity = clientRepository.findByClientId(clientId);
        if (clientEntity == null) {
            return null;
        }
        ClientDetailsImpl clientDetails = new ClientDetailsImpl();
        BeanUtils.copyProperties(clientEntity, clientDetails);
        clientDetails.setId(clientEntity.getId());

        String authCenterClientId = DynamicPropertyUtils.getString("auth.center.client-id");
        if (StringUtils.equals(authCenterClientId, clientId)) {
            List<ResourceEntity> allResource = resourceRepository.findAll();
            clientDetails.setScopes(allResource.stream().map(r -> r.getResourceServerName() + ":" + r.getScope()).collect(Collectors.joining(",")));
        }
        return clientDetails;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(ClientDTO clientDTO) {
        Boolean exists = clientRepository.existsByClientId(clientDTO.getClientId());
        ThrowUtils.throwIfTrue(exists, SecurityError.CLIENT_EXISTED);

        ClientEntity entity = new ClientEntity();
        BeanUtils.copyProperties(clientDTO, entity, "secret");
        entity.setSecret(passwordEncoder.encode(clientDTO.getSecret()));
        if (StringUtils.isNotBlank(clientDTO.getPrivateKey())) {
            stringRedisTemplate.opsForValue().set("CLIENT_PRIVATE_KEY:" + clientDTO.getClientId(), clientDTO.getPrivateKey());
        }
        return clientRepository.save(entity).getId() != null;
    }

    @Override
    public ClientDetailsImpl findById(Long id) {
        Optional<ClientEntity> optional = clientRepository.findById(id);
        return optional.map(clientEntity -> {
            ClientDetailsImpl clientDetails = new ClientDetailsImpl();
            BeanUtils.copyProperties(clientEntity, clientDetails);
            return clientDetails;
        }).orElse(null);
    }

    @Override
    public ClientDTO findDtoByClientId(String clientId) {
        ClientEntity clientEntity = clientRepository.findByClientId(clientId);
        if (clientEntity == null) {
            return null;
        }

        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(clientEntity, dto);
        dto.setId(clientEntity.getId());
        return dto;
    }
}
