package com.thanhhandsome.pythonmaster.service.thisinh;

import com.thanhhandsome.pythonmaster.dto.request.thisinh.CreateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.ThiSinhFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.UpdateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.thisinh.ThiSinhResponse;
import com.thanhhandsome.pythonmaster.entity.CuocThi;
import com.thanhhandsome.pythonmaster.entity.DangKy;
import com.thanhhandsome.pythonmaster.entity.ThanhToan;
import com.thanhhandsome.pythonmaster.entity.ThiSinh;
import com.thanhhandsome.pythonmaster.repository.CuocThiRepository;
import com.thanhhandsome.pythonmaster.repository.DangKyRepository;
import com.thanhhandsome.pythonmaster.repository.ThanhToanRepository;
import com.thanhhandsome.pythonmaster.repository.ThiSinhRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThiSinhServiceImpl implements ThiSinhService {

    private final ThiSinhRepository thiSinhRepository;
    private final DangKyRepository dangKyRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final CuocThiRepository cuocThiRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ThiSinhResponse> getThiSinhs(ThiSinhFilterRequest filter) {
        int page = filter.getPage() != null && filter.getPage() >= 0 ? filter.getPage() : 0;
        int size = filter.getSize() != null && filter.getSize() > 0 ? filter.getSize() : 10;
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<ThiSinh> spec = buildSpecification(filter);

        Page<ThiSinh> thiSinhPage = thiSinhRepository.findAll(spec, pageable);
        List<ThiSinhResponse> items = thiSinhPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.of(items, page, size, thiSinhPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public ThiSinhResponse getById(Long id) {
        ThiSinh thiSinh = thiSinhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thí sinh với ID: " + id));
        return toResponse(thiSinh);
    }

    @Override
    @Transactional
    public ThiSinhResponse create(CreateThiSinhRequest req) {
        // Kiểm tra trùng SĐT nếu có
        if (req.getSoDienThoai() != null && !req.getSoDienThoai().isBlank()) {
            boolean phoneExists = thiSinhRepository.findAll().stream()
                    .anyMatch(t -> req.getSoDienThoai().trim().equals(t.getSoDienThoai()));
            if (phoneExists) {
                throw new IllegalArgumentException("Số điện thoại '" + req.getSoDienThoai() + "' đã tồn tại trong hệ thống.");
            }
        }

        long count = thiSinhRepository.count();
        String idThiSinh = "TS_" + System.currentTimeMillis() + "_" + (count + 1);

        ThiSinh thiSinh = ThiSinh.builder()
                .idThiSinh(idThiSinh)
                .hoTen(req.getHoTen().trim())
                .soDienThoai(req.getSoDienThoai().trim())
                .email(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null)
                .cccd(req.getCccd() != null ? req.getCccd().trim() : null)
                .truongHoc(req.getTruongHoc() != null ? req.getTruongHoc().trim() : "Chưa xác định")
                .tinhThanh(req.getTinhThanh() != null ? req.getTinhThanh().trim() : "Chưa xác định")
                .diaChi(req.getDiaChi() != null ? req.getDiaChi().trim() : null)
                .ngaySinh(req.getNgaySinh())
                .trangThaiHoSo(req.getTrangThaiHoSo() != null ? req.getTrangThaiHoSo() : "created")
                .createdAt(LocalDateTime.now())
                .build();

        thiSinh = thiSinhRepository.save(thiSinh);

        // Tạo bản ghi Đăng ký mặc định với cuộc thi
        CuocThi cuocThi = cuocThiRepository.findByIdCuocThi("PYTHON_MASTER_2026")
                .orElseGet(() -> cuocThiRepository.save(CuocThi.builder()
                        .idCuocThi("PYTHON_MASTER_2026")
                        .tenCuocThi("Đấu trường Python Master 2026")
                        .ngayBatDau(LocalDateTime.now())
                        .ngayKetThuc(LocalDateTime.now().plusMonths(6))
                        .build()));

        DangKy dangKy = DangKy.builder()
                .thiSinh(thiSinh)
                .cuocThi(cuocThi)
                .trangThaiDangKy("approved")
                .ngayDangKy(LocalDateTime.now())
                .build();
        dangKy = dangKyRepository.save(dangKy);

        // Tự động xác định bảng dựa trên tuổi
        String bangDau = determineBangDauByAge(req.getNgaySinh());
        
        // Tạo thanh toán nếu có số tiền hoặc paymentStatus
        String pStatus = req.getPaymentStatus() != null ? req.getPaymentStatus().toLowerCase() : "pending";
        BigDecimal amount = req.getSoTien() != null ? req.getSoTien() : ("completed".equals(pStatus) ? BigDecimal.valueOf(500000) : BigDecimal.ZERO);

        ThanhToan thanhToan = ThanhToan.builder()
                .dangKy(dangKy)
                .bangDau(bangDau)
                .soTien(amount)
                .paymentStatus(pStatus)
                .idGiaoDich("TX_MANUAL_" + System.currentTimeMillis())
                .thoiGianGiaoDich(LocalDateTime.now())
                .build();
        thanhToanRepository.save(thanhToan);
        dangKy.setThanhToan(thanhToan);

        return toResponse(thiSinh);
    }

    @Override
    @Transactional
    public ThiSinhResponse update(Long id, UpdateThiSinhRequest req) {
        ThiSinh thiSinh = thiSinhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thí sinh với ID: " + id));

        thiSinh.setHoTen(req.getHoTen().trim());
        thiSinh.setEmail(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null);
        thiSinh.setSoDienThoai(req.getSoDienThoai().trim());
        if (req.getCccd() != null) thiSinh.setCccd(req.getCccd().trim());
        if (req.getTruongHoc() != null) thiSinh.setTruongHoc(req.getTruongHoc().trim());
        if (req.getTinhThanh() != null) thiSinh.setTinhThanh(req.getTinhThanh().trim());
        if (req.getDiaChi() != null) thiSinh.setDiaChi(req.getDiaChi().trim());
        if (req.getNgaySinh() != null) thiSinh.setNgaySinh(req.getNgaySinh());
        if (req.getTrangThaiHoSo() != null) thiSinh.setTrangThaiHoSo(req.getTrangThaiHoSo());

        thiSinh = thiSinhRepository.save(thiSinh);

        // Cập nhật thông tin đăng ký / thanh toán nếu có
        List<DangKy> dangKys = thiSinh.getDangKys();
        if (dangKys != null && !dangKys.isEmpty()) {
            DangKy dk = dangKys.get(0);
            ThanhToan tt = dk.getThanhToan();
            if (tt != null) {
                if (req.getBangDau() != null && !req.getBangDau().isBlank()) {
                    tt.setBangDau(req.getBangDau());
                }
                if (req.getPaymentStatus() != null && !req.getPaymentStatus().isBlank()) {
                    String newStatus = req.getPaymentStatus().toLowerCase();
                    tt.setPaymentStatus(newStatus);
                    
                    // Tự động gán soTien mặc định khi paymentStatus = "completed"
                    if ("completed".equals(newStatus) && (req.getSoTien() == null || req.getSoTien().compareTo(BigDecimal.ZERO) == 0)) {
                        tt.setSoTien(BigDecimal.valueOf(500000)); // Mặc định 500,000 VND
                    }
                }
                if (req.getSoTien() != null) {
                    tt.setSoTien(req.getSoTien());
                }
                thanhToanRepository.save(tt);
            }
        }

        return toResponse(thiSinh);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ThiSinh thiSinh = thiSinhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thí sinh với ID: " + id));

        // Xóa cascade thủ công DangKy & ThanhToan để đảm bảo tính toàn vẹn
        List<DangKy> dangKys = thiSinh.getDangKys();
        if (dangKys != null) {
            for (DangKy dk : dangKys) {
                if (dk.getThanhToan() != null) {
                    thanhToanRepository.delete(dk.getThanhToan());
                }
                dangKyRepository.delete(dk);
            }
        }

        thiSinhRepository.delete(thiSinh);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctSchools() {
        return thiSinhRepository.findDistinctTruongHoc();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportExcel(ThiSinhFilterRequest filter) {
        Specification<ThiSinh> spec = buildSpecification(filter);
        List<ThiSinh> list = thiSinhRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách thí sinh");

            // Style Header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            font.setFontHeightInPoints((short) 11);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Row tiêu đề
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);

            String[] columns = {
                    "STT", "Mã thí sinh", "Họ và tên", "Email", "Số điện thoại",
                    "CCCD", "Trường học", "Tỉnh thành", "Bảng đấu", "Số tiền (VNĐ)",
                    "Trạng thái", "Ngày đăng ký"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Cell Styles cho dữ liệu
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle numberStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("#,##0"));

            int rowIdx = 1;
            for (ThiSinh t : list) {
                ThiSinhResponse res = toResponse(t);
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx);
                row.createCell(1).setCellValue(res.getIdThiSinh() != null ? res.getIdThiSinh() : "");
                row.createCell(2).setCellValue(res.getHoTen() != null ? res.getHoTen() : "");
                row.createCell(3).setCellValue(res.getEmail() != null ? res.getEmail() : "");
                row.createCell(4).setCellValue(res.getSoDienThoai() != null ? res.getSoDienThoai() : "");
                row.createCell(5).setCellValue(res.getCccd() != null ? res.getCccd() : "");
                row.createCell(6).setCellValue(res.getTruongHoc() != null ? res.getTruongHoc() : "");
                row.createCell(7).setCellValue(res.getTinhThanh() != null ? res.getTinhThanh() : "");
                row.createCell(8).setCellValue(res.getBangDau() != null ? res.getBangDau() : "");

                Cell amountCell = row.createCell(9);
                if (res.getSoTien() != null) {
                    amountCell.setCellValue(res.getSoTien().doubleValue());
                    amountCell.setCellStyle(numberStyle);
                } else {
                    amountCell.setCellValue(0);
                }

                row.createCell(10).setCellValue(res.getTrangThai() != null ? res.getTrangThai() : "");

                Cell dateCell = row.createCell(11);
                if (res.getNgayDangKy() != null) {
                    dateCell.setCellValue(res.getNgayDangKy().format(DATE_TIME_FORMATTER));
                    dateCell.setCellStyle(dateStyle);
                } else {
                    dateCell.setCellValue("");
                }

                rowIdx++;
            }

            // Auto fit column widths
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1200, 3500));
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("Lỗi khi xuất file Excel danh sách thí sinh: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xuất file Excel: " + e.getMessage());
        }
    }

    // ── Helper methods ─────────────────────────────────────────

    private Specification<ThiSinh> buildSpecification(ThiSinhFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Search keyword (Họ tên, SĐT, Email, Trường học)
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String pattern = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("hoTen")), pattern);
                Predicate phoneLike = cb.like(cb.lower(root.get("soDienThoai")), pattern);
                Predicate emailLike = cb.like(cb.lower(root.get("email")), pattern);
                Predicate schoolLike = cb.like(cb.lower(root.get("truongHoc")), pattern);
                predicates.add(cb.or(nameLike, phoneLike, emailLike, schoolLike));
            }

            // 2. Lọc theo trường học
            if (filter.getTruongHoc() != null && !filter.getTruongHoc().isBlank()
                    && !"all".equalsIgnoreCase(filter.getTruongHoc())) {
                predicates.add(cb.equal(root.get("truongHoc"), filter.getTruongHoc().trim()));
            }

            // 3. Lọc theo trạng thái đóng phí / chờ hồ sơ
            if (filter.getTrangThai() != null && !filter.getTrangThai().isBlank()
                    && !"all".equalsIgnoreCase(filter.getTrangThai())) {

                Join<ThiSinh, DangKy> dangKyJoin = root.join("dangKys", JoinType.LEFT);
                Join<DangKy, ThanhToan> thanhToanJoin = dangKyJoin.join("thanhToan", JoinType.LEFT);

                String status = filter.getTrangThai().toUpperCase().trim();

                switch (status) {
                    case "DA_DONG_PHI" -> {
                        predicates.add(cb.equal(cb.lower(thanhToanJoin.get("paymentStatus")), "completed"));
                    }
                    case "CHO_HO_SO" -> {
                        Predicate notCompleted = cb.or(
                                cb.isNull(thanhToanJoin.get("paymentStatus")),
                                cb.notEqual(cb.lower(thanhToanJoin.get("paymentStatus")), "completed")
                        );
                        Predicate isPendingProfile = cb.or(
                                cb.equal(cb.lower(root.get("trangThaiHoSo")), "created"),
                                cb.equal(cb.lower(root.get("trangThaiHoSo")), "pending")
                        );
                        predicates.add(cb.and(notCompleted, isPendingProfile));
                    }
                    case "CHUA_DONG_PHI" -> {
                        predicates.add(cb.or(
                                cb.isNull(thanhToanJoin.get("paymentStatus")),
                                cb.notEqual(cb.lower(thanhToanJoin.get("paymentStatus")), "completed")
                        ));
                    }
                }
                // Tránh duplicate khi join OneToMany
                if (query != null) {
                    query.distinct(true);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private ThiSinhResponse toResponse(ThiSinh t) {
        DangKy dk = (t.getDangKys() != null && !t.getDangKys().isEmpty()) ? t.getDangKys().get(0) : null;
        ThanhToan tt = dk != null ? dk.getThanhToan() : null;

        String pStatus = tt != null ? tt.getPaymentStatus() : "pending";
        boolean isCompleted = pStatus != null && pStatus.equalsIgnoreCase("completed");

        String trangThai;
        String trangThaiKey;

        if (isCompleted) {
            trangThai = "Đã đóng phí";
            trangThaiKey = "DA_DONG_PHI";
        } else if ("created".equalsIgnoreCase(t.getTrangThaiHoSo()) || "pending".equalsIgnoreCase(t.getTrangThaiHoSo())) {
            trangThai = "Chờ hồ sơ";
            trangThaiKey = "CHO_HO_SO";
        } else {
            trangThai = "Chưa đóng phí";
            trangThaiKey = "CHUA_DONG_PHI";
        }

        return ThiSinhResponse.builder()
                .id(t.getId())
                .idThiSinh(t.getIdThiSinh())
                .hoTen(t.getHoTen())
                .email(t.getEmail())
                .soDienThoai(t.getSoDienThoai())
                .cccd(t.getCccd())
                .truongHoc(t.getTruongHoc())
                .tinhThanh(t.getTinhThanh())
                .diaChi(t.getDiaChi())
                .ngaySinh(t.getNgaySinh())
                .trangThaiHoSo(t.getTrangThaiHoSo())
                .trangThai(trangThai)
                .trangThaiKey(trangThaiKey)
                .bangDau(tt != null ? tt.getBangDau() : "Bảng A")
                .soTien(tt != null ? tt.getSoTien() : null)
                .paymentStatus(pStatus)
                .ngayDangKy(dk != null ? dk.getNgayDangKy() : t.getCreatedAt())
                .createdAt(t.getCreatedAt())
                .build();
    }

    /**
     * Xác định bảng dựa trên tuổi thí sinh
     * - <= 18 tuổi: Bảng A
     * - > 18 tuổi: Bảng B
     * - Không có ngày sinh: Bảng A (mặc định)
     */
    private String determineBangDauByAge(LocalDate ngaySinh) {
        if (ngaySinh == null) {
            return "Bảng A"; // Mặc định nếu không có ngày sinh
        }

        LocalDate today = LocalDate.now();
        int age = today.getYear() - ngaySinh.getYear();
        
        // Điều chỉnh nếu chưa đến sinh nhật trong năm nay
        if (today.getMonthValue() < ngaySinh.getMonthValue() || 
            (today.getMonthValue() == ngaySinh.getMonthValue() && today.getDayOfMonth() < ngaySinh.getDayOfMonth())) {
            age--;
        }

        return age <= 18 ? "Bảng A" : "Bảng B";
    }
}
