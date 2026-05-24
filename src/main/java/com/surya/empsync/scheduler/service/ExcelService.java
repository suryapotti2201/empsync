package com.surya.empsync.scheduler.service;

import com.surya.empsync.model.Employee;
import com.surya.empsync.payload.AttendanceResponse;
import com.surya.empsync.service.AttendanceService;
import com.surya.empsync.service.EmployeeService;
import com.surya.empsync.service.PreSalaryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.LongStream;

@Service
public class ExcelService {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PreSalaryService preSalaryService;

    public byte[] generateSalaryAndAttendanceExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet attendanceSheet = workbook.createSheet("Attendance Report");
        Sheet salarySheet = workbook.createSheet("Salary Report");

        CellStyle headerStyle = createHeaderCellStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dataAlternateStyle = createDataAlternateStyle(workbook);

        Map<Long, AttendanceResponse> attendanceResponseMap = attendanceService.getAllEmployeesAttendanceMap();

        Map<Long, Employee> employeeNamesMap = employeeService.getAllEmployees();
        Map<Long, Long> preSalaryAmountMap = preSalaryService.getPreSalary();

        Row attendanceHeaderRow = attendanceSheet.createRow(0);
        attendanceHeaderRow.setHeightInPoints(25);
        setCellStyle(workbook, attendanceHeaderRow, 0, "Employee Name", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 1, "Total Number of working days", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 2, "Present days", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 3, "Leaves taken", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 4, "Half days taken", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 5, "Number of leaves permitted per month", headerStyle, false, false);
        setCellStyle(workbook, attendanceHeaderRow, 6, "Total Number of leaves taken this month", headerStyle, false, false);

        Row salaryHeaderRow = salarySheet.createRow(0);
        salaryHeaderRow.setHeightInPoints(25);
        setCellStyle(workbook, salaryHeaderRow, 0, "Employee Name", headerStyle, false, false);
        setCellStyle(workbook, salaryHeaderRow, 1, "Salary", headerStyle, false, false);
        setCellStyle(workbook, salaryHeaderRow, 2, "Loss of Pay", headerStyle, false, false);
        setCellStyle(workbook, salaryHeaderRow, 3, "Pre Salary", headerStyle, false, false);
        setCellStyle(workbook, salaryHeaderRow, 4, "Net Salary", headerStyle, false, false);

        int rowNum = 1;
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        Long noOfWorkingDays = YearMonth.now().minusMonths(1).lengthOfMonth() -
                lastMonth.atDay(1).datesUntil(lastMonth.plusMonths(1).atDay(1))
                        .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY).count();
        for(Map.Entry<Long, Employee>  employeeEntry : employeeNamesMap.entrySet()) {
            CellStyle cellStyle;
            if(rowNum % 2 == 0) {
                cellStyle = dataAlternateStyle;
            }else {
                cellStyle = dataStyle;
            }
            Row attendanceRow = attendanceSheet.createRow(rowNum);
            setCellStyle(workbook, attendanceRow, 0, employeeEntry.getValue().getName(), cellStyle, true, true);
            long actualNoOfWorkingDays = 0L;
            LocalDate joinedDate = employeeEntry.getValue().getDateOfJoining();
            if(YearMonth.now().minusMonths(1).isAfter(YearMonth.from(joinedDate))){
                setCellStyle(workbook, attendanceRow, 1, noOfWorkingDays.toString(), cellStyle, true, false);
            }else{
                actualNoOfWorkingDays = LongStream.rangeClosed(0, YearMonth.from(joinedDate).atEndOfMonth().toEpochDay()
                                - joinedDate.toEpochDay()).mapToObj(joinedDate::plusDays)
                        .filter(date -> date.getDayOfWeek() != DayOfWeek.SUNDAY).count();
                setCellStyle(workbook, attendanceRow, 1, String.valueOf(actualNoOfWorkingDays), cellStyle, true, false);
            }
            AttendanceResponse attendanceResponse = attendanceResponseMap.getOrDefault(employeeEntry.getKey(), new AttendanceResponse());
            setCellStyle(workbook, attendanceRow, 2, String.valueOf(attendanceResponse.getNoOfPresentDays()), cellStyle, true, false);
            setCellStyle(workbook, attendanceRow, 3, String.valueOf(attendanceResponse.getNoOfAbsentDays()), cellStyle, true, false);
            setCellStyle(workbook, attendanceRow, 4, String.valueOf(attendanceResponse.getNoOfHalfDays()), cellStyle, true, false);
            setCellStyle(workbook, attendanceRow, 5, "2", cellStyle, true, false);
            long totalLeaves = attendanceResponse.getNoOfAbsentDays() + (attendanceResponse.getNoOfHalfDays() / 2) - 2;
            setCellStyle(workbook, attendanceRow, 6, String.valueOf(totalLeaves > 0 ? totalLeaves : 0), cellStyle, true, false);

            Row salaryRow = salarySheet.createRow(rowNum);
            setCellStyle(workbook, salaryRow, 0, employeeEntry.getValue().getName(), cellStyle, true, true);
            long salary;
            if(YearMonth.now().minusMonths(1).isAfter(YearMonth.from(joinedDate))){
                salary = employeeEntry.getValue().getSalary();
            }else {
                salary = (employeeEntry.getValue().getSalary()/noOfWorkingDays) * actualNoOfWorkingDays;
            }
            setCellStyle(workbook, salaryRow, 1, String.valueOf(salary), cellStyle, true, false);
            Long preSalary = preSalaryAmountMap.getOrDefault(employeeEntry.getKey(), 0L);
            long LOP = (salary /noOfWorkingDays) * (totalLeaves > 0 ? totalLeaves : 0);
            setCellStyle(workbook, salaryRow, 2, String.valueOf(LOP), cellStyle, true, false);
            setCellStyle(workbook, salaryRow, 3, String.valueOf(preSalary), cellStyle, true, false);
            long netSalary = salary - preSalary - ((employeeEntry.getValue().getSalary()/noOfWorkingDays) *
                    (totalLeaves > 0 ? totalLeaves : 0));
            setCellStyle(workbook, salaryRow, 4, String.valueOf(netSalary), cellStyle, true, false);
            rowNum++;
        }

        for (int i = 0; i < 7; i++) {
            attendanceSheet.autoSizeColumn(i);
        }

        for (int i = 0; i < 5; i++) {
            salarySheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        return byteArrayOutputStream.toByteArray();
    }

    private CellStyle createDataAlternateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFColor cellColor = new XSSFColor(new Color(242, 242, 242), null);
        ((XSSFCellStyle) style).setFillForegroundColor(cellColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFColor cellColor = new XSSFColor(new Color(31, 78, 121), null);
        ((XSSFCellStyle) style).setFillForegroundColor(cellColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private void setCellStyle(Workbook workbook, Row row, int col, String value, CellStyle baseStyle, boolean isData, boolean isFirstCell) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(baseStyle);
        if(isData && !isFirstCell) {
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
        }else if(isData) {
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
        }
        if(value.length() > 25){
            cellStyle.setWrapText(true);
            row.setHeightInPoints(35);
        }else {
            cellStyle.setWrapText(false);
        }
        cell.setCellStyle(cellStyle);
    }

    private void setBorders(CellStyle cellStyle){
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        XSSFColor borderColor = new XSSFColor(new Color(217, 217, 217), null);
        ((XSSFCellStyle) cellStyle).setTopBorderColor(borderColor);
        ((XSSFCellStyle) cellStyle).setBottomBorderColor(borderColor);
        ((XSSFCellStyle) cellStyle).setLeftBorderColor(borderColor);
        ((XSSFCellStyle) cellStyle).setRightBorderColor(borderColor);
    }
}