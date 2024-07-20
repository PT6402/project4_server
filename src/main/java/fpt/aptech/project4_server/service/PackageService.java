package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.packageread.PackageAdCreateRes;
import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.repository.PackageReadRepository;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageReadRepository packageReadRepository;

//    public ResponseEntity<ResultDto<?>> createPackage(PackageAdCreateRes packRes) {
//        PackageRead newPackage = new PackageRead();
//        newPackage.setPackageName(packRes.getPackageName());
//        newPackage.setDayQuantity(packRes.getDayQuantity());
//
//        packageReadRepository.save(newPackage);
//
//        ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    public ResponseEntity<ResultDto<?>> createPackage(PackageAdCreateRes packRes) {
        // Check if a package with the same name and day quantity already exists
        Optional<PackageRead> existingPackageByName = packageReadRepository.findByPackageName(packRes.getPackageName());
        Optional<PackageRead> existingPackageByDayQuantity = packageReadRepository.findByDayQuantity(packRes.getDayQuantity());

        if (existingPackageByName.isPresent() || existingPackageByDayQuantity.isPresent()) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Package with the same name and day quantity already exists")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // Create a new package
        PackageRead newPackage = new PackageRead();
        newPackage.setPackageName(packRes.getPackageName());
        newPackage.setDayQuantity(packRes.getDayQuantity());

        packageReadRepository.save(newPackage);

        ResultDto<?> response = ResultDto.builder()
                .status(true)
                .message("Create successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<ResultDto<?>> updatePackage(int packageId, PackageAdCreateRes packRes) {
        Optional<PackageRead> packageOptional = packageReadRepository.findById(packageId);
        if (packageOptional.isEmpty()) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Package not found")
                    .build(), HttpStatus.NOT_FOUND);
        }

        PackageRead existingPackage = packageOptional.get();
        existingPackage.setPackageName(packRes.getPackageName());
        existingPackage.setDayQuantity(packRes.getDayQuantity());

        packageReadRepository.save(existingPackage);
        return new ResponseEntity<>(ResultDto.builder()
                .status(true)
                .message("Package updated successfully")
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ResultDto<?>> deletePackage(int packageId) {
        try {
            packageReadRepository.deleteById(packageId);
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Package deleted successfully")
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to delete package: " + e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<List<PackageShowbook>>> viewPackages() {
        try {
            List<PackageShowbook> packages = packageReadRepository.findAll().stream()
                    .map(packageRead -> PackageShowbook.builder()
                            .id(packageRead.getId())
                            .packageName(packageRead.getPackageName())
                            .dayQuantity(packageRead.getDayQuantity())
                            .rentPrice(calculateRentPrice(packageRead))
                            .build())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(ResultDto.<List<PackageShowbook>>builder()
                    .status(true)
                    .message("Packages retrieved successfully")
                    .model(packages)
                    .build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.<List<PackageShowbook>>builder()
                    .status(false)
                    .message("Failed to retrieve packages: " + e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private double calculateRentPrice(PackageRead packageRead) {
        // Calculate rent price based on package details, implement your logic here
        double basePrice = 10.0; // Example base price
        return basePrice * packageRead.getDayQuantity();
    }
}
