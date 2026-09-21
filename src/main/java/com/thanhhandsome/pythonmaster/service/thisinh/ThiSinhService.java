package com.thanhhandsome.pythonmaster.service.thisinh;

import com.thanhhandsome.pythonmaster.dto.request.thisinh.CreateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.ThiSinhFilterRequest;
import com.thanhhandsome.pythonmaster.dto.request.thisinh.UpdateThiSinhRequest;
import com.thanhhandsome.pythonmaster.dto.response.common.PageResponse;
import com.thanhhandsome.pythonmaster.dto.response.thisinh.ThiSinhResponse;

import java.util.List;

public interface ThiSinhService {
    PageResponse<ThiSinhResponse> getThiSinhs(ThiSinhFilterRequest filter);
    ThiSinhResponse getById(Long id);
    ThiSinhResponse create(CreateThiSinhRequest request);
    ThiSinhResponse update(Long id, UpdateThiSinhRequest request);
    void delete(Long id);
    List<String> getDistinctSchools();
    byte[] exportExcel(ThiSinhFilterRequest filter);
}
