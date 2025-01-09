package com.hfsolution.feature.stockmanagement.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Schedule;

public interface ScheduleRepository extends IBaseRepository<Schedule,Long>, JpaSpecificationExecutor<Schedule>{

    List<Schedule> findAllByActiveTrueAndExecuteFalse();

} 
