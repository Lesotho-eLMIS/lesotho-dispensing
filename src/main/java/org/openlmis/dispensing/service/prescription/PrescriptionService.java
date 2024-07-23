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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;
import org.openlmis.dispensing.domain.status.PrescriptionStatus;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionLineItemDto;
import org.openlmis.dispensing.dto.referencedata.LotDto;
import org.openlmis.dispensing.dto.referencedata.OrderableDto;
import org.openlmis.dispensing.dto.stockmanagement.StockCardSummaryDto;
import org.openlmis.dispensing.dto.stockmanagement.StockEventDto;
import org.openlmis.dispensing.dto.stockmanagement.StockEventLineItemDto;
import org.openlmis.dispensing.exception.ResourceNotFoundException;
import org.openlmis.dispensing.repository.patient.PatientRepository;
import org.openlmis.dispensing.repository.prescription.PrescriptionRepository;
import org.openlmis.dispensing.service.patient.PatientService;
import org.openlmis.dispensing.service.referencedata.LotReferenceDataService;
import org.openlmis.dispensing.service.referencedata.OrderableReferenceDataService;
import org.openlmis.dispensing.service.stockmanagement.StockCardSummariesStockManagementService;
import org.openlmis.dispensing.service.stockmanagement.StockEventStockManagementService;
import org.openlmis.dispensing.util.Message;
import org.openlmis.dispensing.util.PrescriptionSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionService.class);

  @Autowired
  private PrescriptionRepository prescriptionRepository;
  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private PatientService patientService;

  @Autowired
  private LotReferenceDataService lotReferenceDataService;

  @Autowired
  private StockCardSummariesStockManagementService stockCardSummariesStockManagementService;

  @Autowired
  private StockEventStockManagementService stockEventStockManagementService;

  @Autowired
  private OrderableReferenceDataService orderableReferenceDataService;

  @Value("${dispensing.dispensingdebit.reasonId}")
  private String dispensingDebitReasonId;

  /**
   * Search for prescriptions.
   *
   * @param firstName   patient first name.
   * @param lastName    patient last name.
   * @param dateOfBirth patient date of birth.
   * @return List of prescriptions matching the criteria.
   */

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
   * Serve a Prescription.
   *
   * @param id  prescription id.
   * @param dto prescription dto.
   * @return a updated prescription dto.
   */
  public PrescriptionDto servePrescription(UUID id, PrescriptionDto dto) {
    Optional<Prescription> existingPrescriptionOpt = prescriptionRepository.findById(id);

    if (!existingPrescriptionOpt.isPresent()) {
      return null;
    }
    Prescription existingPrescription = existingPrescriptionOpt.get();
    
    //Incoming prescription
    Prescription prescription = existingPrescription;
    updatePrescriptionEntity(prescription, dto);
    //convertToPrescriptionEntity(dto);
    
    // debit stock
    for (PrescriptionLineItem prescriptionLineItem : prescription.getLineItems()) {
      if(prescriptionLineItem.getStatus().equals("Dispensed")){
        //skip lines that have succeeded before
        continue;
      }
      // Get SOH - call
      LotDto lot = lotReferenceDataService
          .findOne(prescriptionLineItem.getLotId());
      
      OrderableDto orderable = null;
      if(null == prescriptionLineItem.getSubstituteOrderableId()){
        orderable = orderableReferenceDataService
          .findOne(prescriptionLineItem.getOrderableId());
      } else {
        orderable = orderableReferenceDataService
          .findOne(prescriptionLineItem.getSubstituteOrderableId());
      }
      
      UUID programId = orderable.getPrograms().stream().findFirst().get().getProgramId();
      List<StockCardSummaryDto> stockCardSummaries = stockCardSummariesStockManagementService
          .search(
              programId,
              prescription.getFacilityId(),
              Collections.singleton(orderable.getId()),
              LocalDate.now(),
              lot.getLotCode());

      if (!stockCardSummaries.isEmpty()) {
        Integer stockOnHand = stockCardSummaries.get(0).getStockOnHand();
        if (prescriptionLineItem.getQuantityDispensed() <= stockOnHand) {
          LOGGER.info("We have enough stock for product "
              + prescriptionLineItem.getOrderableId());
          // debit bulk orderable
          StockEventDto stockEventDebit = new StockEventDto();
          stockEventDebit.setFacilityId(prescription.getFacilityId());
          stockEventDebit.setProgramId(programId);
          stockEventDebit.setUserId(prescription.getServedByUserId());
          StockEventLineItemDto lineItemDebit = new StockEventLineItemDto(
              orderable.getId(),
              prescriptionLineItem.getLotId(),
              prescriptionLineItem.getQuantityDispensed(),
              LocalDate.now(),
              UUID.fromString(dispensingDebitReasonId));
          stockEventDebit.setLineItems(Collections.singletonList(lineItemDebit));
          // submit stock event to stockmanagement service
          LOGGER.error("Submitting stockevent DR : " + stockEventDebit.toString());
          stockEventStockManagementService.submit(stockEventDebit);

          prescriptionLineItem.setStatus("Dispensed");
          //prescription.setId(existingPrescription.get().getId());
          
        } else {
          //Not enough stock for this line item
          prescriptionLineItem.setStatus("Failure - inadequate stock");
        }
      } else {
        //the specified product (orderable or substitute) is not available at this facility
        prescriptionLineItem.setStatus("Failure - product not found");
      }
    }

    //if all lines are Dispensed or if not dispesnsed but served internal is false
    // status = served
    //else if any line is served internally, status = patially served
    prescription.setStatus(PrescriptionStatus.SERVED);
    prescription = prescriptionRepository.save(prescription);

    return prescriptionToDto(prescription);
  }

  /**
   * Update a PrescriptionLineItem.
   */
  public void updateLineItemEntity(PrescriptionLineItem lineItem, PrescriptionLineItemDto lineItemDto) {
    lineItem.setDosage(lineItemDto.getDosage());
    lineItem.setPeriod(lineItemDto.getPeriod());
    lineItem.setLotId(lineItemDto.getLotId());
    lineItem.setQuantityPrescribed(lineItemDto.getQuantityPrescribed());
    lineItem.setQuantityDispensed(lineItemDto.getQuantityDispensed());
    lineItem.setServedInternally(lineItemDto.getServedInternally());
    lineItem.setOrderableId(lineItemDto.getOrderableId());
    lineItem.setSubstituteOrderableId(lineItemDto.getSubstituteOrderableId());
    lineItem.setComments(lineItemDto.getComments());
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
    prescription.setStatus(PrescriptionStatus.INITIATED);
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

    Optional<Patient> patient = patientRepository.findById(prescriptionDto.getPatientId());
    if (patient.isPresent()) {
      Prescription prescription = new Prescription();
      LocalDate today = LocalDate.now();
      prescription.setPatient(patient.get());
      prescription.setPatientType(prescriptionDto.getPatientType());
      prescription.setFollowUpDate(prescriptionDto.getFollowUpDate());
      prescription.setIssueDate(prescriptionDto.getIssueDate());
      prescription.setCreatedDate(today);
      prescription.setCapturedDate(today);
      prescription.setLastUpdate(today);
      prescription.setIsVoided(prescriptionDto.getIsVoided());
      //prescription.setStatus(prescriptionDto.getStatus());
      prescription.setFacilityId(prescriptionDto.getFacilityId());
      prescription.setPrescribedByUserId(prescriptionDto.getPrescribedByUserId());
      prescription.setServedByUserId(prescriptionDto.getServedByUserId());
      if (prescriptionDto.getLineItems() != null) {
        List<PrescriptionLineItem> lineItems = prescriptionDto.getLineItems().stream()
            .map(lineItemDto -> {
              PrescriptionLineItem lineItem = lineItemDto.toPrescriptionLineItem(); // No parameter passed
              lineItem.setPrescription(prescription); // Set the prescription for each line item
              return lineItem;
            })
            .collect(Collectors.toList());
        prescription.setLineItems(lineItems);
      }
      return prescription;
    }
    return null;
  }

  private PrescriptionLineItem convertToPrescriptionLineItemEntity(PrescriptionLineItemDto lineItemDto,
      Prescription prescription) {
    if (lineItemDto == null) {
      return null;
    }
    return new PrescriptionLineItem(lineItemDto.getDosage(), lineItemDto.getPeriod(),
        lineItemDto.getLotId(), lineItemDto.getQuantityPrescribed(), lineItemDto.getQuantityDispensed(),
        lineItemDto.getServedInternally(), lineItemDto.getOrderableId(), lineItemDto.getSubstituteOrderableId(),
        lineItemDto.getComments(), lineItemDto.getStatus(), prescription);
  }

  /**
   * Create dto from jpa model.
   *
   * @param prescription jpa model.
   * @return Prescription created dto.
   */
  private PrescriptionDto prescriptionToDto(Prescription prescription) {
    return PrescriptionDto.builder()
        .id(prescription.getId())
        .patientId(prescription.getPatient().getId())
        .patientType(prescription.getPatientType())
        .followUpDate(prescription.getFollowUpDate())
        .issueDate(prescription.getIssueDate())
        .createdDate(prescription.getCreatedDate())
        .capturedDate(prescription.getCapturedDate())
        .lastUpdate(prescription.getLastUpdate())
        .isVoided(prescription.getIsVoided())
        //.status(prescription.getStatus())
        .facilityId(prescription.getFacilityId())
        .prescribedByUserId(prescription.getPrescribedByUserId())
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
        .id(lineItem.getId())
        .dosage(lineItem.getDosage())
        .period(lineItem.getPeriod())
        .lotId(lineItem.getLotId())
        .quantityPrescribed(lineItem.getQuantityPrescribed())
        .quantityDispensed(lineItem.getQuantityDispensed())
        .servedInternally(lineItem.getServedInternally())
        .orderableId(lineItem.getOrderableId())
        .substituteOrderableId(lineItem.getSubstituteOrderableId())
        .comments(lineItem.getComments())
        .status(lineItem.getStatus())
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
    // if (prescriptionDto.getStatus() != null) {
    //   prescription.setStatus(prescriptionDto.getStatus());
    // }
    if (prescriptionDto.getFacilityId() != null) {
      prescription.setFacilityId(prescriptionDto.getFacilityId());
    }
    if (prescriptionDto.getPrescribedByUserId() != null) {
      prescription.setPrescribedByUserId(prescriptionDto.getPrescribedByUserId());
    }
    if (prescriptionDto.getServedByUserId() != null) {
      prescription.setServedByUserId(prescriptionDto.getServedByUserId());
    }
    if (prescriptionDto.getLineItems() != null) {
      prescription.getLineItems().clear();
      prescription.getLineItems().addAll(prescriptionDto.getLineItems().stream()
          .map(lineItemDto -> convertToPrescriptionLineItemEntity(lineItemDto, prescription))
          .collect(Collectors.toList()));
    }
  }

  /**
   * Get a Prescription.
   *
   * @param id prescription id.
   *
   * @return a prescription dto.
   */
  public PrescriptionDto getPrescriptionById(UUID id) {
    Optional<Prescription> prescriptionOptional = prescriptionRepository.findById(id);

    if (prescriptionOptional.isPresent()) {
      return prescriptionToDto(prescriptionOptional.get());
    } else {
      throw new ResourceNotFoundException(new Message("Prescription id not found ", id));
    }
  }

  /**
   * Get a Prescription.
   *
   *
   * @return a prescriptions dto.
   */
  public List<PrescriptionDto> getAllPrescriptions() {
    List<Prescription> prescriptions = prescriptionRepository.findAll();
    return prescriptions.stream()
        .map(this::prescriptionToDto)
        .collect(Collectors.toList());
  }

  /**
   * Set prescription to isVoided.
   *
   * @param id id of prescription to update
   */
  @Transactional
  public void setIsVoided(UUID id) {
    Optional<Prescription> optionalPrescription = prescriptionRepository.findById(id);
    if (optionalPrescription.isPresent()) {
      Prescription prescription = optionalPrescription.get();
      prescription.setIsVoided(true);
      prescriptionRepository.saveAndFlush(prescription);
    } else {
      throw new ResourceNotFoundException(new Message("Prescription id not found ", id));
    }
  }

  /**
   * Get a Prescription based on parameters.
   *
   *
   * @return a prescriptions dtos.
   */
  public List<PrescriptionDto> searchPrescriptions(String patientNumber, String firstName, String lastName,
      String dateOfBirth,
      UUID facilityUuid, String nationalId, String status, String patientType, Boolean isVoided,
      LocalDate followUpDate) {

    // First, find the patients based on the given patient details
    List<PatientDto> patientDtos = patientService.searchPatients(patientNumber, firstName, lastName, dateOfBirth,
        facilityUuid, nationalId);

    if (patientDtos.isEmpty()) {
      return new ArrayList<PrescriptionDto>();
    }

    // Extract the patient IDs from the found patients and convert them to string
    List<UUID> patientIds = patientDtos.stream()
        .map(PatientDto::getId)
        .collect(Collectors.toList());
    // Create the Specification
    Specification<Prescription> spec = Specification
        .where(PrescriptionSpecification.patientIdIn(patientIds))
        .and(PrescriptionSpecification.statusEquals(status))
        .and(PrescriptionSpecification.patientTypeEquals(patientType))
        .and(PrescriptionSpecification.isVoidedEquals(isVoided))
        .and(PrescriptionSpecification.followUpDateEquals(followUpDate));

    // Then, search for prescriptions based on the Specification
    List<Prescription> prescriptions = prescriptionRepository.findAll(spec);

    // Convert Prescription entities to PrescriptionDto objects
    // return prescriptions.stream()
    // .map(this::prescriptionToDto)
    // .collect(Collectors.toList());
    return prescriptions == null ? new ArrayList<PrescriptionDto>()
        : prescriptions.stream()
            .map(this::prescriptionToDto)
            .collect(Collectors.toList());
  }

}
