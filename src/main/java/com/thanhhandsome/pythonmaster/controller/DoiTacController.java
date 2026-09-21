package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.request.doitac.CreateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.DoiTacFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.UpdateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.doitac.DoiTacResponse;
import com.thanhhandsome.pythonmaster.service.doitac.DoiTacService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doi-tac")
@RequiredArgsConstructor
@Tag(name = "Đối tác", description = "CRUD đối tác / doanh nghiệp")
public class DoiTacController {

    private final DoiTacService doiTacService;

    /**
     * GET /api/v1/doi-tac
     * Danh sách đối tác kèm phân trang + tìm kiếm + lọc theo phân loại.
     * Params: keyword, phanLoai, page, size, sortBy, sortDir
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DoiTacResponse>>> getDoiTacs(
            @Valid @ModelAttribute DoiTacFilterRequest filter) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách đối tác thành công", doiTacService.getDoiTacs(filter))
        );
    }

    /**
     * GET /api/v1/doi-tac/phan-loai
     * Lấy danh sách phân loại duy nhất để đổ dropdown filter.
     */
    @GetMapping("/phan-loai")
    public ResponseEntity<ApiResponse<List<String>>> getPhanLoai() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách phân loại thành công", doiTacService.getDistinctPhanLoai())
        );
    }

    /**
     * GET /api/v1/doi-tac/{id}
     * Chi tiết đối tác theo ID.
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<DoiTacResponse>> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin đối tác thành công", doiTacService.getById(id))
        );
    }

    /**
     * POST /api/v1/doi-tac
     * Thêm mới đối tác.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DoiTacResponse>> create(
            @Valid @RequestBody CreateDoiTacRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Thêm đối tác thành công", doiTacService.create(request))
        );
    }

    /**
     * PUT /api/v1/doi-tac/{id}
     * Cập nhật thông tin đối tác.
     */
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<DoiTacResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoiTacRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật đối tác thành công", doiTacService.update(id, request))
        );
    }

    /**
     * DELETE /api/v1/doi-tac/{id}
     * Xóa đối tác.
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        doiTacService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Xóa đối tác thành công", null)
        );
    }
}
