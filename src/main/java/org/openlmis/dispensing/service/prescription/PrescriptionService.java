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

package org.openlmis.dispensing.service.prescription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionLineItemDto;
import org.openlmis.dispensing.repository.prescription.PrescriptionRepository;
import org.openlmis.dispensing.util.PrescriptionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionService {
  @Autowired
  private PrescriptionRepository prescriptionRepository;

  /**
   * Search for prescriptions.
   *
   * @param firstName   patient first name.
   * @param lastName    patient last name.
   * @param dateOfBirth patient date of birth.
   * @return List of prescriptions matching the criteria.
   */
  @Transactional(readOnly = true)
  public List<PrescriptionDto> searchPrescriptions(String firstName, String lastName, String dateOfBirth) {
    Specification<Prescription> spec = PrescriptionSpecifications.bySearchCriteria(firstName, lastName, dateOfBirth);
    return prescriptionRepository.findAll(spec).stream()
        .map(this::prescriptionToDto)
        .collect(Collectors.toList());
  }

  /**
   * Update a Prescription.
   *
   * @param id  prescription id.
   * @param dto prescription dto.
   * @return a updated prescription dto.
   */
  public PrescriptionDto updatePrescription(UUID id, PrescriptionDto dto) {
    Optional<Prescription> existingPrescription = prescriptionRepository.findById(id);

    if (!existingPrescription.isPresent()) {
      return null;
    }

    Prescription prescription = existingPrescription.get();
    updatePrescriptionEntity(prescription, dto);
    prescription = prescriptionRepository.save(prescription);

    return prescriptionToDto(prescription);
  }

  /**
   * Create a Prescription.
   *
   * @param prescriptionDto prescriptionDto.
   * @return id of created prescriptionDto.
   */
  @Transactional
  public UUID createPrescription(PrescriptionDto prescriptionDto) {
    Prescription prescription = convertToPrescriptionEntity(prescriptionDto);
    return prescriptionRepository.save(prescription).getId();
  }

  /**
   * Convert prescription dto to jpa model (entity).
   *
   * @param prescriptionDto Dto.
   * @return Prescription.
   */
  private Prescription convertToPrescriptionEntity(PrescriptionDto prescriptionDto) {
    if (null == prescriptionDto) {
      return null;
    }
    Prescription prescription = new Prescription();
    prescription.setPatientType(prescriptionDto.getPatientType());
    prescription.setFollowUpDate(prescriptionDto.getFollowUpDate());
    prescription.setIssueDate(prescriptionDto.getIssueDate());
    prescription.setCreatedDate(prescriptionDto.getCreatedDate());
    prescription.setCapturedDate(prescriptionDto.getCapturedDate());
    prescription.setLastUpdate(prescriptionDto.getLastUpdate());
    prescription.setIsVoided(prescriptionDto.getIsVoided());
    prescription.setStatus(prescriptionDto.getStatus());
    prescription.setFacilityId(prescriptionDto.getFacilityId());
    prescription.setUserId(prescriptionDto.getUserId());
    if (prescriptionDto.getLineItems() != null) {
      prescription.setLineItems(prescriptionDto.getLineItems().stream()
          .map(PrescriptionLineItemDto::toPrescriptionLineItem)
          .collect(Collectors.toList()));
    }
    return prescription;
  }

  /**
   * Create dto from jpa model.
   *
   * @param prescription jpa model.
   * @return Prescription created dto.
   */
  private PrescriptionDto prescriptionToDto(Prescription prescription) {
    return PrescriptionDto.builder()
        .patientType(prescription.getPatientType())
        .followUpDate(prescription.getFollowUpDate())
        .issueDate(prescription.getIssueDate())
        .createdDate(prescription.getCreatedDate())
        .capturedDate(prescription.getCapturedDate())
        .lastUpdate(prescription.getLastUpdate())
        .isVoided(prescription.getIsVoided())
        .status(prescription.getStatus())
        .facilityId(prescription.getFacilityId())
        .userId(prescription.getUserId())
        .lineItems(prescription.getLineItems() != null
            ? prescription.getLineItems().stream()
            .map(this::lineItemToDto)
            .collect(Collectors.toList())
            : null)
        .build();
  }

  /**
   * Convert PrescriptionLineItem entity to PrescriptionLineItemDto.
   *
   * @param lineItem PrescriptionLineItem entity.
   * @return PrescriptionLineItemDto.
   */
  private PrescriptionLineItemDto lineItemToDto(PrescriptionLineItem lineItem) {
    if (lineItem == null) {
      return null;
    }

    return PrescriptionLineItemDto.builder()
        .dosage(lineItem.getDosage())
        .period(lineItem.getPeriod())
        .batchId(lineItem.getBatchId())
        .quantityPrescribed(lineItem.getQuantityPrescribed())
        .quantityDispensed(lineItem.getQuantityDispensed())
        .servedInternally(lineItem.getServedInternally())
        .orderableId(lineItem.getOrderableId())
        .substituteOrderableId(lineItem.getSubstituteOrderableId())
        .comments(lineItem.getComments())
        .prescription(lineItem.getPrescription())
        .build();
  }

  private void updatePrescriptionEntity(Prescription prescription, PrescriptionDto prescriptionDto) {
    if (prescriptionDto.getPatientType() != null) {
      prescription.setPatientType(prescriptionDto.getPatientType());
    }
    if (prescriptionDto.getFollowUpDate() != null) {
      prescription.setFollowUpDate(prescriptionDto.getFollowUpDate());
    }
    if (prescriptionDto.getIssueDate() != null) {
      prescription.setIssueDate(prescriptionDto.getIssueDate());
    }
    if (prescriptionDto.getCreatedDate() != null) {
      prescription.setCreatedDate(prescriptionDto.getCreatedDate());
    }
    if (prescriptionDto.getCapturedDate() != null) {
      prescription.setCapturedDate(prescriptionDto.getCapturedDate());
    }
    if (prescriptionDto.getLastUpdate() != null) {
      prescription.setLastUpdate(prescriptionDto.getLastUpdate());
    }
    if (prescriptionDto.getIsVoided() != null) {
      prescription.setIsVoided(prescriptionDto.getIsVoided());
    }
    if (prescriptionDto.getStatus() != null) {
      prescription.setStatus(prescriptionDto.getStatus());
    }
    if (prescriptionDto.getFacilityId() != null) {
      prescription.setFacilityId(prescriptionDto.getFacilityId());
    }
    if (prescriptionDto.getUserId() != null) {
      prescription.setUserId(prescriptionDto.getUserId());
    }
    if (prescriptionDto.getLineItems() != null) {
      prescription.getLineItems().clear();
      prescription.getLineItems().addAll(prescriptionDto.getLineItems().stream()
          .map(PrescriptionLineItemDto::toPrescriptionLineItem)
          .collect(Collectors.toList()));
    }
  }

}
