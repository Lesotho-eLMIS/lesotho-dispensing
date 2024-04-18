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

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.UUID;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.service.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";

  static {
    org.slf4j.LoggerFactory.getLogger(PrescriptionController.class);
  }

  @Autowired
  private final PrescriptionService prescriptionService;

  public PrescriptionController(PrescriptionService prescriptionService) {
    this.prescriptionService = prescriptionService;
  }

  @PostMapping
  public ResponseEntity<UUID> createPrescription(@org.springframework.web.bind.annotation.RequestBody PrescriptionDto prescriptionDto) {
    UUID id = prescriptionService.createPrescription(prescriptionDto);
    return new ResponseEntity<>(id, HttpStatus.CREATED);
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
  @PutMapping("/{id}")
  @ResponseStatus(OK)
  public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable UUID id, @RequestBody PrescriptionDto dto) {
    PrescriptionDto updatedPrescription = prescriptionService.updatePrescription(id, dto);
    return new ResponseEntity<>(updatedPrescription, OK);
  }


}
