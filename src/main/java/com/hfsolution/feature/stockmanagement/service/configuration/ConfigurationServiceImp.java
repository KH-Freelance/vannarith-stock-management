package com.hfsolution.feature.stockmanagement.service.configuration;

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
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.external.telegram.ChatInfoResponse;
import com.hfsolution.app.external.telegram.TelegramRestClientConsumer;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppLog;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.ConfigurationDao;
import com.hfsolution.feature.stockmanagement.dto.configuration.ConfigurationDto;
import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseDetailDto;
import com.hfsolution.feature.stockmanagement.dto.request.configuration.ConfigurationRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.entity.Configuration;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.service.recovery.RecoveryService;
import com.hfsolution.feature.stockmanagement.service.report.ReportService;

import static com.hfsolution.app.constant.AppConstant.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
@Service
@RequiredArgsConstructor
public class ConfigurationServiceImp implements ConfigurationService, SchedulingConfigurer {

    
    private final TelegramRestClientConsumer telegramRestClientConsumer;
    private final ConfigurationDao scheduleDao;
    private final ReportService reportService;
    private final RecoveryService recoveryService;
    private final HttpServletRequest httpServletRequest;
    private final TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final List<String> scheduleFunction = Arrays.asList("BACKUP","REPORT");

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
        
        List<Configuration> configurations = scheduleDao.findFunctionWithActive(scheduleFunction).getEntityList();
        if(!configurations.isEmpty()){
            for (Configuration configuration : configurations) {
                scheduleTask(configuration);   
            } 
        }
        
    }

    //init schedule
    private void scheduleTask(Configuration schedule) {
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


    private void executeTask(Configuration configuration) {
        String action = "";
        try {
            if ("BACKUP".equals(configuration.getFunctionType())) {
                action = "backup";
                String[] nameDetails = generateFileInfoDetails(configuration);
                String fileName = nameDetails[0];
                String caption = AppTools.appGetMessage("067").replace("[obj]",nameDetails[1]);
                recoveryService.backup(fileName);
                ResponseEntity<Resource> backupResponse = recoveryService.getExcelData(fileName);
                telegramRestClientConsumer.sendFileToTelegram(backupResponse.getBody().getFile(),configuration.getTelegramToken(),configuration.getChatId(),caption);
                
            } else {
                action = "report";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                String startDateTime = calculateStartDate(configuration).format(formatter); 
                String endDateTime = generateScheduledDateTime(configuration).format(formatter);
                String[] nameDetails = generateFileInfoDetails(configuration);
                String fileName = nameDetails[0];
                String caption = AppTools.appGetMessage("067").replace("[obj]",nameDetails[1]);
                File reportSaleFile = reportService.reportSaleFile(startDateTime, endDateTime, fileName);
                telegramRestClientConsumer.sendFileToTelegram(reportSaleFile,configuration.getTelegramToken(),configuration.getChatId(),caption);

            }
            configuration.setLastExecutedDate(new Timestamp(System.currentTimeMillis()));
            scheduleDao.saveEntity(configuration);
            var appLog = new AppLog<>();
            appLog.setAction("Schedueler");
            appLog.setInfo(configuration.toJson());
            appLog.setStep("executeTask-"+action);
            appLog.writeToLog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //for report
    private LocalDateTime calculateStartDate(Configuration configuration) {

        LocalDateTime scheduleDateTime = generateScheduledDateTime(configuration);
        String scheduleType = configuration.getScheduleType().toUpperCase();
    
        switch (scheduleType) {
            case "DAILY":
                return scheduleDateTime.toLocalDate().atStartOfDay();
            case "WEEKLY":
                return scheduleDateTime.toLocalDate()
                        .with(TemporalAdjusters.previous(AppTools.convertToDayOfWeek(configuration.getDayOfWeek())))
                        .atStartOfDay();
            case "MONTHLY":
                return scheduleDateTime.toLocalDate().minusMonths(1)
                        .withDayOfMonth(Math.toIntExact(configuration.getDayOfMonth())).atStartOfDay();
    
            default:
                throw new IllegalArgumentException("Unsupported schedule type: " + scheduleType);
        }

    }
    
    
    private String generateCronExpression(Configuration schedule) {

        LocalTime localTime = LocalTime.parse(schedule.getTime());  
        int hour = localTime.getHour();
        int minute = localTime.getMinute();
        switch (schedule.getScheduleType().toUpperCase()) {
            case "DAILY":
                return String.format("0 %d %d * * ?", minute, hour); 
            case "WEEKLY":
                return String.format("0 %d %d ? * %s", minute, hour, schedule.getDayOfWeek().toUpperCase());
            case "MONTHLY":
                return String.format("0 %d %d %s * ?", minute, hour, schedule.getDayOfMonth()); 
            default:
                throw new IllegalArgumentException("Invalid schedule type: " + schedule.getScheduleType());
        }
    }


    @Override
    public Object updateConfiguration(Long id, ConfigurationRequest configRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE CONFIGURATION");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        SuccessResponse<Configuration> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Configuration> configurationResult = scheduleDao.findById(id);
            if(!configurationResult.getStatus().equals(SUCCESS) || configurationResult.getEntity()==null){
                String msg = AppTools.appGetMessage("066");
                throw new AppException("066",msg);
            }
            Configuration configuration = configurationResult.getEntity();
            Optional.ofNullable(configRequest.getTelegramToken()).ifPresent(configuration::setTelegramToken);
            Optional.ofNullable(configRequest.getChatId()).ifPresent(configuration::setChatId);
            configuration.setActive(configRequest.isActive());

            if(scheduleFunction.stream().anyMatch(data->data.equals(configuration.getFunctionType()))){

                Optional.ofNullable(configRequest.getScheduleType()).ifPresent(configuration::setScheduleType);
                Optional.ofNullable(configRequest.getTime()).ifPresent(configuration::setTime);
                if(configRequest.getTime()==null || configRequest.getTime().isEmpty() || configRequest.getTime().isBlank()){
                    String msg = AppTools.appGetMessage("072");
                    throw new AppException("072",msg);
                }
                if(configuration.getScheduleType().equals("WEEKLY")){
                    if(configRequest.getDayOfWeek()==null || configRequest.getDayOfWeek().isEmpty() || configRequest.getDayOfWeek().isBlank()){
                        String msg = AppTools.appGetMessage("070");
                        throw new AppException("070",msg);
                    }
                    configuration.setDayOfWeek(configRequest.getDayOfWeek());
                    configuration.setDayOfMonth(0l);
                }else if(configuration.getScheduleType().equals("MONTHLY")){
                    if(configRequest.getDayOfMonth()==null ||configRequest.getDayOfMonth()==0){
                        String msg = AppTools.appGetMessage("071");
                        throw new AppException("071",msg);
                    }
                    configuration.setDayOfMonth(configRequest.getDayOfMonth());
                    configuration.setDayOfWeek("N/A");
                }else {
                    configuration.setDayOfWeek("N/A");
                    configuration.setDayOfMonth(0l);
                }
                scheduleDao.saveEntity(configuration);
                cancelTask(configuration.getId());
                scheduleTask(configuration);

            }else{
                scheduleDao.saveEntity(configuration);
            }
            
            response.setStatus(SUCCESS);
            response.setCode("065");
            response.setMsg(AppTools.appGetMessage("065").replace("[schedule]",configuration.getFunctionType()));
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

        httpServletRequest.setAttribute(ACTION,"SEARCH CONFIGURATION");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {

            Specification<Configuration> schedule = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Configuration> scheduleResult = scheduleDao.search(schedule,pageable);
            if(!scheduleResult.getStatus().equals(SUCCESS) || scheduleResult.getPage()==null){
                String msg = AppTools.appGetMessage("066");
                throw new AppException("066",msg);
            }
            
            Page<ConfigurationDto> schedulePaegeDto = scheduleResult.getPage().map(data->{
                String title = "undefined";
                ChatInfoResponse chatInfoResponse = telegramRestClientConsumer.getChatInfo(data.getTelegramToken(),data.getChatId());
                if(chatInfoResponse.getResult()!=null && chatInfoResponse.getResult().getTitle()!=null){
                    title = chatInfoResponse.getResult().getTitle();
                }
                ConfigurationDto scheduleDto = new ConfigurationDto();
                BeanUtils.copyProperties(data, scheduleDto);
                scheduleDto.setChannel(title);
                return scheduleDto;
            });
            
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(schedulePaegeDto);
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
       
    } 

    public LocalDateTime generateScheduledDateTime(Configuration configuration) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = configuration.getTime();
        String scheduleType = configuration.getScheduleType();
        String dayOfWeek = configuration.getDayOfWeek();
        Long dayOfMonth = configuration.getDayOfMonth();

        LocalTime parsedTime = LocalTime.parse(time, timeFormatter);
        LocalDate currentDate = LocalDate.now();
        LocalDate resolvedDate;
        if ("DAILY".equalsIgnoreCase(scheduleType)) {
            resolvedDate = currentDate;
        } else if ("WEEKLY".equalsIgnoreCase(scheduleType)) {
            DayOfWeek desiredDayOfWeek = AppTools.convertToDayOfWeek(dayOfWeek);
            resolvedDate = currentDate.with(TemporalAdjusters.nextOrSame(desiredDayOfWeek));
        } else if ("MONTHLY".equalsIgnoreCase(scheduleType)) {
            resolvedDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), Math.toIntExact(dayOfMonth));
        } else {
            throw new IllegalArgumentException("Unsupported schedule type: " + scheduleType);
        }

        return LocalDateTime.of(resolvedDate, parsedTime);
    }


    private String[] generateFileInfoDetails(Configuration configuration) {
        String functionType = configuration.getFunctionType().toLowerCase();
        String type = configuration.getScheduleType();
        LocalDateTime scheduleDateTime = generateScheduledDateTime(configuration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] details = new String[2];
    
        switch (type.toUpperCase()) {
            case "DAILY":
                details[0] = functionType + "_daily_" + scheduleDateTime.format(formatter);
                details[1] = functionType + " for Today";
                break;
    
            case "WEEKLY":
                LocalDate startOfWeek = scheduleDateTime.toLocalDate()
                .with(TemporalAdjusters.previous(AppTools.convertToDayOfWeek(configuration.getDayOfWeek())));
                LocalDate endOfWeek = scheduleDateTime.toLocalDate();
                details[0] = functionType + "_weekly_start_" + startOfWeek.format(formatter) +
                             "_end_" + endOfWeek.format(formatter);
                details[1] = functionType + " for the week from " + startOfWeek.format(formatter) +
                             " to " + endOfWeek.format(formatter);
                break;
    
            case "MONTHLY":
                LocalDate startOfMonth = scheduleDateTime.toLocalDate()
                .minusMonths(1).withDayOfMonth(Math.toIntExact(configuration.getDayOfMonth()));
                LocalDate endOfMonth = scheduleDateTime.toLocalDate();
                details[0] = functionType + "_monthly_start_" + startOfMonth.format(formatter) +
                             "_end_" + endOfMonth.format(formatter);
                details[1] = functionType + " for the month from " + startOfMonth.format(formatter) +
                             " to " + endOfMonth.format(formatter);
                break;
    
        }
        return details;
    }

    
    


    
    


    

    


    
}