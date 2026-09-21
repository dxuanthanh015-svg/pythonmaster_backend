package com.thanhhandsome.pythonmaster.service.doitac;

import com.thanhhandsome.pythonmaster.entity.DoanhNghiep;
import com.thanhhandsome.pythonmaster.repository.DangKyWebRepository;
import com.thanhhandsome.pythonmaster.repository.DoanhNghiepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Đồng bộ bảng DOANHNGHIEP từ cột partner trong Excel / DANGKYWEB.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerSyncService {

    private static final String TEST_MST = "MST999888";
    private static final String[] EXCEL_PATHS = {
            "src/main/Data/Data_khach_hang_Cleaned.xlsx",
            "c:/Users/DELL/IdeaProjects/Project/pythonmaster/src/main/Data/Data_khach_hang_Cleaned.xlsx",
            "data/Data_khach_hang_Cleaned.xlsx"
    };

    private final DoanhNghiepRepository doanhNghiepRepository;
    private final DangKyWebRepository dangKyWebRepository;

    public int syncAll() {
        removeSyntheticTestRow();

        Set<String> names = new LinkedHashSet<>();
        File excel = findExcel();
        if (excel != null) {
            names.addAll(readPartnerNamesFromExcel(excel));
            log.info("Đọc {} đối tác từ Excel: {}", names.size(), excel.getAbsolutePath());
        }
        names.addAll(dangKyWebRepository.findDistinctPartners());

        int upserted = upsertPartners(names);
        log.info("Đã đồng bộ {} đối tác vào bảng DOANHNGHIEP (tổng hiện có: {}).",
                upserted, doanhNghiepRepository.count());
        return upserted;
    }

    public int upsertPartners(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return 0;
        }
        int created = 0;
        for (String raw : names) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String ten = raw.trim();
            if (doanhNghiepRepository.existsByTenDoanhNghiepIgnoreCase(ten)) {
                continue;
            }

            try {
                // Sửa logic: Gọi hàm uniqueMst(ten) đã viết sẵn để sinh mã số thuế hợp lệ
                String mst = uniqueMst(ten);

                doanhNghiepRepository.save(DoanhNghiep.builder()
                        .tenDoanhNghiep(ten)
                        .maSoThue(mst)
                        .nganhNghe("Đối tác")
                        .build());
                created++;
            } catch (Exception e) {
                log.error("Lỗi khi lưu doanh nghiệp '{}': {}", ten, e.getMessage());
            }
        }
        return created;
    }

    @Transactional
    public void removeSyntheticTestRow() {
        doanhNghiepRepository.findByMaSoThue(TEST_MST)
                .ifPresent(dn -> {
                    log.info("Xóa bản ghi đối tác test MST={}", TEST_MST);
                    doanhNghiepRepository.delete(dn);
                });
    }

    private File findExcel() {
        for (String path : EXCEL_PATHS) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private Set<String> readPartnerNamesFromExcel(File file) {
        Set<String> names = new LinkedHashSet<>();
        try (InputStream is = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheet("Data đăng ký pythonmaster.vn");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }
            int rowIndex = 0;
            for (Row row : sheet) {
                rowIndex++;
                if (rowIndex == 1) {
                    continue;
                }
                String partner = cellString(row.getCell(13));
                if (partner != null && !partner.isBlank()) {
                    names.add(partner.trim());
                }
            }
        } catch (Exception e) {
            log.error("Không đọc được cột partner từ Excel: {}", e.getMessage(), e);
        }
        return names;
    }

    private String uniqueMst(String ten) {
        String base = "DT" + Integer.toUnsignedString(ten.toLowerCase().hashCode());
        if (base.length() > 20) {
            base = base.substring(0, 20);
        }
        String mst = base;
        int i = 1;
        while (doanhNghiepRepository.existsByMaSoThue(mst)) {
            String suffix = String.valueOf(i++);
            mst = (base.length() + suffix.length() > 20)
                    ? base.substring(0, 20 - suffix.length()) + suffix
                    : base + suffix;
        }
        return mst;
    }

    private static String cellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> {
                String value = cell.getStringCellValue().trim();
                if (value.contains("?")) {
                    value = value.replace("Ð", "Đ")
                            .replace("ð", "đ")
                            .replace("?", "");
                }
                yield value;
            }
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
}