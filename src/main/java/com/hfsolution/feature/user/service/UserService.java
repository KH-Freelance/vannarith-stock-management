package com.hfsolution.feature.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.feature.token.repository.TokenRepository;
import com.hfsolution.feature.user.dto.ChangePasswordRequest;
import com.hfsolution.feature.user.dto.ChangeRoleRequest;
import com.hfsolution.feature.user.dto.DeleteUserRequest;
import com.hfsolution.feature.user.dto.ResetPasswordRequest;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.enums.Role;
import com.hfsolution.feature.user.repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final SearchFilter<User> searchFilter;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            // throw new IllegalStateException("Wrong password");
            throw new AppException("003");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            // throw new IllegalStateException("Password are not the same");
            throw new AppException("004");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public void resetPassword(ResetPasswordRequest request, Integer userId) {

        Optional<User> opUser = repository.findById(userId);
        if(!opUser.isPresent()){
            throw new AppException("002");
            // throw new IllegalStateException("User not Found");
        }
        
        // update the password
        opUser.get().setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(opUser.get());
    }

    public void changeRole(ChangeRoleRequest request, Integer userId) {
        Optional<User> opUser = repository.findById(userId);
        if(!opUser.isPresent()){
            throw new AppException("002");
            // throw new IllegalStateException("User not Found");
        }
        // // update the password
        opUser.get().setRole(request.getNewRole());
        // save the new password
        repository.save(opUser.get());
    }

    public User getInfo(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return user;
    }

    public Page<User> searchUser(SearchRequestDTO request) {
        Specification<User> users = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
        Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
        return repository.findAll(users,pageable);
    }

    public Page<User> getAllUsers(int page,int size, Sort.Direction sort,String sortByColumn) {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(page);
        pageRequestDto.setPageSize(size);
        pageRequestDto.setSort(sort);
        pageRequestDto.setSortByColumn(sortByColumn);
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
        return repository.findAll(pageable);
    }

    public void deleteUser(Integer userId) {

        Optional<User> opUser = repository.findById(userId);
        if(!opUser.isPresent()){
            throw new AppException("002");
        }
        tokenRepository.deleteByUserId(opUser.get().getId());
        repository.deleteById(opUser.get().getId());

    }
}
