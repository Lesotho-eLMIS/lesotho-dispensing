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

package org.openlmis.dispensing.dto.prescription;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;
import org.openlmis.dispensing.domain.status.PrescriptionLineItemStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionLineItemDto {
  private UUID id;
  private Integer dose;
  private String doseUnits;
  private String doseFrequency;
  private String route;
  private Integer duration;
  private String durationUnits;
  private String additionalInstructions;
  private UUID orderablePrescribed;
  private Integer quantityPrescribed;
  private PrescriptionLineItemStatus status;
  
  private UUID orderableDispensed;
  private UUID lotId;
  private Integer quantityDispensed;
  private Integer remainingBalance;
  private Boolean servedExternally;
  private String comments;
  private LocalDate collectBalanceDate;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public PrescriptionLineItem toPrescriptionLineItem() {
    return new PrescriptionLineItem(
        dose, doseUnits, doseFrequency, route, duration, durationUnits,
        additionalInstructions, orderablePrescribed, quantityPrescribed,
        orderableDispensed, lotId, quantityDispensed, remainingBalance, servedExternally,
        comments, collectBalanceDate
    );
  }

  /**
   * Convert jpa model to dto.
   *
   * @param item the jpa model object.
   * @return the converted dto object.
   */
  public static PrescriptionLineItemDto fromPrescriptionLineItem(PrescriptionLineItem item) {
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
        //.status(item.getStatus())
        .orderableDispensed(item.getOrderableDispensed())
        .lotId(item.getLotId())
        .quantityDispensed(item.getQuantityDispensed())
        .remainingBalance(item.getRemainingBalance())
        .servedExternally(item.getServedExternally())
        .comments(item.getComments())
        .collectBalanceDate(item.getCollectBalanceDate())
        .build();
  }
}
