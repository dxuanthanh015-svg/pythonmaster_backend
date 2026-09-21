package com.thanhhandsome.pythonmaster.service.doitac;

import com.thanhhandsome.pythonmaster.dto.request.doitac.CreateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.DoiTacFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.UpdateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.doitac.DoiTacResponse;
import com.thanhhandsome.pythonmaster.entity.DoanhNghiep;
import com.thanhhandsome.pythonmaster.repository.DoanhNghiepRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoiTacServiceImpl implements DoiTacService {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "tenDoanhNghiep", "maSoThue", "nguoiDaiDien",
            "email", "soDienThoai", "diaChi", "nganhNghe", "createdAt", "updatedAt"
    );

    private final DoanhNghiepRepository doanhNghiepRepository;

    /* ── LIST ──────────────────────────────────────────────────── */

    @Override
    public PageResponse<DoiTacResponse> getDoiTacs(DoiTacFilterRequest filter) {
        int page   = filter.getPage()    != null && filter.getPage()    >= 0 ? filter.getPage()    : 0;
        int size   = filter.getSize()    != null && filter.getSize()    >  0 ? filter.getSize()    : 10;
        String sortBy  = resolveSortBy(filter.getSortBy());
        String sortDir = filter.getSortDir() != null ? filter.getSortDir() : "asc";

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DoanhNghiep> pageResult = doanhNghiepRepository.findAll(buildSpecification(filter), pageable);

        List<DoiTacResponse> items = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.of(items, page, size, pageResult.getTotalElements());
    }

    /* ── GET BY ID ─────────────────────────────────────────────── */

    @Override
    public DoiTacResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    /* ── CREATE ────────────────────────────────────────────────── */

    @Override
    @Transactional
    public DoiTacResponse create(CreateDoiTacRequest request) {
        if (doanhNghiepRepository.existsByMaSoThue(request.getMaSoThue())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mã số thuế \"" + request.getMaSoThue() + "\" đã tồn tại trong hệ thống.");
        }

        DoanhNghiep entity = DoanhNghiep.builder()
                .tenDoanhNghiep(request.getTenDoanhNghiep())
                .maSoThue(request.getMaSoThue())
                .nguoiDaiDien(request.getNguoiDaiDien())
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .diaChi(request.getDiaChi())
                .nganhNghe(request.getNganhNghe())
                .build();

        return toResponse(doanhNghiepRepository.save(entity));
    }

    /* ── UPDATE ────────────────────────────────────────────────── */

    @Override
    @Transactional
    public DoiTacResponse update(Long id, UpdateDoiTacRequest request) {
        DoanhNghiep entity = findOrThrow(id);

        if (request.getTenDoanhNghiep() != null) entity.setTenDoanhNghiep(request.getTenDoanhNghiep());
        if (request.getNguoiDaiDien()   != null) entity.setNguoiDaiDien(request.getNguoiDaiDien());
        if (request.getEmail()          != null) entity.setEmail(request.getEmail());
        if (request.getSoDienThoai()    != null) entity.setSoDienThoai(request.getSoDienThoai());
        if (request.getDiaChi()         != null) entity.setDiaChi(request.getDiaChi());
        if (request.getNganhNghe()      != null) entity.setNganhNghe(request.getNganhNghe());

        return toResponse(doanhNghiepRepository.save(entity));
    }

    /* ── DELETE ────────────────────────────────────────────────── */

    @Override
    @Transactional
    public void delete(Long id) {
        DoanhNghiep entity = findOrThrow(id);
        doanhNghiepRepository.delete(entity);
    }

    /* ── DROPDOWN ──────────────────────────────────────────────── */

    @Override
    public List<String> getDistinctPhanLoai() {
        return doanhNghiepRepository.findDistinctPhanLoai();
    }

    /* ── HELPERS ───────────────────────────────────────────────── */

    private DoanhNghiep findOrThrow(Long id) {
        return doanhNghiepRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy đối tác với ID = " + id));
    }

    private DoiTacResponse toResponse(DoanhNghiep d) {
        return DoiTacResponse.builder()
                .id(d.getId())
                .tenDoanhNghiep(d.getTenDoanhNghiep())
                .maSoThue(d.getMaSoThue())
                .nguoiDaiDien(d.getNguoiDaiDien())
                .email(d.getEmail())
                .soDienThoai(d.getSoDienThoai())
                .diaChi(d.getDiaChi())
                .phanLoai(d.getNganhNghe())
                .nhanVienPhuTrach(d.getNhanVien() != null ? d.getNhanVien().getHoTen() : null)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    private Specification<DoanhNghiep> buildSpecification(DoiTacFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String keyword = blankToNull(filter.getKeyword());
            if (keyword != null) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tenDoanhNghiep")), pattern),
                        cb.like(cb.lower(root.get("nguoiDaiDien")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("soDienThoai")), pattern),
                        cb.like(cb.lower(root.get("maSoThue")), pattern)
                ));
            }

            String phanLoai = blankToNull(filter.getPhanLoai());
            if (phanLoai != null) {
                predicates.add(cb.equal(cb.lower(root.get("nganhNghe")), phanLoai.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String resolveSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }
        if ("phanLoai".equals(sortBy)) {
            return "nganhNghe";
        }
        return SORTABLE_FIELDS.contains(sortBy) ? sortBy : "id";
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
