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

package org.openlmis.dispensing.domain.patient;

import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_history", schema = "dispensing")
public class MedicalHistory extends BaseEntity {
  private String type;
  private String history;

  @ManyToOne
  @JoinColumn(name = "patient_id")
  private Patient patient;

  /**
   * Constructor for MedicalHistory.
   *
   */
  public MedicalHistory(String type, String history) {
    this.type = type;
    this.history = history;
    // patient can be set later or remain null
  }
}
