package com.hfsolution.app.util;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.enums.CellDataTypeEnum;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimestampConverter implements Converter<Timestamp> {

    @Override
    public Class<?> supportJavaTypeKey() {
        // The type of Java object this converter supports
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        // The Excel cell type to convert to
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Timestamp convertToJavaData(ReadConverterContext<?> context) throws Exception {
        // Convert from Excel cell data to Java object (Timestamp)
        String cellStr = context.getReadCellData().getStringValue();
        if (StringUtils.isEmpty(cellStr)) {
            return null;
        }
        // Parse the string back into a Timestamp (using a specific date format)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new Timestamp(sdf.parse(cellStr).getTime());
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Timestamp> context) throws Exception {
        // Convert from Java object (Timestamp) to Excel cell data (String)
        Timestamp value = context.getValue();
        if (value == null) {
            return new WriteCellData<>("");
        }
        // Format the Timestamp to a string format for Excel (e.g., "yyyy-MM-dd HH:mm:ss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new WriteCellData<>(sdf.format(value));
    }
}
