package com.thanhhandsome.pythonmaster.configuration;

import com.thanhhandsome.pythonmaster.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminService adminService;

    @Override
    public void run(String... args) {
        adminService.createDefaultAdminIfNeeded();
    }
}
