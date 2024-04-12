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

import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person", schema = "dispensing")
public class Person extends BaseEntity {
  private String firstName;
  private String lastName;
  private String nickName;
  private String nationalId;
  private String sex;
  private LocalDate dateOfBirth;
  private Boolean isDoBEstimated;
  private String physicalAddress;
  private String nextOfKinFullName;
  private String nextOfKinContact;
  private String motherMaidenName;
  private Boolean deceased;
  private Boolean retired;
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "personId") // foreign key in Contact table
  private List<Contact> contacts;
  //dates for events needed?? e.g. deceased, retired, created, etc.
}
