package com.example.activities.controller;

import com.example.activities.model.Favorite;
import com.example.activities.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService service;

    @GetMapping
    public List<Favorite> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Favorite> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Favorite> create(@RequestBody Favorite entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Favorite> update(@PathVariable String id, @RequestBody Favorite entity) {
        entity.setId(id);
        return ResponseEntity.ok(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}