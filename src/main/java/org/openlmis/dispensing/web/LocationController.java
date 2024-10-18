package org.openlmis.dispensing.web;

import org.openlmis.dispensing.dto.patient.LocationDto;
import org.openlmis.dispensing.service.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/api/location")
public class LocationController {
    public static final String ID_PATH_VARIABLE = "/{id}";
    @Autowired
    private LocationService locationService;



    /**
     * Create patient.
     *
     * @param locationDto a location dto bound to request body.
     * @return created location's ID.
     */
    @Transactional
    @RequestMapping(method = POST)
    public ResponseEntity<UUID> createLocation(@RequestBody LocationDto locationDto){
        UUID createdLocationId = locationService.createLocation(locationDto);
        ResponseEntity<UUID> response = new ResponseEntity<>(createdLocationId, CREATED);
        return response;
    }

    /**
     * Get patient with a given id (uuid).
     * A patients matching the given id.
     */
    @GetMapping(ID_PATH_VARIABLE)
    @ResponseStatus(OK)
    @ResponseBody
    public ResponseEntity<LocationDto> getLocation (@PathVariable UUID id){
        LocationDto location = locationService.getLocationById(id);
        return new ResponseEntity<>(location, OK);
    }
}
