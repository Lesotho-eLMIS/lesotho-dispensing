package org.openlmis.dispensing.service.location;

import org.openlmis.dispensing.domain.patient.Location;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.dto.patient.LocationDto;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.repository.location.LocationRepository;
import org.openlmis.dispensing.repository.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create a Patient.
     *
     * @param locationDto locationDto.
     * @return id of created locationDto.
     */
    @Transactional
    public UUID createLocation(LocationDto locationDto){
        Location location = convertToLocationEntity(locationDto);
        return locationRepository.save(location).getId();
    }

    /**
     * Convert patient dto to jpa model (entity).
     *
     * @param locationDto Dto.
     * @return LocationPatient.
     */
    private Location convertToLocationEntity(LocationDto locationDto){
        if (null == locationDto){
            return null;
        }

        Location location = new Location();
        location.setChief(locationDto.getChief());
        location.setConstituency(locationDto.getConstituency());
        location.setVillage(locationDto.getVillage());
        location.setDistrict(locationDto.getDistrict());

        return location;
    }

    /**
     * Get a Location.
     *
     * @param id location id.
     *
     * @return a location dto.
     */
    public LocationDto getLocationById (UUID id){
        Optional<Location> locationOptional = locationRepository.findById(id);

        if (locationOptional.isPresent()){
            return locationToDto(locationOptional.get());
        }
        return null;
    }
    /**
     * Create dto from jpa model.
     *
     * @param location jpa model.
     * @return location created dto.
     */
    private LocationDto locationToDto(Location location){
        return LocationDto.builder()
                .id(location.getId())
                .district(location.getDistrict())
                .village(location.getConstituency())
                .chief(location.getChief())
                .build();
    }


}
