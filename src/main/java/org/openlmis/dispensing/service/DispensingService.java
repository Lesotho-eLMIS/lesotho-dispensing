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

package org.openlmis.dispensing.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.openlmis.dispensing.domain.event.DispensingEvent;
import org.openlmis.dispensing.domain.qualitychecks.Discrepancy;
import org.openlmis.dispensing.dto.DiscrepancyDto;
import org.openlmis.dispensing.dto.DispensingEventDto;
import org.openlmis.dispensing.exception.ResourceNotFoundException;
import org.openlmis.dispensing.repository.DispensingEventsRepository;
import org.openlmis.dispensing.service.requisition.RejectionReasonService;
import org.openlmis.dispensing.util.DispensingEventProcessContext;
import org.openlmis.dispensing.util.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DispensingService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DispensingService.class);

  @Autowired
  private DispensingEventsRepository pointOfDeliveryEventsRepository;

  @Autowired
  private DispensingEventProcessContextBuilder contextBuilder;

  @Autowired
  private RejectionReasonService rejectionReasonService;

  /**
   * Get a list of Point of Delivery events.
   *
   * @param destinationId destination id.
   * @return a list of pod events.
   */
  public List<DispensingEventDto> getDispensingEventsByDestinationId(UUID destinationId) {
    List<DispensingEvent> pointOfDeliveryEvents = pointOfDeliveryEventsRepository
        .findByDestinationId(destinationId);
    
    if (pointOfDeliveryEvents == null) {
      return Collections.emptyList();
    }
    return podToDto(pointOfDeliveryEvents);
  }

  /**
   * Get a Point of Delivery event by id.
   *
   * @param id point of delivery event id.
   * @return a pod event.
   */
  public Optional<DispensingEvent> getDispensingEventById(UUID id) {
    return pointOfDeliveryEventsRepository.findById(id);
  }

  /**
   * Save or update POD.
   *
   * @param dto POD event dto.
   * @return the saved POD event.
   */
  public DispensingEventDto updateDispensingEvent(DispensingEventDto dto, UUID id) {
    //LOGGER.info("update POS event");
    //physicalInventoryValidator.validateDraft(dto, id);
    //checkPermission(dto.getProgramId(), dto.getFacilityId());

    //checkIfDraftExists(dto, id);
    
    LOGGER.info("Attempting to fetch pod event with id = " + id);
    Optional<DispensingEvent> existingPodEventOpt = 
        pointOfDeliveryEventsRepository.findById(id);

    if (existingPodEventOpt.isPresent()) {
      DispensingEvent existingPodEvent = existingPodEventOpt.get();
      DispensingEventProcessContext context = contextBuilder.buildContext(dto);
      dto.setContext(context);
      DispensingEvent incomingPodEvent = dto.toDispensingEvent();

      // Update the Existing PodEvent object with values incoming DTO data
      existingPodEvent = copyAttributes(existingPodEvent, incomingPodEvent);
    
      //save updated pod event
      pointOfDeliveryEventsRepository.save(existingPodEvent);
      return podToDto(existingPodEvent);
    } else {
      return null;
    }
  }

  private DispensingEvent copyAttributes(
      DispensingEvent existingPodEvent, DispensingEvent incomingPodEvent) {
    if (incomingPodEvent.getSourceId() != null) {
      existingPodEvent.setSourceId(incomingPodEvent.getSourceId());
    }
    if (incomingPodEvent.getSourceFreeText() != null) {
      existingPodEvent.setSourceFreeText(incomingPodEvent.getSourceFreeText());
    }
    if (incomingPodEvent.getDestinationId() != null) {
      existingPodEvent.setDestinationId(incomingPodEvent.getDestinationId());
    }
    if (incomingPodEvent.getDestinationFreeText() != null) {
      existingPodEvent.setDestinationFreeText(incomingPodEvent.getDestinationFreeText());
    }
    if (incomingPodEvent.getReferenceNumber() != null) {
      existingPodEvent.setReferenceNumber(incomingPodEvent.getReferenceNumber());
    }
    if (incomingPodEvent.getPackingDate() != null) {
      existingPodEvent.setPackingDate(incomingPodEvent.getPackingDate());
    }
    if (incomingPodEvent.getPackedBy() != null) {
      existingPodEvent.setPackedBy(incomingPodEvent.getPackedBy());
    }
    if (incomingPodEvent.getCartonsQuantityOnWaybill() != null) {
      existingPodEvent.setCartonsQuantityOnWaybill(
          incomingPodEvent.getCartonsQuantityOnWaybill());
    }
    if (incomingPodEvent.getCartonsQuantityShipped() != null) {
      existingPodEvent.setCartonsQuantityShipped(
          incomingPodEvent.getCartonsQuantityShipped());
    }
    if (incomingPodEvent.getCartonsQuantityAccepted() != null) {
      existingPodEvent.setCartonsQuantityAccepted(
          incomingPodEvent.getCartonsQuantityAccepted());
    }
    if (incomingPodEvent.getCartonsQuantityRejected() != null) {
      existingPodEvent.setCartonsQuantityRejected(
          incomingPodEvent.getCartonsQuantityRejected());
    }
    if (incomingPodEvent.getContainersQuantityOnWaybill() != null) {
      existingPodEvent.setContainersQuantityOnWaybill(
          incomingPodEvent.getContainersQuantityOnWaybill());
    }
    if (incomingPodEvent.getContainersQuantityShipped() != null) {
      existingPodEvent.setContainersQuantityShipped(
          incomingPodEvent.getContainersQuantityShipped());
    }
    if (incomingPodEvent.getContainersQuantityAccepted() != null) {
      existingPodEvent.setContainersQuantityAccepted(
          incomingPodEvent.getContainersQuantityAccepted());
    }
    if (incomingPodEvent.getContainersQuantityRejected() != null) {
      existingPodEvent.setContainersQuantityRejected(
          incomingPodEvent.getContainersQuantityRejected());
    }
    if (incomingPodEvent.getRemarks() != null) {
      existingPodEvent.setRemarks(incomingPodEvent.getRemarks());
    }
    if (incomingPodEvent.getDiscrepancies() != null) {
      existingPodEvent.setDiscrepancies(incomingPodEvent.getDiscrepancies());
    }
    return existingPodEvent;
  }

  /**
   * Delete POD.
   *
   * @param id POD event id.
   */
  public void deleteDispensingEvent(UUID id) {
    //LOGGER.info("update POS event");
    //physicalInventoryValidator.validateDraft(dto, id);
    //checkPermission(dto.getProgramId(), dto.getFacilityId());

    //checkIfDraftExists(dto, id);
    
    LOGGER.info("Attempting to fetch pod event with id = " + id);
    Optional<DispensingEvent> existingPodEventOpt = 
        pointOfDeliveryEventsRepository.findById(id);

    if (existingPodEventOpt.isPresent()) {
      //delete pod event
      pointOfDeliveryEventsRepository.delete(existingPodEventOpt.get());
    } else {
      throw new ResourceNotFoundException(new Message("Point of delivery event not found ", id));
    }
  }

  /**
   * Create from jpa model.
   *
   * @param pointOfDeliveryEvents inventory jpa model.
   * @return created dto.
   */
  private List<DispensingEventDto> podToDto(
        Collection<DispensingEvent> pointOfDeliveryEvents) {

    List<DispensingEventDto> podDtos = new ArrayList<>(pointOfDeliveryEvents.size());
    pointOfDeliveryEvents.forEach(i -> podDtos.add(podToDto(i)));
    return podDtos;
  }

  /**
   * Create dto from jpa model.
   *
   * @param pointOfDeliveryEvent inventory jpa model.
   * @return created dto.
   */
  private DispensingEventDto podToDto(DispensingEvent pointOfDeliveryEvent) {
    return DispensingEventDto.builder()
      .id(pointOfDeliveryEvent.getId())
      .sourceId(pointOfDeliveryEvent.getSourceId())
      .sourceFreeText(pointOfDeliveryEvent.getSourceFreeText())
      .destinationId(pointOfDeliveryEvent.getDestinationId())
      .destinationFreeText(pointOfDeliveryEvent.getDestinationFreeText())
      .receivedByUserId(pointOfDeliveryEvent.getReceivedByUserId())
      .receivedByUserNames(pointOfDeliveryEvent.getReceivedByUserNames())
      .receivingDate(pointOfDeliveryEvent.getReceivingDate())
      .referenceNumber(pointOfDeliveryEvent.getReferenceNumber())
      .packingDate(pointOfDeliveryEvent.getPackingDate())
      .packedBy(pointOfDeliveryEvent.getPackedBy())
      .cartonsQuantityOnWaybill(pointOfDeliveryEvent.getCartonsQuantityOnWaybill())
      .cartonsQuantityShipped(pointOfDeliveryEvent.getCartonsQuantityShipped())
      .cartonsQuantityAccepted(pointOfDeliveryEvent.getCartonsQuantityAccepted())
      .cartonsQuantityRejected(pointOfDeliveryEvent.getCartonsQuantityRejected())
      .containersQuantityOnWaybill(pointOfDeliveryEvent.getContainersQuantityOnWaybill())
      .containersQuantityShipped(pointOfDeliveryEvent.getContainersQuantityShipped())
      .containersQuantityAccepted(pointOfDeliveryEvent.getContainersQuantityAccepted())
      .containersQuantityRejected(pointOfDeliveryEvent.getContainersQuantityRejected())
      .remarks(pointOfDeliveryEvent.getRemarks())
      .discrepancies(discrepaciesToDtos(pointOfDeliveryEvent.getDiscrepancies()))
      .build();
  }

  /**
   * Create from jpa model.
   *
   * @param discrepancies inventory jpa model.
   * @return created dto.
   */
  private List<DiscrepancyDto> discrepaciesToDtos(
        Collection<Discrepancy> discrepancies) {

    List<DiscrepancyDto> discrepacyDtos = new ArrayList<>(discrepancies.size());
    discrepancies.forEach(i -> discrepacyDtos.add(discrepancyToDto(i)));
    return discrepacyDtos;
  }

  /**
   * Create dto from jpa model.
   *
   * @param discrepancy inventory jpa model.
   * @return created dto.
   */
  private DiscrepancyDto discrepancyToDto(Discrepancy discrepancy) {

    return DiscrepancyDto.builder()
      .id(discrepancy.getId())
      .rejectionReason(
          rejectionReasonService.findOne(discrepancy.getRejectionReasonId()))
      .shipmentType(discrepancy.getShipmentType())
      .quantityAffected(discrepancy.getQuantityAffected())
      .comments(discrepancy.getComments())
      .build();
  }

}
