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

package org.openlmis.dispensing.web;

import static org.springframework.http.HttpStatus.CREATED;
//import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import org.openlmis.dispensing.dto.patient.PatientDto;
// import org.openlmis.dispensing.service.PermissionService;
import org.openlmis.dispensing.service.patient.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller used to perform CRUD operations on point of delivery event.
 */
@Controller
@RequestMapping("/api/patient")
public class PatientController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";
  private static final Logger LOGGER = LoggerFactory.getLogger(PatientController.class);

  //   @Autowired
  //   private PermissionService permissionService;


  @Autowired
  private PatientService patientService;

  /**
   * Create patient.
   *
   * @param patientDto a patient dto bound to request body.
   * @return created patient's ID.
   */
  @Transactional
  @RequestMapping(method = POST)
  public ResponseEntity<UUID> createPatient(
        @RequestBody PatientDto patientDto) {

    LOGGER.debug("Try to create a patient");

    Profiler profiler = getProfiler("CREATE_PATIENT", patientDto);

    //checkPermission(pointOfDeliveryEventDto, profiler.startNested("CHECK_PERMISSION"));

    profiler.start("PROCESS");
    UUID createdPatientId = patientService.createPatient(patientDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdPatientId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * List patients matching the given attributes.
   * @param patientNumber unique patient number.
   * @param firstName patient first name.
   * @param lastName patient last name.
   * @param dateOfBirth patient date of birth.
   * @return List of patients matching the given attributes.
   */
  @RequestMapping(method = GET)
  public ResponseEntity<List<PatientDto>> searchPatients(
      @RequestParam(required = false) String patientNumber,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String dateOfBirth,
      @RequestParam(required = false) String facilityId,
      @RequestParam(required = false) String geoZoneId,
      @RequestParam(required = false) String nationalId) {
    UUID facilityUuid = null;
    UUID geoZoneUuid = null;
    if (facilityId != null && !facilityId.isEmpty()) {
      try {
        facilityUuid = UUID.fromString(facilityId);  // Convert String to UUID
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(null);
      }
    }
    LocalDate dob = null;
    if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
      try {
        dob = LocalDate.parse(dateOfBirth);  // Convert String to LocalDate
      } catch (DateTimeParseException e) {
        return ResponseEntity.badRequest().body(null);  // Return bad request if parsing fails
      }
    }

    if (geoZoneId != null && !geoZoneId.isEmpty()) {
      try {
        geoZoneUuid = UUID.fromString(geoZoneId);  // Convert String to UUID
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(null);
      }
    }

    List<PatientDto> patientDtos = patientService.searchPatients(patientNumber, firstName, lastName, dob, facilityUuid, geoZoneUuid, nationalId);
    return new ResponseEntity<>(patientDtos, OK);
  }

  /**
   * Get patient with a given id (uuid).
   * A patients matching the given id.
   */
  @GetMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PatientDto> getPatien(@PathVariable UUID id) {
    PatientDto patient = patientService.getPatientById(id);
    return new ResponseEntity<>(patient, OK);
  }

  /**
   * Update a Patient.
   *
   * @param id Patient id.
   * @param dto Patient dto.
   * @return Updates Patient dto.
   */
  @Transactional
  @PutMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PatientDto> updatePatient(@PathVariable UUID id,
                                                    @RequestBody PatientDto dto) {
    PatientDto updatedPatient = patientService.updatePatient(id, dto);
    return new ResponseEntity<>(updatedPatient, OK);
  }
}
