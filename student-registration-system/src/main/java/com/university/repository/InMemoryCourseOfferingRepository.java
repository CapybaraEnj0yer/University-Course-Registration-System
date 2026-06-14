package com.university.repository;

import com.university.model.CourseOffering;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCourseOfferingRepository implements CourseOfferingRepository {
    private Map<String, CourseOffering> database = new HashMap<>();

    @Override
    public CourseOffering findById(String offeringId) {
        return database.get(offeringId);
    }

    @Override
    public List<CourseOffering> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void save(CourseOffering offering) {
        database.put(offering.getOfferingId(), offering);
    }
}