package com.ticketing.controller;

import com.ticketing.model.Category;
import com.ticketing.model.Department;
import com.ticketing.model.Role;
import com.ticketing.model.User;
import com.ticketing.repository.RoleRepository;
import com.ticketing.service.CategoryService;
import com.ticketing.service.DepartmentService;
import com.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final CategoryService categoryService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("usersCount", userService.getAllUsers().size());
        model.addAttribute("departmentsCount", departmentService.getAllDepartments().size());
        model.addAttribute("categoriesCount", categoryService.getAllCategories().size());
        return "admin/dashboard";
    }

    // User management
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/users/view";
    }

    @PostMapping("/users/{id}/role")
    public String toggleRole(@PathVariable Long id, 
                            @RequestParam Integer roleId, 
                            @RequestParam boolean assign,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            
            if (assign) {
                userService.assignRole(id, role.getName());
                redirectAttributes.addFlashAttribute("success", 
                        "Role " + role.getName() + " assigned to " + user.getUsername());
            } else {
                userService.removeRole(id, role.getName());
                redirectAttributes.addFlashAttribute("success", 
                        "Role " + role.getName() + " removed from " + user.getUsername());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating roles: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    // Department management
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("newDepartment", new Department());
        return "admin/departments/list";
    }

    @PostMapping("/departments")
    public String createDepartment(@ModelAttribute Department department, 
                                 RedirectAttributes redirectAttributes) {
        try {
            departmentService.createDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Department created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating department: " + e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/{id}/edit")
    public String editDepartmentForm(@PathVariable Integer id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "admin/departments/edit";
    }

    @PostMapping("/departments/{id}")
    public String updateDepartment(@PathVariable Integer id, 
                                @ModelAttribute Department department,
                                RedirectAttributes redirectAttributes) {
        try {
            departmentService.updateDepartment(id, department);
            redirectAttributes.addFlashAttribute("success", "Department updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating department: " + e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    // Category management
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new Category());
        return "admin/categories/list";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute Category category, 
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Integer id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "admin/categories/edit";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Integer id, 
                               @ModelAttribute Category category,
                               RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
