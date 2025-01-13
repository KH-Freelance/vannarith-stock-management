package com.hfsolution.feature.stockmanagement.service.schedule;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
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

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
@Service
@RequiredArgsConstructor
public class ScheduleServiceImp implements ScheduleService, SchedulingConfigurer {

    
    private final TelegramRestClientConsumer telegramRestClientConsumer;
    private final ScheduleDao scheduleDao;
    private final ReportService reportService;
    private final RecoveryService recoveryService;
    private final HttpServletRequest httpServletRequest;


    private final TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void initTaskScheduler() {
        if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ThreadPoolTaskScheduler scheduler = (ThreadPoolTaskScheduler) taskScheduler;
            scheduler.setPoolSize(1); 
            scheduler.setThreadNamePrefix("TaskScheduler-");
            scheduler.initialize();
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);

        List<Schedule> schedules = scheduleDao.findActive().getEntityList();
        for (Schedule schedule : schedules) {
            scheduleTask(schedule);
        }
    }

    //init schedule
    private void scheduleTask(Schedule schedule) {
        String cronExpression = generateCronExpression(schedule);
        ScheduledFuture<?> future = taskScheduler.schedule(
            () -> executeTask(schedule),
            
            new CronTrigger(cronExpression)
        );
        scheduledTasks.put(schedule.getId(), future);
    }

    //cancel schedule
    private void cancelTask(Long scheduleId) {
        ScheduledFuture<?> future = scheduledTasks.remove(scheduleId);
        if (future != null) {
            future.cancel(false);
        }
    }


    private void executeTask(Schedule schedule) {
        try {
            if ("BACKUP".equals(schedule.getFunctionType())) {

                String[] nameDetails = generateFileDetails(schedule);
                String fileName = nameDetails[0];
                String caption = AppTools.appGetMessage("067").replace("[obj]",nameDetails[1]);
                recoveryService.backup(fileName);
                ResponseEntity<Resource> backupResponse = recoveryService.getExcelData(fileName);
                telegramRestClientConsumer.sendFileToTelegram(backupResponse.getBody().getFile(),schedule.getTelegramToken(),schedule.getChatId(),caption);
            
            } else {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                String startDateTime = calculateStartDate(schedule).withHour(0).withMinute(0).withSecond(0).withNano(0).format(formatter); // 00:00
                String endDateTime = schedule.getScheduleDate().toLocalDateTime().withHour(23).withMinute(59).withSecond(59).withNano(0).format(formatter); // 23:59
                String[] nameDetails = generateFileDetails(schedule);
                String fileName = nameDetails[0];
                String caption = AppTools.appGetMessage("067").replace("[obj]",nameDetails[1]);
                File reportSaleFile = reportService.reportSaleFile(startDateTime, endDateTime, fileName);
                telegramRestClientConsumer.sendFileToTelegram(reportSaleFile,schedule.getTelegramToken(),schedule.getChatId(),caption);

            }
            LocalDateTime scheduleDateTime = schedule.getScheduleDate().toLocalDateTime();
            switch (schedule.getScheduleType()) {
                case "DAILY":
                    schedule.setScheduleDate(Timestamp.valueOf(scheduleDateTime.plusDays(1)));
                    break;
                case "WEEKLY":
                    schedule.setScheduleDate(Timestamp.valueOf(scheduleDateTime.plusWeeks(1)));
                    break;
                case "MONTHLY":
                    schedule.setScheduleDate(Timestamp.valueOf(scheduleDateTime.plusMonths(1)));
                    break;
            }
            schedule.setLastExecuteDate(new Timestamp(System.currentTimeMillis()));
            scheduleDao.saveEntity(schedule);

        } catch (Exception e) {
            System.err.println("Error executing task: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private String generateCronExpression(Schedule schedule) {
        switch (schedule.getScheduleType().toUpperCase()) {
            case "DAILY":
                return String.format("0 %d %d * * ?", schedule.getScheduleDate().toLocalDateTime().getMinute(),
                        schedule.getScheduleDate().toLocalDateTime().getHour());
            case "WEEKLY":
                return String.format("0 %d %d ? * %s", schedule.getScheduleDate().toLocalDateTime().getMinute(),
                        schedule.getScheduleDate().toLocalDateTime().getHour(),
                        schedule.getScheduleDate().toLocalDateTime().getDayOfWeek().name().substring(0, 3).toUpperCase());
            case "MONTHLY":
                return String.format("0 %d %d %d * ?", schedule.getScheduleDate().toLocalDateTime().getMinute(),
                        schedule.getScheduleDate().toLocalDateTime().getHour(),
                        schedule.getScheduleDate().toLocalDateTime().getDayOfMonth());
            default:
                throw new IllegalArgumentException("Invalid schedule type: " + schedule.getScheduleType());
        }
    }


    @Override
    public Object updateScheduleConfig(Long id, ScheduleRequest scheduleRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE SCHEDULE CONIG");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        SuccessResponse<Schedule> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Schedule> scheduleResult = scheduleDao.findById(id);
            if(!scheduleResult.getStatus().equals(SUCCESS) || scheduleResult.getEntity()==null){
                String msg = AppTools.appGetMessage("066");
                throw new AppException("066",msg);
            }
            Schedule schedule = scheduleResult.getEntity();
            Optional.ofNullable(scheduleRequest.getScheduleType()).ifPresent(schedule::setScheduleType);
            Optional.ofNullable(scheduleRequest.getTelegramToken()).ifPresent(schedule::setTelegramToken);
            Optional.ofNullable(scheduleRequest.getChatId()).ifPresent(schedule::setChatId);
            schedule.setActive(scheduleRequest.isActive());
            Optional.ofNullable(scheduleRequest.getScheduleDate())
            .map(factoryDate -> {
                LocalDateTime factoryDateTime = LocalDateTime.parse(factoryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return Timestamp.valueOf(factoryDateTime);
            })
            .ifPresent(schedule::setScheduleDate);
            scheduleDao.saveEntity(schedule);
            
            //refresh schedule configuration
            cancelTask(schedule.getId());
            if (scheduleRequest.isActive()) {
                scheduleTask(schedule);
            }
            response.setStatus(SUCCESS);
            response.setCode("065");
            response.setMsg(AppTools.appGetMessage("065").replace("[schedule]",schedule.getFunctionType()));
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

    //generate file name
    private String[] generateFileDetails(Schedule schedule) {
        String functionType = schedule.getFunctionType().toLowerCase();
        String type = schedule.getScheduleType();
        LocalDateTime scheduleDateTime =  schedule.getScheduleDate().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] details = new String[2]; 
    
        switch (type.toUpperCase()) {
            case "DAILY":
                details[0] = functionType+"_daily_" + scheduleDateTime.format(formatter) ;
                details[1] = functionType+" for date " + scheduleDateTime.format(formatter);
                break;
    
            case "WEEKLY":
                LocalDate startOfWeek = scheduleDateTime.minusDays(7).toLocalDate();
                LocalDate endOfWeek = scheduleDateTime.minusDays(1).toLocalDate();
                details[0] = functionType+"_weekly_start_" + startOfWeek.format(formatter) +
                             "_end_" + endOfWeek.format(formatter) ;
                details[1] = functionType+" for the week from " + startOfWeek.format(formatter) +
                             " to " + endOfWeek.format(formatter);
                break;
    
            case "MONTHLY":
                LocalDate startOfMonth = scheduleDateTime.minusMonths(1).toLocalDate().withDayOfMonth(1);
                LocalDate endOfMonth = scheduleDateTime.minusMonths(1).toLocalDate()
                                        .withDayOfMonth(scheduleDateTime.minusMonths(1).toLocalDate().lengthOfMonth());
                details[0] = functionType+"_monthly_start_" + startOfMonth.format(formatter) +
                             "_end_" + endOfMonth.format(formatter);
                details[1] = functionType+" for the month from " + startOfMonth.format(formatter) +
                             " to " + endOfMonth.format(formatter);
                break;
    
            default:
                throw new IllegalArgumentException("Invalid schedule type: " + type);
        }
        return details;
    }

    //calculate for weekly and monthly
    private LocalDateTime calculateStartDate(Schedule schedule) {
        String type = schedule.getScheduleType();
        LocalDateTime scheduleDateTime =  schedule.getScheduleDate().toLocalDateTime() ;
        
        switch (type) {
    
            case "WEEKLY":
                return scheduleDateTime.minusDays(7);
    
            case "MONTHLY":
                return scheduleDateTime.minusMonths(1);
    
        }
        return scheduleDateTime;
    }

    
    


    

    


    
}
