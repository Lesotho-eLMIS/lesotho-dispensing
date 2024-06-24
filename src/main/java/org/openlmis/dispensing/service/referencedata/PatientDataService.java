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

  public boolean exists(UUID id) {
    return id != null && findOne(id) != null;
  }
  public PatientDto findById(UUID id) {
    if (id == null) {
      return null;
    }
    return  null;
  }
}
