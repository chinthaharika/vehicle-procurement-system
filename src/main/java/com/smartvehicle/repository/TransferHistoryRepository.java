package com.smartvehicle.repository;

import com.smartvehicle.model.TransferHistory;
import com.smartvehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    List<TransferHistory> findByVehicleOrderByTransferTimeDesc(Vehicle vehicle);

    List<TransferHistory> findByVehicleOrderByTransferTimeAsc(Vehicle vehicle);

    // Count total sales (all transfers where fromUser is not null, excluding genesis)
    long countByFromUserNotNull();

    // Get all transactions for admin view
    List<TransferHistory> findAllByOrderByTransferTimeDesc();
}
