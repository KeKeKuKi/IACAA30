package com.gapache.cloud.auth.server.controller;

import com.gapache.cloud.auth.server.model.UserClientRelationDTO;
import com.gapache.cloud.auth.server.service.ClientService;
import com.gapache.cloud.auth.server.service.UserClientRelationService;
import com.gapache.commons.model.JsonResult;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.gapache.security.model.ClientDTO;
import org.springframework.web.bind.annotation.*;

/**
 * @author HuSen
 * @since 2020/8/3 11:34 上午
 */
@RestController
@RequestMapping("/api/client")
@NeedAuth("client")
public class ClientController {

    private final ClientService clientService;

    private final UserClientRelationService userClientRelationService;

    public ClientController(ClientService clientService, UserClientRelationService userClientRelationService) {
        this.clientService = clientService;
        this.userClientRelationService = userClientRelationService;
    }

    @PostMapping
    @AuthResource(scope = "create", name = "创建客户端")
    public JsonResult<Boolean> create(@RequestBody ClientDTO clientDTO) {
        return JsonResult.of(clientService.create(clientDTO));
    }

    @PostMapping("/bindUser")
    @AuthResource(scope = "bindUser", name = "绑定用户到客户端")
    public JsonResult<Boolean> bindUser(@RequestBody UserClientRelationDTO userClientRelationDTO) {
        return JsonResult.of(userClientRelationService.create(userClientRelationDTO));
    }

    @GetMapping("/findByClientId/{clientId}")
    @AuthResource(scope = "findByClientId", name = "根据clientId查询client")
    public JsonResult<ClientDTO> findByClientId(@PathVariable String clientId) {
        return JsonResult.of(clientService.findDtoByClientId(clientId));
    }
}
