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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.flywaydb.core.internal.util.StringUtils;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.service.prescription.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/prescription")
public class PrescriptionController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";
  private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionController.class);

  @Autowired
  private PrescriptionService prescriptionService;

  /**
   * Create prescription.
   *
   * @param prescriptionDto a prescription dto bound to request body.
   * @return created prescription's ID.
   */
  @Transactional
  @RequestMapping(method = POST)
  public ResponseEntity<UUID> createPrescription(
      @org.springframework.web.bind.annotation.RequestBody PrescriptionDto prescriptionDto) {
    LOGGER.debug("Try to create a prescription");
    Profiler profiler = getProfiler("CREATE_PRESCRIPTION", prescriptionDto);

    profiler.start("PROCESS");
    UUID createdPrescriptionId = prescriptionService.createPrescription(prescriptionDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdPrescriptionId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * Get prescription with a given id (uuid).
   * A prescriptions matching the given id.
   */
  @GetMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PrescriptionDto> getPrescription(@PathVariable UUID id) {
    PrescriptionDto prescription = prescriptionService.getPrescriptionById(id);
    if (null == prescription) {
      return new ResponseEntity<>(NOT_FOUND);
    }
    return new ResponseEntity<>(prescription, OK);
  }

  /**
   * Update a Prescription.
   *
   * @param id  Prescription id.
   * @param dto Prescription dto.
   * @return Updated Prescription dto.
   */
  @Transactional
  @PutMapping(ID_PATH_VARIABLE)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable UUID id, @RequestBody PrescriptionDto dto) {
    PrescriptionDto updatedPrescription = prescriptionService.updatePrescription(id, dto);
    return new ResponseEntity<>(updatedPrescription, OK);
  }

  /**
   * Serve a Prescription.
   *
   * @param id  Prescription id.
   * @param dto Prescription dto.
   * @return Updated Prescription dto.
   */
  @Transactional
  @RequestMapping(value = "/{id}/serve", method = POST)
  @ResponseStatus(OK)
  @ResponseBody
  public ResponseEntity<PrescriptionDto> servePrescription(@PathVariable UUID id, @RequestBody PrescriptionDto dto) {
    PrescriptionDto servedPrescription = prescriptionService.servePrescription(id, dto);
    return new ResponseEntity<>(servedPrescription, OK);
  }

  /**
   * Makes prescription void.
   *
   * @param id prescription id.
   */
  @Transactional
  @RequestMapping(value = "/{id}/void", method = POST)
  @ResponseStatus(OK)
  public void deactivate(@PathVariable UUID id) {
    LOGGER.debug("Try to make prescription with id: {} void", id);
    prescriptionService.setIsVoided(id);
    LOGGER.debug("prescription with id: {} made void", id);
  }

  /**
   * Get prescriptions based on parameters.
   *
   * @return List of all prescriptions.
   */
  @GetMapping
  @ResponseStatus
  @ResponseBody
  // @RequestMapping(value = "/prescriptions", method = RequestMethod.GET)
  public ResponseEntity<List<PrescriptionDto>> searchPrescriptions(
      @RequestParam(required = false) String patientNumber,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String dateOfBirth,
      @RequestParam(required = false) String facilityId,
      @RequestParam(required = false) String nationalId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String patientType,
      @RequestParam(required = false) Boolean isVoided,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate followUpDate) {

    // Convert facilityId to UUID if not null or empty
    UUID facilityUuid = StringUtils.hasText(facilityId) ? UUID.fromString(facilityId) : null;

    // Call the service method to search for prescriptions
    List<PrescriptionDto> prescriptionDtos = prescriptionService.searchPrescriptions(
        patientNumber, firstName, lastName, dateOfBirth, facilityUuid, nationalId,
        status, patientType, isVoided, followUpDate);

    // Return the response entity with the list of PrescriptionDto
    return ResponseEntity.ok(prescriptionDtos);
  }

}
