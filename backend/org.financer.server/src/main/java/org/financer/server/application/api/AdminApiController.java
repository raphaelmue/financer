package org.financer.server.application.api;

import org.financer.server.application.model.user.UserAssembler;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.user.Role;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.api.admin.AdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.InitAdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.UpdateAdminConfigurationDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.util.mapping.ModelMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class AdminApiController implements AdminApi {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserAssembler userAssembler;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private AdminConfigurationService adminConfigurationService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<AdminConfigurationDTO> getConfiguration() {
        authenticationService.throwIfUserHasNotRole(Role.ROLE_ADMIN);
        return new ResponseEntity<>(ModelMapperUtils.map(adminConfigurationService, AdminConfigurationDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminConfigurationDTO> updateConfiguration(@NotNull @Valid UpdateAdminConfigurationDTO updateAdminConfigurationDTO) {
        authenticationService.throwIfUserHasNotRole(Role.ROLE_ADMIN);
        return new ResponseEntity<>(ModelMapperUtils.map(
                this.adminConfigurationService.updateProperties(updateAdminConfigurationDTO.getDefaultLanguage(),
                        updateAdminConfigurationDTO.getDefaultCurrency()),
                AdminConfigurationDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminConfigurationDTO> initializeServerConfiguration(@NotNull @Valid InitAdminConfigurationDTO initAdminConfigurationDTO) {
        authenticationService.throwIfUserHasNotRole(Role.ROLE_ADMIN);
        this.userDomainService.registerUser(modelMapper.map(initAdminConfigurationDTO.getAdminUser(), User.class),
                new IPAddress(request.getRemoteAddr()), null);
        return new ResponseEntity<>(ModelMapperUtils.map(
                this.adminConfigurationService.updateProperties(initAdminConfigurationDTO.getDefaultLanguage(),
                        initAdminConfigurationDTO.getDefaultCurrency()),
                AdminConfigurationDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedModel<UserDTO>> getUsers(@Valid Pageable pageable) {
        return new ResponseEntity<>(userAssembler.toPagedModel(userDomainService.fetchUsers(pageable)), HttpStatus.OK);
    }
}
