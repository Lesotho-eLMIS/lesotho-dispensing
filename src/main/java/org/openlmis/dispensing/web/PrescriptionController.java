package org.openlmis.dispensing.web;

import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.service.prescription.PrescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController extends BaseController {
  public static final String ID_PATH_VARIABLE = "/{id}";
  private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionController.class);

  @Autowired
  private PrescriptionService prescriptionService;

  public PrescriptionController(PrescriptionService prescriptionService) {
    this.prescriptionService = prescriptionService;
  }

  @PostMapping
  public ResponseEntity<UUID> createPrescription(@RequestBody PrescriptionDto prescriptionDto) {
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
  @GetMapping
  public ResponseEntity<List<PrescriptionDto>> searchPrescriptions(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) String dateOfBirth) {
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
  public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable UUID id,
                                                            @RequestBody PrescriptionDto dto) {
    PrescriptionDto updatedPrescription = prescriptionService.updatePrescription(id, dto);
    return new ResponseEntity<>(updatedPrescription, HttpStatus.OK);
  }


}
