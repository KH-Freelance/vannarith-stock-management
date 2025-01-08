package com.hfsolution.feature.stockmanagement.service.schedule;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.hfsolution.feature.stockmanagement.dto.request.schedule.ScheduleRequest;


@Service
public interface ScheduleService {

    Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum);
    Object updateScheduleConfig(Long id , ScheduleRequest scheduleRequest);
    
}
