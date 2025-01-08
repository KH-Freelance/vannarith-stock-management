package com.hfsolution.feature.stockmanagement.service.schedule;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.external.telegram.TelegramRestClientConsumer;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.ScheduleDao;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.request.schedule.ScheduleRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.entity.Schedule;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.service.recovery.RecoveryService;
import com.hfsolution.feature.stockmanagement.service.report.ReportService;

import static com.hfsolution.app.constant.AppConstant.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImp implements ScheduleService{

    
    private final TelegramRestClientConsumer telegramRestClientConsumer;
    private final ScheduleDao scheduleDao;
    private final ReportService reportService;
    private final RecoveryService recoveryService;
    private final HttpServletRequest httpServletRequest;

    @Scheduled(fixedRate = 10000) 
    public void executeScheduledTasks() {
        
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            List<Schedule> schedules = scheduleDao.findActiveStatus().getEntityList();
        
            if(!schedules.isEmpty()){
                LocalDateTime currentDate = LocalDateTime.now();
                
                for (Schedule schedule : schedules) {
                    String functionType = schedule.getFunctionType();
                    boolean isRunning = isRunning(schedule,currentDate);
                    System.out.println("========"+isRunning+"\n");
                    if (isRunning) {

                        if ("BACKUP".equals(functionType)) {
                            
                        }else{
                            
                        }

                    }
                }
            }
        } catch (DatabaseException e) {
            throw e;
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
        
    }

    private boolean isRunning(Schedule schedule, LocalDateTime currentDate) {

        LocalDateTime scheduledDate = schedule.getScheduleDate().toLocalDateTime().withSecond(0).withNano(0);
        currentDate = currentDate.withSecond(0).withNano(0);
        currentDate = LocalDateTime.of(2025, 1, 13, 16, 30).withSecond(0).withNano(0);
        String type = schedule.getScheduledType();
    
        System.out.println("========" +currentDate+"===="+currentDate.getDayOfWeek());
        System.out.println("========" +scheduledDate +"===="+scheduledDate.getDayOfWeek());
        System.out.println("========" + type);
    
        if (currentDate.isBefore(scheduledDate)) {
            return false; 
        }
    
        switch (type) {
            case "DAILY":
                return currentDate.toLocalTime().equals(scheduledDate.toLocalTime());
    
            case "WEEKLY":
                return currentDate.getDayOfWeek() == scheduledDate.getDayOfWeek() &&
                currentDate.toLocalTime().equals(scheduledDate.toLocalTime());
    
            case "MONTHLY":
                return currentDate.getDayOfMonth() == scheduledDate.getDayOfMonth() &&
                currentDate.toLocalTime().equals(scheduledDate.toLocalTime());
    
            default:
                return false; 
        }
    
    }

    @Override
    public Object updateScheduleConfig(Long id, ScheduleRequest scheduleRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE SCHEDULE CONIG");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Schedule> scheduleResult = scheduleDao.findById(id);
            Schedule schedule = scheduleResult.getEntity();
            Optional.ofNullable(scheduleRequest.getScheduleType()).ifPresent(schedule::setScheduledType);
            Optional.ofNullable(scheduleRequest.getTelegramToken()).ifPresent(schedule::setTelegramToken);
            Optional.ofNullable(scheduleRequest.getChatId()).ifPresent(schedule::setChatId);
            Optional.ofNullable(scheduleRequest.isActive()).ifPresent(schedule::setActive);
            Optional.ofNullable(scheduleRequest.getScheduleDate())
            .map(factoryDate -> {
                LocalDateTime factoryDateTime = LocalDateTime.parse(factoryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return Timestamp.valueOf(factoryDateTime);
            })
            .ifPresent(schedule::setScheduleDate);
            scheduleDao.saveEntity(schedule);
            response.setStatus(SUCCESS);
            response.setCode("065");
            response.setMsg(AppTools.appGetMessage("065"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH SCHEDULE");
        SuccessResponse<Page<Schedule>> response = new SuccessResponse<>();
        try {

            Specification<Schedule> schedule = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Schedule> scheduleResult = scheduleDao.search(schedule,pageable);
            if(!scheduleResult.getStatus().equals(SUCCESS) || scheduleResult.getPage()==null){
                String msg = AppTools.appGetMessage("066");
                throw new AppException("066",msg);
            }

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(scheduleResult.getPage());
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
       
    } 


    
}
