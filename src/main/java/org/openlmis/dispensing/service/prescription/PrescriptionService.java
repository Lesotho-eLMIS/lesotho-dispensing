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
import org.openlmis.dispensing.domain.status.PrescriptionLineItemStatus;
import org.openlmis.dispensing.domain.status.PrescriptionStatus;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionLineItemDto;
import org.openlmis.dispensing.dto.referencedata.FacilityDto;
import org.openlmis.dispensing.dto.referencedata.LotDto;
import org.openlmis.dispensing.dto.referencedata.OrderableDto;
import org.openlmis.dispensing.dto.stockmanagement.StockCardSummaryDto;
import org.openlmis.dispensing.dto.stockmanagement.StockEventDto;
import org.openlmis.dispensing.dto.stockmanagement.StockEventLineItemDto;
import org.openlmis.dispensing.exception.ResourceNotFoundException;
import org.openlmis.dispensing.repository.patient.PatientRepository;
import org.openlmis.dispensing.repository.prescription.PrescriptionRepository;
import org.openlmis.dispensing.service.patient.PatientService;
import org.openlmis.dispensing.service.referencedata.FacilityReferenceDataService;
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
  private FacilityReferenceDataService facilityReferenceDataService;

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
      if (prescriptionLineItem.getStatus().equals(PrescriptionLineItemStatus.FULLY_SERVED)) {
        //skip lines that have succeeded before
        continue;
      }
      // Get SOH - call
      LotDto lot = lotReferenceDataService
          .findOne(prescriptionLineItem.getLotId());
      
      OrderableDto orderable = orderableReferenceDataService
          .findOne(prescriptionLineItem.getOrderableDispensed());
      
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
              + orderable.getFullProductName());
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

          
          // if (prescriptionLineItem.getRemainingBalance() == 0) {
          //   prescriptionLineItem.setRemainingBalance(prescriptionLineItem.getQuantityPrescribed());
          // }
          // Integer balanceBeforeServe = existingPrescription.getLineItems().get(position).getRemainingBalance();
          // prescriptionLineItem.setRemainingBalance(
          //   balanceBeforeServe - prescriptionLineItem.getQuantityDispensed()
          // );

          // All these will be computed by UI
          
          if (((prescriptionLineItem.getRemainingBalance() > 0) && (prescriptionLineItem.getServedExternally())) 
              || (prescriptionLineItem.getRemainingBalance() == 0)) {
            //don't create backorder - we are done
            prescriptionLineItem.setStatus(PrescriptionLineItemStatus.FULLY_SERVED);
          } else {
            //create backorder
            prescriptionLineItem.setStatus(PrescriptionLineItemStatus.PARTIALLY_SERVED);
          }
          
        } else {
          //Not enough stock for this line item
          prescriptionLineItem.setStatus(PrescriptionLineItemStatus.INADEQUATE_STOCK);
        }
      } else {
        //the specified product (orderable or substitute) is not available at this facility
        prescriptionLineItem.setStatus(PrescriptionLineItemStatus.PRODUCT_NOT_EXIST);
      }
    }

    //if all lines are Dispensed or if not dispesnsed but served internal is false
    // status = served
    //else if any line is served internally, status = patially served
    Boolean isFullyServed = true;
    for (PrescriptionLineItem lineItem : prescription.getLineItems()) {
      if (!lineItem.getStatus().equals(PrescriptionLineItemStatus.FULLY_SERVED)) {
        isFullyServed = false;
      }
    }

    if (isFullyServed) {
      prescription.setStatus(PrescriptionStatus.FULLY_SERVED);
    } else {
      prescription.setStatus(PrescriptionStatus.PARTIALLY_SERVED);
    }

    prescription = prescriptionRepository.save(prescription);

    return prescriptionToDto(prescription);
  }

  /**
   * Update a PrescriptionLineItem.
   */
  public void updateLineItemEntity(PrescriptionLineItem lineItem, PrescriptionLineItemDto lineItemDto) {
    lineItem.setDose(lineItemDto.getDose());
    lineItem.setDoseUnits(lineItemDto.getDoseUnits());
    lineItem.setDoseFrequency(lineItemDto.getDoseFrequency());
    lineItem.setRoute(lineItemDto.getRoute());
    lineItem.setDuration(lineItemDto.getDuration());
    lineItem.setDurationUnits(lineItemDto.getDurationUnits());
    lineItem.setAdditionalInstructions(lineItemDto.getAdditionalInstructions());
    lineItem.setOrderablePrescribed(lineItemDto.getOrderablePrescribed());
    lineItem.setQuantityPrescribed(lineItemDto.getQuantityPrescribed());
    lineItem.setOrderableDispensed(lineItemDto.getOrderableDispensed());
    lineItem.setLotId(lineItemDto.getLotId());
    lineItem.setQuantityDispensed(lineItemDto.getQuantityDispensed());
    lineItem.setServedExternally(lineItemDto.getServedExternally());
    lineItem.setComments(lineItemDto.getComments());
    lineItem.setRemainingBalance(lineItemDto.getRemainingBalance());
    //lineItem.setStatus(PrescriptionLineItemStatus.valueOf(lineItemDto.getStatus()));
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
      prescription.setStatus(prescriptionDto.getStatus());
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

  // private PrescriptionLineItem convertToPrescriptionLineItemEntity(PrescriptionLineItemDto lineItemDto,
  //     Prescription prescription) {
  //   if (lineItemDto == null) {
  //     return null;
  //   }
  //   return new PrescriptionLineItem(lineItemDto.getDosage(), lineItemDto.getPeriod(),
  //       lineItemDto.getLotId(), lineItemDto.getQuantityPrescribed(), lineItemDto.getQuantityDispensed(),
  //       lineItemDto.getServedExternally(), lineItemDto.getOrderableId(), lineItemDto.getSubstituteOrderableId(),
  //       lineItemDto.getComments(), lineItemDto.getStatus(), prescription);
  // }

  private PrescriptionLineItem convertToPrescriptionLineItemEntity(PrescriptionLineItemDto lineItemDto,
      Prescription prescription) {
    if (lineItemDto == null) {
      return null;
    }
    PrescriptionLineItem item = new PrescriptionLineItem(
        lineItemDto.getDose(),
        lineItemDto.getDoseUnits(),
        lineItemDto.getDoseFrequency(),
        lineItemDto.getRoute(),
        lineItemDto.getDuration(),
        lineItemDto.getDurationUnits(),
        lineItemDto.getAdditionalInstructions(),
        lineItemDto.getOrderablePrescribed(),
        lineItemDto.getQuantityPrescribed(),
        lineItemDto.getOrderableDispensed(),
        lineItemDto.getLotId(),
        lineItemDto.getQuantityDispensed(),
        lineItemDto.getRemainingBalance(),
        lineItemDto.getServedExternally(),
        lineItemDto.getComments(),
        lineItemDto.getCollectBalanceDate()
    );
    // item.setStatus(PrescriptionLineItemStatus.valueOf(lineItemDto.getStatus()));
    item.setPrescription(prescription);
    return item;
  }

  /**
   * Create dto from jpa model.
   *
   * @param prescription jpa model.
   * @return Prescription created dto.
   */
  private PrescriptionDto prescriptionToDto(Prescription prescription) {
    FacilityDto facility = facilityReferenceDataService.findOne(prescription.getFacilityId());
    return PrescriptionDto.builder()
        .id(prescription.getId())
        .patientId(prescription.getPatient().getId())
        .patientFirstName(prescription.getPatient().getPerson().getFirstName())
        .patientLastName(prescription.getPatient().getPerson().getLastName())
        .patientNumber(prescription.getPatient().getPatientNumber())
        .patientType(prescription.getPatientType())
        .followUpDate(prescription.getFollowUpDate())
        .issueDate(prescription.getIssueDate())
        .createdDate(prescription.getCreatedDate())
        .capturedDate(prescription.getCapturedDate())
        .lastUpdate(prescription.getLastUpdate())
        .isVoided(prescription.getIsVoided())
        .status(prescription.getStatus())
        .facilityId(prescription.getFacilityId())
        .facilityName(facility != null ? facility.getName() : null)
        .prescribedByUserId(prescription.getPrescribedByUserId())
        .servedByUserId(prescription.getServedByUserId())
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
   * @param item PrescriptionLineItem entity.
   * @return PrescriptionLineItemDto.
   */
  private PrescriptionLineItemDto lineItemToDto(PrescriptionLineItem item) {
    if (item == null) {
      return null;
    }
    OrderableDto dispensedOrderable = null;
    OrderableDto prescribedOrderable = null;
    LotDto lot = null;

    if (item.getOrderablePrescribed() != null) {
      prescribedOrderable = orderableReferenceDataService.findOne(item.getOrderablePrescribed());
    }
    if (item.getOrderableDispensed() != null) {
      dispensedOrderable = orderableReferenceDataService.findOne(item.getOrderableDispensed());
    }
    if (item.getLotId() != null) {
      lot = lotReferenceDataService.findOne(item.getLotId());
    }

    return PrescriptionLineItemDto.builder()
        .id(item.getId())
        .dose(item.getDose())
        .doseUnits(item.getDoseUnits())
        .doseFrequency(item.getDoseFrequency())
        .route(item.getRoute())
        .duration(item.getDuration())
        .durationUnits(item.getDurationUnits())
        .additionalInstructions(item.getAdditionalInstructions())
        .orderablePrescribed(item.getOrderablePrescribed())
        .quantityPrescribed(item.getQuantityPrescribed())
        .status(item.getStatus())
        .orderableDispensed(item.getOrderableDispensed())
        .lotId(item.getLotId())
        .quantityDispensed(item.getQuantityDispensed())
        .servedExternally(item.getServedExternally())
        .comments(item.getComments())
        .remainingBalance(item.getRemainingBalance())
        .orderablePrescribedName(prescribedOrderable != null ? prescribedOrderable.getFullProductName() : null)
        .orderableDispensedName(dispensedOrderable != null ? dispensedOrderable.getFullProductName() : null)
        .lotCode(lot != null ? lot.getLotCode() : null)
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

  private void updatePrescriptionLineItemEntity(PrescriptionLineItem prescriptionLineItem, 
      PrescriptionLineItemDto prescriptionLineItemDto) {
    if (prescriptionLineItem.getDose() != null) {
      prescriptionLineItem.setDose(prescriptionLineItemDto.getDose());
    }
    if (prescriptionLineItem.getDoseUnits() != null) {
      prescriptionLineItem.setDoseUnits(prescriptionLineItemDto.getDoseUnits());
    }
    if (prescriptionLineItem.getDoseFrequency() != null) {
      prescriptionLineItem.setDoseFrequency(prescriptionLineItem.getDoseFrequency());
    }
    if (prescriptionLineItem.getRoute() != null) {
      prescriptionLineItem.setRoute(prescriptionLineItemDto.getRoute());
    }
    if (prescriptionLineItem.getDuration() != null) {
      prescriptionLineItem.setDuration(prescriptionLineItemDto.getDuration());
    }
    if (prescriptionLineItem.getDurationUnits() != null) {
      prescriptionLineItem.setDurationUnits(prescriptionLineItemDto.getDurationUnits());
    }
    if (prescriptionLineItem.getDuration() != null) {
      prescriptionLineItem.setDuration(prescriptionLineItemDto.getDuration());
    }
    if (prescriptionLineItem.getAdditionalInstructions() != null) {
      prescriptionLineItem.setAdditionalInstructions(prescriptionLineItemDto.getAdditionalInstructions());
    }
    if (prescriptionLineItem.getOrderablePrescribed() != null) {
      prescriptionLineItem.setOrderablePrescribed(prescriptionLineItemDto.getOrderableDispensed());
    }
    if (prescriptionLineItem.getQuantityPrescribed() != null) {
      prescriptionLineItem.setQuantityDispensed(prescriptionLineItemDto.getQuantityDispensed());
    }

    if (prescriptionLineItem.getOrderableDispensed() != null) {
      prescriptionLineItem.setOrderableDispensed(prescriptionLineItemDto.getOrderableDispensed());
    }
    if (prescriptionLineItem.getLotId() != null) {
      prescriptionLineItem.setLotId(prescriptionLineItemDto.getLotId());
    }
    if (prescriptionLineItem.getAdditionalInstructions() != null) {
      prescriptionLineItem.setAdditionalInstructions(prescriptionLineItemDto.getAdditionalInstructions());
    }
    if (prescriptionLineItem.getQuantityDispensed() != null) {
      prescriptionLineItem.setQuantityDispensed(prescriptionLineItemDto.getQuantityDispensed());
    }
    if (prescriptionLineItem.getRemainingBalance() != null) {
      prescriptionLineItem.setRemainingBalance(prescriptionLineItemDto.getRemainingBalance());
    }
    if (prescriptionLineItem.getServedExternally() != null) {
      prescriptionLineItem.setServedExternally(prescriptionLineItemDto.getServedExternally());
    }
    if (prescriptionLineItem.getComments() != null) {
      prescriptionLineItem.setComments(prescriptionLineItemDto.getComments());
    }
    if (prescriptionLineItem.getCollectBalanceDate() != null) {
      prescriptionLineItem.setCollectBalanceDate(prescriptionLineItemDto.getCollectBalanceDate());
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
      LocalDate dateOfBirth,
      UUID facilityUuid, UUID geoZoneUuid, String nationalId, List<PrescriptionStatus> statuses, String patientType, Boolean isVoided,
      LocalDate followUpDate) {

    // First, find the patients based on the given patient details
    List<PatientDto> patientDtos = patientService.searchPatients(patientNumber, firstName, lastName, dateOfBirth,
        facilityUuid, geoZoneUuid, nationalId);

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
        //.and(PrescriptionSpecification.statusEquals(status))
        .and(PrescriptionSpecification.statusIn(statuses))
        .and(PrescriptionSpecification.patientTypeEquals(patientType))
        .and(PrescriptionSpecification.isVoidedEquals(isVoided))
        .and(PrescriptionSpecification.followUpDateEquals(followUpDate));

    // Then, search for prescriptions based on the Specification
    List<Prescription> prescriptions = prescriptionRepository.findAll(spec);

    // Convert Prescription entities to PrescriptionDto objects
    return prescriptions == null ? new ArrayList<PrescriptionDto>()
        : prescriptions.stream()
            .map(this::prescriptionToDto)
            .collect(Collectors.toList());
  }

}
