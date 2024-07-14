package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.packageread.PackageAdCreateRes;
import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
import fpt.aptech.project4_server.service.PackageService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/package")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping("/create")
    public ResponseEntity<ResultDto<?>> createPackage(@RequestBody PackageAdCreateRes packRes) {
        return packageService.createPackage(packRes);
    }

    @PutMapping("/update/{packageId}")
    public ResponseEntity<ResultDto<?>> updatePackage(@PathVariable int packageId, @RequestBody PackageAdCreateRes packRes) {
        return packageService.updatePackage(packageId, packRes);
    }

    @DeleteMapping("/delete/{packageId}")
    public ResponseEntity<ResultDto<?>> deletePackage(@PathVariable int packageId) {
        return packageService.deletePackage(packageId);
    }

    @GetMapping("/view")
    public ResponseEntity<ResultDto<List<PackageShowbook>>> viewPackages() {
        return packageService.viewPackages();
    }
}
