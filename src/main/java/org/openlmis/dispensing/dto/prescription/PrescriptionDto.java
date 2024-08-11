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

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;
import org.openlmis.dispensing.domain.status.PrescriptionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDto {
  private UUID id;
  private UUID patientId;
  private String patientType;
  private LocalDate followUpDate;
  private LocalDate issueDate;
  private LocalDate createdDate;
  private LocalDate capturedDate;
  private LocalDate lastUpdate;
  private Boolean isVoided;
  //private String status;
  private PrescriptionStatus status;
  private UUID facilityId;
  private UUID prescribedByUserId;
  private UUID servedByUserId;
  private List<PrescriptionLineItemDto> lineItems;



  /**
   * Gets contacts as {@link Contact}.
   */
  public List<PrescriptionLineItem> lineItems() {
    if (null == lineItems) {
      return emptyList();
    }

    List<PrescriptionLineItem> lineItemList = new ArrayList<>();
    for (PrescriptionLineItemDto prescriptionLineItemDto : lineItems) {
      lineItemList.add(prescriptionLineItemDto.toPrescriptionLineItem());
    }
    return lineItemList;
  }
}
