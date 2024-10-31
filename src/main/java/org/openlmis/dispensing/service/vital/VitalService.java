/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.dispensing.service.vital;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.domain.vital.Vital;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.dto.vital.VitalDto;
import org.openlmis.dispensing.exception.ResourceNotFoundException;
import org.openlmis.dispensing.repository.patient.PatientRepository;
import org.openlmis.dispensing.repository.vital.VitalRepository;
import org.openlmis.dispensing.service.patient.PatientService;
import org.openlmis.dispensing.util.Message;
import org.openlmis.dispensing.util.VitalSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VitalService {


    @Autowired
    private VitalRepository vitalRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private PatientService patientService;

    /**
     * Update a Vital.
     *
     * @param id  vital id.
     * @param dto vital dto.
     * @return a updated vital dto.
     */
    public VitalDto updateVital(UUID id, VitalDto dto) {
        Optional<Vital> existingVital = vitalRepository.findById(id);

        if (!existingVital.isPresent()) {
            return null;
        }

        Vital vital = existingVital.get();
        updateVitalEntity(vital, dto);
        vital = vitalRepository.save(vital);

        return vitalToDto(vital);
    }

    /**
     * Create a Vital.
     *
     * @param vitalDto vitalDto.
     * @return id of created vitalDto.
     */
    @Transactional
    public UUID createVital(VitalDto vitalDto) {
        Vital vital = convertToVitalEntity(vitalDto);
        return vitalRepository.save(vital).getId();
    }

    /**
     * Convert vital dto to jpa model (entity).
     *
     * @param vitalDto Dto.
     * @return Vital.
     */
    private Vital convertToVitalEntity(VitalDto vitalDto) {
        if (null == vitalDto) {
            return null;
        }

        Optional<Patient> patient = patientRepository.findById(vitalDto.getPatientId());
        if (patient.isPresent()) {
            Vital vital = new Vital();
            vital.setPatient(patient.get());
            vital.setHeight(vitalDto.getHeight());
            vital.setWeight(vitalDto.getWeight());
            vital.setTbStatus(vitalDto.getTbStatus());
            vital.setSystolic(vitalDto.getSystolic());
            vital.setDiastolic(vitalDto.getDiastolic());
            vital.setMuac(vitalDto.getMuac());
            return vital;
        }
        return null;
    }

    /**
     * Create dto from jpa model.
     *
     * @param vital jpa model.
     * @return Vital created dto.
     */
    private VitalDto vitalToDto(Vital vital) {
        return VitalDto.builder()
                .id(vital.getId())
                .patientId(vital.getPatient().getId())
                .height(vital.getHeight())
                .weight(vital.getWeight())
                .tbStatus(vital.getTbStatus())
                .systolic(vital.getSystolic())
                .diastolic(vital.getDiastolic())
                .muac(vital.getMuac())
                .build();
    }

    private void updateVitalEntity(Vital vital, VitalDto vitalDto) {
        if (vitalDto.getHeight() != null) {
            vital.setHeight(vitalDto.getHeight());
        }
        if (vitalDto.getWeight() != null) {
            vital.setWeight(vitalDto.getWeight());
        }
        if (vitalDto.getTbStatus() != null) {
            vital.setTbStatus(vitalDto.getTbStatus());
        }
        if (vitalDto.getSystolic() != null) {
            vital.setSystolic(vitalDto.getSystolic());
        }
        if (vitalDto.getDiastolic() != null) {
            vital.setDiastolic(vitalDto.getDiastolic());
        }
        if (vitalDto.getMuac() != null) {
            vital.setMuac(vitalDto.getMuac());
        }
    }

    /**
     * Get a Vital.
     *
     * @param id vital id.
     *
     * @return a vital dto.
     */
    public VitalDto getVitalById(UUID id) {
        Optional<Vital> vitalOptional = vitalRepository.findById(id);

        if (vitalOptional.isPresent()) {
            return vitalToDto(vitalOptional.get());
        } else {
            throw new ResourceNotFoundException(new Message("Vital id not found ", id));
        }
    }

    /**
     * Get a Vital.
     *
     *
     * @return a vitals dto.
     */
    public List<VitalDto> getAllVitals() {
        List<Vital> vitals = vitalRepository.findAll();
        return vitals.stream()
                .map(this::vitalToDto)
                .collect(Collectors.toList());
    }

  /**
  * Get a Vital based on parameters.
  *
  *
  * @return a vitals dtos.
  */
  public List<VitalDto> searchVitals(String patientNumber) {

    // First, find the patients based on the given patient details
    List<PatientDto> patientDtos = patientService.searchPatientByPatientNumber(patientNumber);

    if (patientDtos.isEmpty()) {
      return new ArrayList<VitalDto>();
    }

    // Extract the patient IDs from the found patients and convert them to string
    List<UUID> patientIds = patientDtos.stream()
        .map(PatientDto::getId)
        .collect(Collectors.toList());
    // Create the Specification
    Specification<Vital> spec = Specification
        .where(VitalSpecification.patientIdIn(patientIds));

    // Then, search for vitals based on the Specification
    List<Vital> vitals = vitalRepository.findAll(spec);

    // Convert Vital entities to VitalDto objects
    return vitals == null ? new ArrayList<VitalDto>()
        : vitals.stream()
            .map(this::vitalToDto)
            .collect(Collectors.toList());
  }

}
