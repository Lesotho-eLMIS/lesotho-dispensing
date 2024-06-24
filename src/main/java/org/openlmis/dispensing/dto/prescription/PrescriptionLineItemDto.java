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

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionLineItemDto {
  private UUID id;
  private String dosage;
  private Integer period;
  private String batchId;
  private Integer quantityPrescribed;
  private Integer quantityDispensed;
  private Boolean servedInternally;
  private String orderableId;
  private String substituteOrderableId;
  private String comments;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */

  public PrescriptionLineItem toPrescriptionLineItem() {
    return new PrescriptionLineItem(
        dosage, period, batchId, quantityPrescribed,
        quantityDispensed, servedInternally, orderableId,
        substituteOrderableId, comments
    );
  }
}
