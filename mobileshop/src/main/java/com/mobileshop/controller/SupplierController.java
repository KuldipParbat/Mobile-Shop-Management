package com.mobileshop.controller;

import com.mobileshop.dto.SupplierDTO;
import com.mobileshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierDTO createSupplier(@RequestBody SupplierDTO dto) {
        return supplierService.createSupplier(dto);
    }

    @GetMapping
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public SupplierDTO getSupplier(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    @PutMapping("/{id}")
    public SupplierDTO updateSupplier(@PathVariable Long id,
                                      @RequestBody SupplierDTO dto) {
        return supplierService.updateSupplier(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }
}