package com.university.restaurant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [M1] /api/v1/menu/** (public), /api/v1/admin/menu/** (Admin CRUD), PATCH /api/v1/staff/menu/{id}/stock (Staff/Admin)
 */
@RestController
@RequestMapping("/api/v1")
public class MenuController {
}