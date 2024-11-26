package com.hfsolution.feature.stockmanagement.dto.csvrepresentation;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.user.entity.User;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PaymentCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private Long purchaseId;

    @CsvBindByPosition(position = 2)
    private BigDecimal amount;

    @CsvBindByPosition(position = 3)
    private Timestamp createdDate;

    @CsvBindByPosition(position = 4)
    private Timestamp updateDate;

}
