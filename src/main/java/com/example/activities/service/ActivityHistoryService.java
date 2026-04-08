package com.example.activities.service;

import com.example.activities.model.ActivityHistory;
import com.example.activities.repository.ActivityHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityHistoryService {

    private final ActivityHistoryRepository repository;

    public List<ActivityHistory> findAll() {
        return repository.findAll();
    }

    public ActivityHistory findById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("ActivityHistory not found"));
    }

    public ActivityHistory save(ActivityHistory entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}