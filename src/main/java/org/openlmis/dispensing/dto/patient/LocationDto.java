package org.openlmis.dispensing.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.dto.BaseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto extends BaseDto {
    private String District;
    private String village;
    private String constituency;
}
