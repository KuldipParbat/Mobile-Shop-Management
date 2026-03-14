package com.mobileshop.service.impl;

import com.mobileshop.dto.SupplierDTO;
import com.mobileshop.entity.Supplier;
import com.mobileshop.repository.SupplierRepository;
import com.mobileshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierDTO createSupplier(SupplierDTO dto) {

        Supplier supplier = mapToEntity(dto);

        supplier = supplierRepository.save(supplier);

        return mapToDTO(supplier);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierDTO getSupplierById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        return mapToDTO(supplier);
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO dto) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setGstNumber(dto.getGstNumber());

        supplier = supplierRepository.save(supplier);

        return mapToDTO(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {

        supplierRepository.deleteById(id);
    }

    private Supplier mapToEntity(SupplierDTO dto) {

        return Supplier.builder()
                .id(dto.getId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .gstNumber(dto.getGstNumber())
                .build();
    }

    private SupplierDTO mapToDTO(Supplier supplier) {

        return SupplierDTO.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .gstNumber(supplier.getGstNumber())
                .build();
    }
}