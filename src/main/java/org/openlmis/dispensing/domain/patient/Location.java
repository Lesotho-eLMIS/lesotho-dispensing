package org.openlmis.dispensing.domain.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location")
public class Location extends BaseEntity {
    private String district;
    private String village;
    private String constituency;
    private String chief;
}
