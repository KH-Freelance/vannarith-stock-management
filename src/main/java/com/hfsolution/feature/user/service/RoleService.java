package com.hfsolution.feature.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.user.entity.Permission;
import com.hfsolution.feature.user.entity.Role;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.PermissionRepository;
import com.hfsolution.feature.user.repository.RoleRepository;
import com.hfsolution.feature.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Role createRole(String name, String description, List<Long> permissionIds) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);

        // Fetch permissions by their IDs and add them to the role
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        for (Permission permission : permissions) {
            role.addPermission(permission);
        }

        return repository.save(role);
    }
    public Page<Role> search(String q, int page,int size, Sort.Direction sort,String sortByColumn) {
        Specification<Role> roles = new CustomSpecification<>(q);
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(page);
        pageRequestDto.setPageSize(size);
        pageRequestDto.setSort(sort);
        pageRequestDto.setSortByColumn(sortByColumn);
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
        return repository.findAll(roles,pageable);
    } 

    public void delete(Long id) {
        Optional<Role> opRole = repository.findById(id);
        if(!opRole.isPresent()){
            throw new AppException("040");
        }
        List<User> usersWithRole = userRepository.findByRoleId(id);
        if (!usersWithRole.isEmpty()) {
            String userNames = usersWithRole.stream()
                    .map(user -> user.getFirstname() + " " + user.getLastname())
                    .collect(Collectors.joining(", "));
            
            String msg = AppTools.appGetMessage("052").replace("[users]",userNames);
            throw new AppException("052",msg,"Y");
        }
        opRole.get().getPermissions().clear();
        repository.delete(opRole.get());
    }

    public Role getPermission(Long id) {
        Optional<Role> opRole = repository.findById(id);
        if(!opRole.isPresent()){
            throw new AppException("040");
        }

       return opRole.get();
    }

    public Role addPermission(Long id, List<Long> permissionIds) {
        Optional<Role> opRole = repository.findById(id);
        if(!opRole.isPresent()){
            throw new AppException("040");
        }

        // Fetch the permissions by their IDs
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        // Add each permission to the role
        for (Permission permission : permissions) {
            opRole.get().addPermission(permission);
        }
        
        // Save the role with the updated permissions
        return repository.save(opRole.get());
    }

    public Role removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        // Fetch the role by its ID
        Role role = repository.findById(roleId)
            .orElseThrow(() -> new AppException("040"));

        // Fetch the permissions by their IDs
        List<Permission> permissionsToRemove = permissionRepository.findAllById(permissionIds);

        // Remove each permission from the role
        for (Permission permission : permissionsToRemove) {
            role.removePermission(permission);
        }

        // Save the role with the updated permissions
        return repository.save(role);
    }

    public Page<Permission> searchPermission(String q, int page,int size, Sort.Direction sort,String sortByColumn) {
        Specification<Permission> permissions = new CustomSpecification<>(q);
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(page);
        pageRequestDto.setPageSize(size);
        pageRequestDto.setSort(sort);
        pageRequestDto.setSortByColumn(sortByColumn);
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
        return permissionRepository.findAll(permissions,pageable);
    } 
}
