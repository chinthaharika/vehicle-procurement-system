package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import com.smartvehicle.model.TransferHistory;

@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    private final String UPLOAD_DIR = "uploads/";

    // --- Seller Operations ---

    @GetMapping("/add")
    public String addVehiclePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.SELLER)
            return "redirect:/login";

        model.addAttribute("vehicle", new Vehicle());
        return "add_vehicle";
    }

    @PostMapping("/add")
    public String addVehicle(@RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(value = "price", required = false) String priceStr,
            @RequestParam(value = "accidentsHistory", required = false) String accidentsHistory,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "document", required = false) MultipartFile document,
            HttpSession session,
            Model model) {
        // DEBUGGING: Print received parameters
        // Processing Add Vehicle Request

        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.SELLER)
            return "redirect:/login";

        // Manual Validation
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            model.addAttribute("error", "Vehicle Number is required.");
            model.addAttribute("vehicle", new Vehicle());
            return "add_vehicle";
        }

        if (priceStr == null || priceStr.trim().isEmpty()) {
            model.addAttribute("error", "Price is required.");
            model.addAttribute("vehicle", new Vehicle());
            return "add_vehicle";
        }

        if (image == null || image.isEmpty()) {
            model.addAttribute("error", "Vehicle image is required.");
            // Re-populate form to avoid losing data (basic implementation)
            Vehicle v = new Vehicle();
            v.setVehicleNumber(vehicleNumber);
            v.setAccidentsHistory(accidentsHistory);
            model.addAttribute("vehicle", v);
            return "add_vehicle";
        }

        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleNumber(vehicleNumber);

            // Manual parsing to prevent 400 error
            try {
                vehicle.setPrice(new java.math.BigDecimal(priceStr));
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Invalid Price: Please enter a valid number.");
                model.addAttribute("vehicle", new Vehicle());
                return "add_vehicle";
            }

            vehicle.setAccidentsHistory(accidentsHistory);
            vehicle.setStatus(Vehicle.Status.AVAILABLE);

            // Save Image
            vehicle.setImage(image.getBytes());
            vehicle.setImageContentType(image.getContentType());

            // Save Document
            if (document != null && !document.isEmpty()) {
                vehicle.setDocument(document.getBytes());
                vehicle.setDocumentContentType(document.getContentType());
                vehicle.setDocumentFilename(document.getOriginalFilename());
            }

            vehicleService.addVehicle(vehicle, user);
            return "redirect:/seller/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error saving vehicle: " + e.getMessage());
            model.addAttribute("vehicle", new Vehicle());
            return "add_vehicle";
        }
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> getVehicleImage(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null && vehicle.getImage() != null) {
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(vehicle.getImageContentType()))
                    .body(vehicle.getImage());
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @GetMapping("/document/{id}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> getVehicleDocument(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null && vehicle.getDocument() != null) {
            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + vehicle.getDocumentFilename() + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(vehicle.getDocumentContentType()))
                    .body(vehicle.getDocument());
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    // --- Buyer Operations ---

    @GetMapping("/history/{id}")
    public String viewHistory(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null)
            return "redirect:/";

        model.addAttribute("vehicle", vehicle);
        
        java.util.List<TransferHistory> historyList = vehicleService.getVehicleHistory(id);
        model.addAttribute("historyList", historyList);
        
        // Create a chronological copy (Oldest -> Newest) for Horizontal Flow
        java.util.List<TransferHistory> chronologicalList = new java.util.ArrayList<>(historyList);
        java.util.Collections.reverse(chronologicalList);
        model.addAttribute("chronologicalList", chronologicalList);
        
        return "vehicle_history";
    }

    @PostMapping("/buy/{id}")
    public String buyVehicle(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.BUYER)
            return "redirect:/login";

        vehicleService.purchaseVehicle(id, user);
        return "redirect:/buyer/dashboard";
    }

    @GetMapping("/resell/{id}")
    public String resellPage(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.BUYER)
            return "redirect:/login";

        Vehicle vehicle = vehicleService.getVehicleById(id);
        // Verify owner
        if (vehicle == null || vehicle.getBuyer() == null || !vehicle.getBuyer().getId().equals(user.getId())) {
            return "redirect:/buyer/dashboard";
        }

        model.addAttribute("vehicle", vehicle);
        return "resell_vehicle";
    }

    @PostMapping("/resell/{id}")
    public String resellVehicle(@PathVariable Long id,
            @RequestParam java.math.BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile image,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile document,
            HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.BUYER)
            return "redirect:/login";

        try {
            vehicleService.resellVehicle(id, user, price, description, image, document);
        } catch (Exception e) {
            e.printStackTrace(); // Handle error gracefully in real app
        }
        return "redirect:/buyer/dashboard";
    }
}
