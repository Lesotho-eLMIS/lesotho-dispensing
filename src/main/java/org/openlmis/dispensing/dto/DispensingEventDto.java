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


package org.openlmis.dispensing.dto;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openlmis.dispensing.domain.event.DispensingEvent;
import org.openlmis.dispensing.domain.qualitychecks.Discrepancy;
import org.openlmis.dispensing.dto.DiscrepancyDto;
import org.openlmis.dispensing.util.DispensingEventProcessContext;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispensingEventDto {

  private UUID id;

  private UUID sourceId;
  private String sourceFreeText;

  private UUID destinationId;
  private String destinationFreeText;

  private UUID receivedByUserId;
  private String receivedByUserNames;

  private ZonedDateTime receivingDate;

  private String referenceNumber;

  private LocalDate packingDate;

  private String packedBy;

  private Integer cartonsQuantityOnWaybill;

  private Integer cartonsQuantityShipped;

  private Integer cartonsQuantityAccepted;

  private Integer cartonsQuantityRejected;

  private Integer containersQuantityOnWaybill;

  private Integer containersQuantityShipped;

  private Integer containersQuantityAccepted;

  private Integer containersQuantityRejected;

  private String remarks;

  private List<DiscrepancyDto> discrepancies;

  private DispensingEventProcessContext context;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public DispensingEvent toDispensingEvent() {

    // List<Discrepancy> discrepanciesList = new ArrayList<>();
    // for (DiscrepancyDto discrepancydto : discrepancies) {
    //   discrepanciesList.add(discrepancydto.toDiscrepancy());
    // }

    DispensingEvent pointOfDeliveryEvent = new DispensingEvent(
        sourceId, sourceFreeText, destinationId, destinationFreeText, 
        context.getCurrentUserId(), context.getCurrentUserNames(), now(), 
        referenceNumber, packingDate, packedBy, cartonsQuantityOnWaybill, 
        cartonsQuantityShipped, cartonsQuantityAccepted, cartonsQuantityRejected,
        containersQuantityOnWaybill, containersQuantityShipped, 
        containersQuantityAccepted, containersQuantityRejected,
        remarks, discrepancies());
    return pointOfDeliveryEvent;
  }

  public boolean hasSourceId() {
    return this.sourceId != null;
  }

  public boolean hasDestinationId() {
    return this.destinationId != null;
  }

  /**
   * Gets discrepancies as {@link Discrepancy}.
   */
  public List<Discrepancy> discrepancies() {
    if (null == discrepancies) {
      return emptyList();
    }

    List<Discrepancy> discrepanciesList = new ArrayList<>();
    for (DiscrepancyDto discrepancydto : discrepancies) {
      discrepanciesList.add(discrepancydto.toDiscrepancy());
    }
    return discrepanciesList;
  }

}
