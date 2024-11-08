package com.example.soroban.controller;

import com.example.soroban.model.Facility;

/**
 * Any class that deals with the modification of a Facility object must utilize this class.
 * @Author: Matthieu Larochelle
 * @Version: 1.1
 */
public class FacilityController {
    private Facility facility;

    /**
     * Constructor method for FacilityController.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     */
    public FacilityController(Facility facility) {
        this.facility = facility;
    }

    /**
     * Update Facility.
     * @Author: Kevin Li, Matthieu Larochelle
     * @Version: 1.1
     * @param newFacilityName: New Facility name.
     */
    public void updateFacility(String newFacilityName, String newFacilityDetails) {
        facility.setName(newFacilityName);
        facility.setDetails(newFacilityDetails);
    }
}
