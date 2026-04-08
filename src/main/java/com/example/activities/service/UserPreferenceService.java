package com.example.activities.service;

import com.example.activities.model.UserPreference;
import com.example.activities.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository repository;

    public List<UserPreference> findAll() {
        return repository.findAll();
    }

    public UserPreference findById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("UserPreference not found"));
    }

    public UserPreference save(UserPreference entity) {
        return repository.save(entity);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}