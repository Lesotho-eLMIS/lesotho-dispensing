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

package org.openlmis.dispensing.dto.referencedata;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LotDto {
  private UUID id;
  private String lotCode;
  private boolean active;
  private UUID tradeItemId;
  @JsonFormat(shape = STRING)
  private LocalDate expirationDate;
  @JsonFormat(shape = STRING)
  private LocalDate manufactureDate;

  @Override
  public String toString() {
    return "{" + " id='" + getId() + "'" + ", lotCode='" + getLotCode() + "'"
      + ", active='" + isActive() + "'" + ", tradeItemId='" + getTradeItemId() + "'"
      + ", expirationDate='" + getExpirationDate() + "'" + ", manufactureDate='" 
      + getManufactureDate() + "'" + "}";
  }
}
