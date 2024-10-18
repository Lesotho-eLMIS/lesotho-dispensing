package org.openlmis.dispensing.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.dto.BaseDto;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto extends BaseDto {
    private UUID id;
    private String district;
    private String village;
    private String constituency;
    private String chief;
}
