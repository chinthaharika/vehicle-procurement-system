package com.smartvehicle.service;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.model.TransferHistory;
import com.smartvehicle.repository.VehicleRepository;
import com.smartvehicle.repository.TransferHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TransferHistoryRepository transferHistoryRepository;

    public Vehicle addVehicle(Vehicle vehicle, User seller) {
        vehicle.setSeller(seller);
        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        vehicle.setCreatedAt(LocalDateTime.now());

        // Generate initial block hash (Genesis)
        String genesisHash = calculateHash(vehicle);
        vehicle.setBlockHash(genesisHash);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Save Genesis History
        TransferHistory history = new TransferHistory();
        history.setVehicle(savedVehicle);
        history.setFromUser(null); // Genesis, no previous owner
        history.setToUser(seller);
        history.setTransferTime(LocalDateTime.now());
        history.setPreviousHash("0000000000000000000000000000000000000000000000000000000000000000"); // Genesis parent
        history.setTransactionHash(genesisHash);
        history.setPrice(vehicle.getPrice()); // Initial Price
        transferHistoryRepository.save(history);

        return savedVehicle;
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }

    public List<Vehicle> getMySoldVehicles(User seller) {
        return vehicleRepository.findBySeller(seller);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public void purchaseVehicle(Long vehicleId, User buyer) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available for purchase");
        }

        vehicle.setBuyer(buyer);
        vehicle.setStatus(Vehicle.Status.SOLD);

        // Blockchain: Update hash to link transaction
        String transactionData = vehicle.getVehicleNumber() + buyer.getLoginId() + LocalDateTime.now().toString();
        String previousHash = vehicle.getBlockHash();
        String newHash = calculateHash(transactionData, previousHash);
        vehicle.setBlockHash(newHash);

        // Save Transfer History
        TransferHistory history = new TransferHistory();
        history.setVehicle(vehicle);
        history.setFromUser(vehicle.getSeller()); // More accurate to use current seller
        history.setToUser(buyer);
        history.setTransferTime(LocalDateTime.now());
        history.setPreviousHash(previousHash);
        history.setTransactionHash(newHash); // Current Transaction Hash
        history.setPrice(vehicle.getPrice()); // Purchase Price
        transferHistoryRepository.save(history);

        vehicleRepository.save(vehicle);
    }

    public void resellVehicle(Long vehicleId, User currentOwner, java.math.BigDecimal newPrice,
            String description,
            org.springframework.web.multipart.MultipartFile image,
            org.springframework.web.multipart.MultipartFile document) throws java.io.IOException {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Verify ownership
        if (vehicle.getBuyer() == null || !vehicle.getBuyer().getId().equals(currentOwner.getId())) {
            throw new RuntimeException("You are not the owner of this vehicle");
        }

        // Logic to flip ownership for resale
        // 1. Current Buyer becomes the new Seller
        vehicle.setSeller(currentOwner);
        // 2. Clear the Buyer field (it's now on the market)
        vehicle.setBuyer(null);

        // 3. Update Details
        vehicle.setPrice(newPrice);

        if (description != null && !description.trim().isEmpty()) {
            vehicle.setAccidentsHistory(description);
        }

        if (image != null && !image.isEmpty()) {
            vehicle.setImage(image.getBytes());
            vehicle.setImageContentType(image.getContentType());
        }

        if (document != null && !document.isEmpty()) {
            vehicle.setDocument(document.getBytes());
            vehicle.setDocumentContentType(document.getContentType());
            vehicle.setDocumentFilename(document.getOriginalFilename());
        }

        // 4. Set Status to AVAILABLE
        vehicle.setStatus(Vehicle.Status.AVAILABLE);

        vehicleRepository.save(vehicle);
    }

    public List<TransferHistory> getVehicleHistory(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null)
            return List.of();
        return transferHistoryRepository.findByVehicleOrderByTransferTimeDesc(vehicle);
    }

    // "Blockchain" Simulation: SHA-256 Hashing
    private String calculateHash(Vehicle v) {
        String data = v.getVehicleNumber() + v.getSeller().getLoginId() + v.getPrice() + v.getCreatedAt();
        return applySha256(data);
    }

    private String calculateHash(String newData, String previousHash) {
        return applySha256(newData + previousHash);
    }

    private String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
