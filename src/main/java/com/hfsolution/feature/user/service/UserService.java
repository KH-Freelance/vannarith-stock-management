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
import org.springframework.web.multipart.MultipartFile;

import static com.hfsolution.app.constant.AppConstant.STOCK_USER;
import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import com.hfsolution.feature.user.entity.Role;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.properties.CloudinaryProperties;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.token.repository.TokenRepository;
import com.hfsolution.feature.user.dto.ChangePasswordRequest;
import com.hfsolution.feature.user.dto.ChangeRoleRequest;
import com.hfsolution.feature.user.dto.DeleteUserRequest;
import com.hfsolution.feature.user.dto.ResetPasswordRequest;
import com.hfsolution.feature.user.dto.UserUpdateRequest;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.RoleRepository;
import com.hfsolution.feature.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final CloudinaryProperties cloudinaryProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PurchaseDao purchaseDao;
    private final StockDao stockDao;
    private final HttpServletResponse response;
    private final RoleRepository roleRepository;
    private final String CSV_FILENAME="user";
    // private final CustomSpecification<User> customSpecification;


    

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


    public void update(long id, UserUpdateRequest userUpdateRequest) {


        //check exist
        Optional<User> opUser = repository.findById(id);
        if(!opUser.isPresent()){
            throw new AppException("002");
        }

        User user = opUser.get();
        user.setFirstname(userUpdateRequest.getFirstname());
        user.setLastname(userUpdateRequest.getLastname());

        // save update user
        repository.save(user);
    }
    public void update(long id, UserUpdateRequest userUpdateRequest, MultipartFile file) {


        //check exist
        Optional<User> opUser = repository.findById(id);
        if(!opUser.isPresent()){
            throw new AppException("002");
        }
        
        User user = opUser.get();
        user.setFirstname(userUpdateRequest.getFirstname());
        user.setLastname(userUpdateRequest.getLastname());
        try {
            if(file == null){
                user.setImageUrl(cloudinaryProperties.getDefaultImage());
            }else{
                user.setImageUrl(uploadImage(file, STOCK_USER+"/"+String.valueOf(id)).get("secure_url").toString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // save update user
        repository.save(user);
    }

    public void resetPassword(ResetPasswordRequest request, Long userId) {

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

    public void changeRole(ChangeRoleRequest request, Long userId) {
        Optional<User> opUser = repository.findById(userId);
        if(!opUser.isPresent()){
            throw new AppException("002");
            // throw new IllegalStateException("User not Found");
        }
        // // update the password
        Optional<Role> opRole = roleRepository.findById(request.getRoleId());
        if(!opRole.isPresent()){
            throw new AppException("040");
        }

        opUser.get().setRole(opRole.get());
        // save the new password
        repository.save(opUser.get());
    }

    public User getInfo(Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return user;
    }

    

    @SuppressWarnings("rawtypes")
    public Page<User> searchUser(String q, int page,int size, Sort.Direction sort,String sortByColumn) {

        // Define ENUM fields in the entity and their types
        Map<String, Class<? extends Enum>> enumFields = new HashMap<>();
        // enumFields.put("role", Role.class);  // Assuming "status" is an ENUM field in the entity
        Specification<User> users = new CustomSpecification<>(q,enumFields);
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(page);
        pageRequestDto.setPageSize(size);
        pageRequestDto.setSort(sort);
        pageRequestDto.setSortByColumn(sortByColumn);
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
        return repository.findAll(users,pageable);
    }


    public void export(String q) {
        try {
            CSVHelper<User> csvService = new CSVHelper<>(User.class,response);
            Specification<User> users = new CustomSpecification<>(q);
            List<User> userResult = repository.findAll(users);
            csvService.export(userResult, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("015",e.getMessage(),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    public Object importData(MultipartFile file) {
        try {
            CSVHelper<User> csvService = new CSVHelper<>(User.class);
            List<User> usererList = csvService.parseCsv(file);
            repository.saveAll(usererList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("023"));
            response.setCode("023");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("022",e.getMessage(),true);
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
        }
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

    public void deleteUser(Long userId) {

        Optional<User> opUser = repository.findById(userId);
        if(!opUser.isPresent()){
            throw new AppException("002");
        }

        BaseEntityResponseDto<Purchase>  purchaseResult =  purchaseDao.findPurchaseByUserId(userId);
        if(purchaseResult.getEntityList()!=null && purchaseResult.getEntityList().size() > 0){
            String msg = AppTools.appGetMessage("0050").replace("[purchaseCodes]", String.join(", ", purchaseResult.getEntityList().stream().limit(3).map(purchase->purchase.getPurchaseCode()).toArray(String[]::new)) + (purchaseResult.getEntityList().size() > 3 ? "..." : ""));
            throw new AppException("0050",msg, "Y");
        }

        BaseEntityResponseDto<Stock>  stockResult =  stockDao.findStockByUserID(userId);
        if(stockResult.getEntityList()!=null && stockResult.getEntityList().size() > 0){
            String msg = AppTools.appGetMessage("0051").replace("[stockIds]", String.join(", ", stockResult.getEntityList().stream().limit(3).map(stock->stock.getId()).toArray(String[]::new)) + (stockResult.getEntityList().size() > 3 ? "..." : ""));
            throw new AppException("0051",msg, "Y");
        }

        

        tokenRepository.deleteByUserId(opUser.get().getId());
        repository.deleteById(opUser.get().getId());

    }

    private Map uploadImage(MultipartFile file, String imageName) throws IOException{
        Cloudinary cloudinary = new Cloudinary(cloudinaryProperties.getUrl());
        System.out.println(cloudinary.config.cloudName);
        // Upload the image
        Map params1 = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", false,
            "overwrite", true,
            "quality", "auto",
            "public_id", imageName
        );

        return cloudinary.uploader().upload(file.getBytes(), params1);
    }
}
