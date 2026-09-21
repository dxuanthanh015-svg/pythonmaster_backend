package com.thanhhandsome.pythonmaster.service.excel;

import com.thanhhandsome.pythonmaster.entity.CuocThi;
import com.thanhhandsome.pythonmaster.entity.DangKy;
import com.thanhhandsome.pythonmaster.entity.ThanhToan;
import com.thanhhandsome.pythonmaster.entity.ThiSinh;
import com.thanhhandsome.pythonmaster.repository.CuocThiRepository;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import com.thanhhandsome.pythonmaster.service.doitac.PartnerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelDataImporterService {
    private final ThiSinhRepository thiSinhRepository;
    private final CuocThiRepository cuocThiRepository;
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final PartnerSyncService partnerSyncService;

    @Transactional
    public int importDataFromExcel(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("File Excel không tồn tại tại đường dẫn: {}", filePath);
            return 0;
        }

        try (InputStream is = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheet("Data đăng ký pythonmaster.vn");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            CuocThi cuocThi = cuocThiRepository.findByIdCuocThi("PYTHON_MASTER_2026")
                    .orElseGet(() -> cuocThiRepository.save(CuocThi.builder()
                            .idCuocThi("PYTHON_MASTER_2026")
                            .tenCuocThi("Đấu trường Python Master 2026")
                            .moTa("Cuộc thi lập trình Python dành cho học sinh, sinh viên")
                            .ngayBatDau(LocalDateTime.of(2026, 6, 1, 0, 0))
                            .ngayKetThuc(LocalDateTime.of(2026, 12, 31, 23, 59))
                            .build()));

            Map<String, ThiSinh> existingByPhone = new HashMap<>();
            for (ThiSinh t : thiSinhRepository.findAll()) {
                if (t.getSoDienThoai() != null) existingByPhone.put(t.getSoDienThoai().trim(), t);
                if (t.getEmail() != null) existingByPhone.put(t.getEmail().trim().toLowerCase(), t);
            }

            int importedCount = 0;
            int rowIndex = 0;

            List<ThiSinh> newThiSinhList = new ArrayList<>();
            List<DangKy> newDangKyList = new ArrayList<>();
            List<ThanhToan> newThanhToanList = new ArrayList<>();
            Set<String> partnerNames = new LinkedHashSet<>();

            // Ngày mặc định ban đầu là ngày của dòng đầu tiên trong file (21/07/2026)
            LocalDate lastSeenDate = LocalDate.of(2026, 7, 21);

            for (Row row : sheet) {
                rowIndex++;
                if (rowIndex == 1) continue; // Bỏ qua tiêu đề

                // Cột 0: date (Ngày khách đăng ký)
                LocalDate rowDate = parseDateFromCell(row.getCell(0));
                if (rowDate != null) {
                    lastSeenDate = rowDate;
                }

                // Cột 1: time (Giờ khách đăng ký)
                LocalTime rowTime = parseTimeFromCell(row.getCell(1));
                if (rowTime == null) {
                    rowTime = LocalTime.of(8, (rowIndex * 7) % 60);
                }

                LocalDateTime registrationDateTime = LocalDateTime.of(lastSeenDate, rowTime);

                String bangDau = getCellValueAsString(row.getCell(2)); // pm-bang
                String hoTen = getCellValueAsString(row.getCell(3));  // pm-hoten
                String sdt = getCellValueAsString(row.getCell(4));    // pm-sdt
                String email = getCellValueAsString(row.getCell(5));  // pm-email
                String cccd = getCellValueAsString(row.getCell(6));   // pm-cccd
                String tinhThanh = getCellValueAsString(row.getCell(7)); // pm-tinhthanh
                String truongHoc = getCellValueAsString(row.getCell(8)); // pm-truong
                String ngaySinhStr = getCellValueAsString(row.getCell(9)); // pm-ngaysinh
                BigDecimal soTien = getCellValueAsBigDecimal(row.getCell(10)); // pm-sotien
                String paymentStatus = getCellValueAsString(row.getCell(11)); // payment_status
                String transactionId = getCellValueAsString(row.getCell(12)); // sepay_transaction_id
                String partner = getCellValueAsString(row.getCell(13)); // partner
                String ghiChu = getCellValueAsString(row.getCell(14)); // ghi chu

                if (partner != null && !partner.isBlank()) {
                    partnerNames.add(partner.trim());
                }

                if ((sdt == null || sdt.isBlank()) && (email == null || email.isBlank())) {
                    continue;
                }

                String finalSdt = (sdt != null && !sdt.isBlank()) ? sdt.trim() : "0900" + String.format("%05d", rowIndex);
                String finalEmail = (email != null && !email.isBlank()) ? email.trim().toLowerCase() : "thisinh" + rowIndex + "@gmail.com";
                String finalHoTen = (hoTen != null && !hoTen.isBlank()) ? hoTen.trim() : "Thí Sinh " + rowIndex;

                String finalTruongHoc = (truongHoc != null && !truongHoc.isBlank())
                        ? truongHoc
                        : ((partner != null && !partner.isBlank()) ? partner : "Trường học khác");

                LocalDate ngaySinh = parseDate(ngaySinhStr);

                // Nếu thí sinh đã tồn tại trong DB, cập nhật lại ngày đăng ký chuẩn từ Excel
                ThiSinh existing = existingByPhone.get(finalSdt);
                if (existing == null) {
                    existing = existingByPhone.get(finalEmail);
                }

                if (existing != null) {
                    existing.setCreatedAt(registrationDateTime);
                    existing.setHoTen(finalHoTen);
                    existing.setTinhThanh(tinhThanh != null ? tinhThanh : existing.getTinhThanh());
                    existing.setTruongHoc(finalTruongHoc);
                    if (ngaySinh != null) existing.setNgaySinh(ngaySinh);

                    // Cập nhật các DangKy của thí sinh
                    for (DangKy dk : existing.getDangKys()) {
                        dk.setNgayDangKy(registrationDateTime);
                        if (dk.getThanhToan() != null) {
                            dk.getThanhToan().setThoiGianGiaoDich(registrationDateTime);
                            if (bangDau != null && !bangDau.isBlank()) {
                                dk.getThanhToan().setBangDau(bangDau);
                            }
                        }
                    }
                    importedCount++;
                    continue;
                }

                ThiSinh thiSinh = ThiSinh.builder()
                        .idThiSinh("TS_" + System.currentTimeMillis() + "_" + rowIndex)
                        .hoTen(finalHoTen)
                        .soDienThoai(finalSdt)
                        .email(finalEmail)
                        .cccd(cccd)
                        .tinhThanh(tinhThanh != null ? tinhThanh : "Chưa xác định")
                        .truongHoc(finalTruongHoc)
                        .ngaySinh(ngaySinh)
                        .trangThaiHoSo("created")
                        .createdAt(registrationDateTime)
                        .build();
                newThiSinhList.add(thiSinh);
                existingByPhone.put(finalSdt, thiSinh);
                existingByPhone.put(finalEmail, thiSinh);

                DangKy dangKy = DangKy.builder()
                        .thiSinh(thiSinh)
                        .cuocThi(cuocThi)
                        .trangThaiDangKy("approved")
                        .ngayDangKy(registrationDateTime)
                        .build();
                newDangKyList.add(dangKy);

                // Kiểm tra trạng thái thanh toán
                String rawStatus = paymentStatus != null ? paymentStatus.toLowerCase().trim() : "";
                boolean isCompleted = rawStatus.contains("completed") || rawStatus.contains("paid");
                String finalPaymentStatus = isCompleted ? "completed" : "pending";

                if (soTien != null && soTien.compareTo(BigDecimal.ZERO) > 0) {
                    ThanhToan thanhToan = ThanhToan.builder()
                            .dangKy(dangKy)
                            .bangDau(bangDau != null && !bangDau.isBlank() ? bangDau : "Bảng A")
                            .soTien(soTien)
                            .paymentStatus(finalPaymentStatus)
                            .idGiaoDich(transactionId != null && !transactionId.isBlank() ? transactionId : "TX_" + rowIndex)
                            .thoiGianGiaoDich(registrationDateTime)
                            .ghiChu(ghiChu)
                            .build();
                    dangKy.setThanhToan(thanhToan);
                    newThanhToanList.add(thanhToan);
                }

                importedCount++;
            }

            if (!newThiSinhList.isEmpty()) {
                thiSinhRepository.saveAll(newThiSinhList);
                dangKyRepository.saveAll(newDangKyList);
                thanhToanRepository.saveAll(newThanhToanList);
            }

            partnerSyncService.upsertPartners(partnerNames);

            log.info("Đã đồng bộ thành công {} bản ghi từ file Excel vào Database với ngày đăng ký chuẩn từ cột date/time!", importedCount);
            return importedCount;

        } catch (Exception e) {
            log.error("Lỗi khi đọc file Excel: {}", e.getMessage(), e);
            return 0;
        }
    }

    private LocalDate parseDateFromCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
                double num = cell.getNumericCellValue();
                if (num >= 30000 && num <= 60000) {
                    return DateUtil.getLocalDateTime(num).toLocalDate();
                }
            }
            if (cell.getCellType() == CellType.STRING) {
                return parseDate(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private LocalTime parseTimeFromCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalTime();
                }
                double num = cell.getNumericCellValue();
                if (num >= 0.0 && num < 1.0) {
                    int totalSeconds = (int) Math.round(num * 86400.0);
                    int hours = (totalSeconds / 3600) % 24;
                    int minutes = (totalSeconds % 3600) / 60;
                    int seconds = totalSeconds % 60;
                    return LocalTime.of(hours, minutes, seconds);
                } else if (num >= 30000 && num <= 60000) {
                    return DateUtil.getLocalDateTime(num).toLocalTime();
                }
            }
            if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.length() == 5) str += ":00";
                return LocalTime.parse(str);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return new BigDecimal(cell.getStringCellValue().trim());
            } catch (Exception ignored) {}
        }
        return BigDecimal.ZERO;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            if (dateStr.contains("/")) {
                String[] parts = dateStr.split("/");
                if (parts.length == 3) {
                    int day = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    int year = Integer.parseInt(parts[2].trim());
                    return LocalDate.of(year, month, day);
                }
            }
            if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr.substring(0, 10));
            }
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
