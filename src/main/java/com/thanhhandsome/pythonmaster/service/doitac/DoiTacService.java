package com.thanhhandsome.pythonmaster.service.doitac;

import com.thanhhandsome.pythonmaster.dto.request.doitac.CreateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.DoiTacFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.doitac.UpdateDoiTacRequest;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.doitac.DoiTacResponse;

import java.util.List;

public interface DoiTacService {
    PageResponse<DoiTacResponse> getDoiTacs(DoiTacFilterRequest filter);
    DoiTacResponse getById(Long id);
    DoiTacResponse create(CreateDoiTacRequest request);
    DoiTacResponse update(Long id, UpdateDoiTacRequest request);
    void delete(Long id);
    List<String> getDistinctPhanLoai();
}
