package com.thanhhandsome.pythonmaster.configuration;

import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import com.thanhhandsome.pythonmaster.service.excel.ExcelDataImporterService;
import com.thanhhandsome.pythonmaster.service.doitac.PartnerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExcelSeederRunner implements CommandLineRunner {
    private final ExcelDataImporterService excelDataImporterService;
    private final PartnerSyncService partnerSyncService;
    private final ThiSinhRepository thiSinhRepository;
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String[] targetPaths = {
                "src/main/Data/Data_khach_hang_Cleaned.xlsx",
                "c:/Users/DELL/IdeaProjects/Project/pythonmaster/src/main/Data/Data_khach_hang_Cleaned.xlsx",
                "data/Data_khach_hang_Cleaned.xlsx"
        };

        File excel = null;
        for (String path : targetPaths) {
            File file = new File(path);
            if (file.exists()) {
                excel = file;
                break;
            }
        }

        if (excel != null) {
            log.info("Nạp lại dữ liệu từ file cleaned: {}", excel.getAbsolutePath());
            thanhToanRepository.deleteAllInBatch();
            dangKyRepository.deleteAllInBatch();
            thiSinhRepository.deleteAllInBatch();
            int imported = excelDataImporterService.importDataFromExcel(excel.getAbsolutePath());
            log.info("Hoàn tất nạp {} thí sinh từ Data_khach_hang_Cleaned.xlsx!", imported);
        } else {
            log.warn("Không tìm thấy Data_khach_hang_Cleaned.xlsx — bỏ qua seed Excel.");
        }

        partnerSyncService.syncAll();
    }
}
