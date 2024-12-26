package com.hfsolution.app.util;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.poi.ss.usermodel.Sheet;

public class CustomColumnWidthHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();

        // Get the maximum number of columns in the sheet
        int maxColumns = sheet.getRow(0).getLastCellNum();

        // Set width for all columns dynamically
        for (int colIndex = 0; colIndex < maxColumns; colIndex++) {
            sheet.setColumnWidth(colIndex, 20 * 256); // Adjust the multiplier for desired width
        }

        // Optional: Set default row height
        sheet.setDefaultRowHeightInPoints(20); // 20 points for all rows
    }
}
