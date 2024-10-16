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

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.qualitychecks.Discrepancy;
import org.openlmis.dispensing.dto.requisition.RejectionReasonDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscrepancyDto {

  private UUID id;
  private RejectionReasonDto rejectionReason;
  private String shipmentType;
  private Integer quantityAffected;
  private String comments;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public Discrepancy toDiscrepancy() {
    Discrepancy discrepancy = new Discrepancy(
        rejectionReason.getId(),
        shipmentType,
        quantityAffected,
        comments);

    return discrepancy;
  }
}
