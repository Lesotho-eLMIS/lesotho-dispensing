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

package org.openlmis.dispensing.service.referencedata;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.util.RequestParameters;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PatientDataService extends BaseReferenceDataService<PatientDto> {

  @Override
  protected String getUrl() {
    return "/api/patient/";
  }

  @Override
  protected Class<PatientDto> getResultClass() {
    return PatientDto.class;
  }

  @Override
  protected Class<PatientDto[]> getArrayResultClass() {
    return PatientDto[].class;
  }

  /**
   * Finds patients by their ids.
   *
   * @param ids ids to look for.
   * @return map of ids and patients
   */
  public Map<UUID, PatientDto> findByIds(Collection<UUID> ids) {
    RequestParameters parameters = RequestParameters
        .init()
        .set("id", ids);

    Page<PatientDto> patientDtos = getPage(parameters);
    return patientDtos.getContent().stream()
        .collect(Collectors.toMap(PatientDto::getId, Function.identity()));
  }

  /**
   * Finds patients by their ids.
   *
   * @param id ids to look for.
   * @return boolean
   */
  public boolean exists(UUID id) {
    return id != null && findOne(id) != null;
  }
  
  /**
   * Finds patients by their ids.
   *
   * @param id ids to look for.
   * @return patientDto
   */
  public PatientDto findById(UUID id) {
    if (id == null) {
      return null;
    }
    return  null;
  }
}
