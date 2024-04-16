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

package org.openlmis.dispensing.domain.prescription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PrescriptionLineItem", schema = "dispensing")
public class PrescriptionLineItem extends BaseEntity {
  private String dosage;
  private Integer period;
  private String batchId;
  private Integer quantityPrescribed;
  private Integer quantityDispensed;
  private Boolean servedInternally;
  private String orderableId;
  private String substituteOrderableId;
  private String comments;

  @ManyToOne
  @JoinColumn(name = "prescription_id")
  private Prescription prescription;
  //dates for events needed?? e.g. deceased, retired, created, etc.
}
