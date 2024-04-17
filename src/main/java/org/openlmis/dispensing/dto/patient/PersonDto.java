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

package org.openlmis.dispensing.dto.patient;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.UUID;
//import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.patient.Person;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
  private UUID id;
  private String firstName;
  private String lastName;
  private String nickName;
  private String nationalId;
  private String sex;
  private LocalDate dateOfBirth;
  private Boolean isDobEstimated;
  private String physicalAddress;
  private String nextOfKinFullName;
  private String nextOfKinContact;
  private String motherMaidenName;
  private Boolean deceased;
  private Boolean retired;
  private List<ContactDto> contacts;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public Person toPerson() {
    Person person = new Person(
        firstName, lastName, nickName, nationalId, sex, dateOfBirth,
        isDobEstimated, physicalAddress, nextOfKinFullName, nextOfKinContact,
        motherMaidenName, deceased, retired, contacts()
    );
    return person;
  }

  /**
   * Gets contacts as {@link Contact}.
   */
  public List<Contact> contacts() {
    if (null == contacts) {
      return emptyList();
    }

    List<Contact> contactsList = new ArrayList<>();
    for (ContactDto contactDto : contacts) {
      contactsList.add(contactDto.toContact());
    }
    return contactsList;
  }
}
