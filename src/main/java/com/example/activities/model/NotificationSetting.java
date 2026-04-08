package com.example.activities.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "notificationSettings")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // Add additional fields dynamically later if needed.
    
    // Minimal standard fields:
    private String name;
}