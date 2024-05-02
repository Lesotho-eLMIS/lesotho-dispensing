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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.UUID;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.service.prescription.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
  public ResponseEntity<UUID> createPrescription(@org.springframework.web.bind.annotation.RequestBody PrescriptionDto prescriptionDto) {
    LOGGER.debug("Try to create a prescription");
    Profiler profiler = getProfiler("CREATE_PRESCRIPTION", prescriptionDto);

    profiler.start("PROCESS");
    UUID createdPrescriptionId = prescriptionService.createPrescription(prescriptionDto);

    profiler.start("CREATE_RESPONSE");
    ResponseEntity<UUID> response = new ResponseEntity<>(createdPrescriptionId, CREATED);

    return stopProfiler(profiler, response);
  }

  /**
   * List prescriptions matching the given attributes.
   *
   * @param firstName   patient first name.
   * @param lastName    patient last name.
   * @param dateOfBirth patient date of birth.
   * @return List of prescriptions matching the given attributes.
   */
  @org.springframework.web.bind.annotation.GetMapping
  public ResponseEntity<List<PrescriptionDto>> searchPrescriptions(@RequestParam(required = false) String firstName,
                                                                   @RequestParam(required = false) String lastName, @RequestParam(required = false) String dateOfBirth) {
    List<PrescriptionDto> prescriptionDtos = prescriptionService.searchPrescriptions(firstName, lastName, dateOfBirth);
    return new ResponseEntity<>(prescriptionDtos, OK);
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


}
