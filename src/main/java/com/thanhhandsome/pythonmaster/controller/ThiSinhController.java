package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.thisinh.CreateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.ThiSinhFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.UpdateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.thisinh.ThiSinhResponse;
import com.thanhhandsome.pythonmaster.service.thisinh.ThiSinhService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/thi-sinh")
@RequiredArgsConstructor
@Tag(name = "Thí sinh", description = "CRUD thí sinh và xuất Excel")
public class ThiSinhController {

    private final ThiSinhService thiSinhService;

    /**
     * Danh sách thí sinh kèm phân trang, tìm kiếm và lọc:
     * - keyword: tìm kiếm theo tên, sđt, email, trường học
     * - truongHoc: lọc theo trường học
     * - trangThai: DA_DONG_PHI, CHO_HO_SO, CHUA_DONG_PHI, ALL
     * - page, size, sortBy, sortDir
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ThiSinhResponse>>> getThiSinhs(
            @Valid @ParameterObject @ModelAttribute ThiSinhFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách thí sinh thành công", thiSinhService.getThiSinhs(filter))
        );
    }

    /**
     * Lấy danh sách các trường học duy nhất để đổ dữ liệu vào dropdown filter
     */
    @GetMapping("/schools")
    public ResponseEntity<ApiResponse<List<String>>> getDistinctSchools() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách trường học thành công", thiSinhService.getDistinctSchools())
        );
    }

    /**
     * Chi tiết thí sinh theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThiSinhResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin thí sinh thành công", thiSinhService.getById(id))
        );
    }

    /**
     * Thêm mới thí sinh
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ThiSinhResponse>> create(
            @Valid @RequestBody CreateThiSinhRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Tạo mới thí sinh thành công", thiSinhService.create(request))
        );
    }

    /**
     * Cập nhật thông tin thí sinh
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ThiSinhResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateThiSinhRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật thông tin thí sinh thành công", thiSinhService.update(id, request))
        );
    }

    /**
     * Xóa thí sinh
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        thiSinhService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa thí sinh thành công", null)
        );
    }

    /**
     * Xuất danh sách thí sinh ra file Excel (.xlsx) theo bộ lọc đang chọn
     */
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@Valid @ParameterObject @ModelAttribute ThiSinhFilterRequest filter) {
        byte[] bytes = thiSinhService.exportExcel(filter);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh-sach-thi-sinh.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
