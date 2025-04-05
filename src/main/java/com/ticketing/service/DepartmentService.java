package com.ticketing.service;

import com.ticketing.model.Department;
import com.ticketing.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            throw new RuntimeException("Department name already exists");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Integer id, Department departmentDetails) {
        Department department = getDepartmentById(id);
        
        // Check if the new name already exists and is not the current department
        if (!department.getName().equals(departmentDetails.getName()) && 
            departmentRepository.findByName(departmentDetails.getName()).isPresent()) {
            throw new RuntimeException("Department name already exists");
        }
        
        department.setName(departmentDetails.getName());
        department.setDescription(departmentDetails.getDescription());
        
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
