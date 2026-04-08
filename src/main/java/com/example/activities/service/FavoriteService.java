package com.example.activities.service;

import com.example.activities.model.Favorite;
import com.example.activities.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository repository;

    public List<Favorite> findAll() {
        return repository.findAll();
    }

    public Favorite findById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found"));
    }

    public Favorite save(Favorite entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}