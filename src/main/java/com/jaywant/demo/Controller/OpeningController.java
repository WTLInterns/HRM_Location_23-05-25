package com.jaywant.demo.Controller;

import com.jaywant.demo.Entity.Opening;
import com.jaywant.demo.Service.OpeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/openings/{subadminid}")
public class OpeningController {
    private final OpeningService openingService;

    @Autowired
    public OpeningController(OpeningService openingService) {
        this.openingService = openingService;
    }

    // Get all openings for a subadmin
    @GetMapping
    public List<Opening> getOpeningsBySubadmin(@PathVariable("subadminid") int subadminId) {
        return openingService.getOpeningsBySubadminId(subadminId);
    }

    // Create Opening for a subadmin
    @PostMapping
    public Opening createOpening(@PathVariable("subadminid") int subadminId, @RequestBody Opening opening) {
        return openingService.createOpeningForSubadmin(subadminId, opening);
    }

    // Get Opening by ID for a subadmin
    @GetMapping("/{id}")
    public Opening getOpeningById(@PathVariable("subadminid") int subadminId, @PathVariable("id") int id) {
        return openingService.getOpeningByIdForSubadmin(subadminId, id);
    }

    // Update Opening for a subadmin
    @PutMapping("/{id}")
    public Opening updateOpening(@PathVariable("subadminid") int subadminId, @PathVariable("id") int id, @RequestBody Opening opening) {
        return openingService.updateOpeningForSubadmin(subadminId, id, opening);
    }

    // Delete Opening for a subadmin
    @DeleteMapping("/{id}")
    public void deleteOpening(@PathVariable("subadminid") int subadminId, @PathVariable("id") int id) {
        openingService.deleteOpeningForSubadmin(subadminId, id);
    }
}

