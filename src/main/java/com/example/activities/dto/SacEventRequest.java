package com.example.activities.dto;

import com.example.activities.model.EventStatus;
import lombok.Data;

@Data
public class SacEventRequest {
    private String title;
    private String description;
    private String category;
    private String date;
    private String startTime;
    private String endTime;
    private String venue;
    private Integer maxParticipants;
    private EventStatus status;
}
