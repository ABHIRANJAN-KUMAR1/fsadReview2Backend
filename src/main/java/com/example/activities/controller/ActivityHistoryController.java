package com.example.activities.controller;

import com.example.activities.model.ActivityHistory;
import com.example.activities.service.ActivityHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-historys")
@RequiredArgsConstructor
public class ActivityHistoryController {

    private final ActivityHistoryService service;

    @GetMapping
    public List<ActivityHistory> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityHistory> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ActivityHistory> create(@RequestBody ActivityHistory entity) {
        return ResponseEntity.ok(service.save(entity));
    }

@PutMapping("/{id}")
    public ResponseEntity<ActivityHistory> update(@PathVariable String id, @RequestBody ActivityHistory entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}