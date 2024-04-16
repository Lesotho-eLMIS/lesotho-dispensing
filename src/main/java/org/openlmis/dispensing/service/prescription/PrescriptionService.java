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
 * http://www.gnu.org/licenses.  For additional information PrescriptionLineItem info@OpenLMIS.org.
 */

package org.openlmis.dispensing.service.prescription;

import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;
import org.openlmis.dispensing.dto.prescription.PrescriptionDto;
import org.openlmis.dispensing.dto.prescription.PrescriptionLineItemDto;
import org.openlmis.dispensing.repository.prescription.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {
  //private static final Logger LOGGER = LoggerFactory.getLogger(PrescriptionService.class);

  @Autowired
  private PrescriptionRepository prescriptionRepository;

  /**
   * Search for Prescriptions.
   *
   * @param PrescriptionNumber unique Prescription number.
   * @param firstName          Prescription first name.
   * @param lastName           Prescription last name.
   * @param dateOfBirth        Prescription date of birth.
   * @return List of Prescriptions matching the criteria.

   @Transactional(readOnly = true)
   public List<PrescriptionDto> searchPrescriptions(String PrescriptionNumber, String firstName, String lastName, String dateOfBirth) {
   Specification<Prescription> spec = PrescriptionSpecifications.bySearchCriteria(PrescriptionNumber, firstName, lastName, dateOfBirth);
   return PrescriptionRepository.findAll(spec).stream()
   .map(this::PrescriptionToDto)
   .collect(Collectors.toList());
   }
   */
  /**
   * Update a Prescription.
   *
   * @param id  Prescription id.
   * @param dto Prescription dto.
   * @return a updated Prescription dto.
   */
  public PrescriptionDto updatePrescription(UUID id, PrescriptionDto dto) {
    Optional<Prescription> existingPrescription = prescriptionRepository.findById(id);

    if (!existingPrescription.isPresent()) {
      return null;
    }

    Prescription Prescription = existingPrescription.get();
    updatePrescriptionEntity(Prescription, dto);
    Prescription = prescriptionRepository.save(Prescription);

    return PrescriptionToDto(Prescription);
  }

  /**
   * Create a Prescription.
   *
   * @param PrescriptionDto PrescriptionDto.
   * @return id of created PrescriptionDto.
   */
  @Transactional
  public UUID createPrescription(PrescriptionDto prescriptionDto) {
    Prescription prescription = convertToPrescriptionEntity(prescriptionDto);
    return prescriptionRepository.save(prescription).getId();
  }

  /**
   * Convert Prescription dto to jpa model (entity).
   *
   * @param PrescriptionDto Dto.
   * @return Prescription.
   */
  private Prescription convertToPrescriptionEntity(PrescriptionDto prescriptionDto) {
    if (null == prescriptionDto) {
      return null;
    }
    Prescription prescription = new Prescription();
    Prescription.setPrescriptionNumber(PrescriptionDto.getPrescriptionNumber());
    Prescription.setPerson(convertToPersonEntity(PrescriptionDto.getPersonDto()));
    if (PrescriptionDto.getMedicalHistory() != null) {
      Prescription.setMedicalHistory(PrescriptionDto.getMedicalHistory().stream()
          .map(medicalHistoryDto -> convertToMedicalHistoryEntity(medicalHistoryDto, Prescription))
          .collect(Collectors.toList()));
    }
    return Prescription;
  }

  private Person convertToPersonEntity(PersonDto personDto) {
    if (personDto == null) {
      return null;
    }
    Person person = new Person();
    person.setFirstName(personDto.getFirstName());
    person.setLastName(personDto.getLastName());
    person.setNationalId(personDto.getNationalId());
    person.setDateOfBirth(personDto.getDateOfBirth());
    person.setNickName(personDto.getNickName());
    person.setSex(personDto.getSex());
    person.setNextOfKinFullName(personDto.getNextOfKinFullName());
    person.setNextOfKinPrescriptionLineItem(personDto.getNextOfKinPrescriptionLineItem());
    person.setNickName(personDto.getNickName());
    person.setMotherMaidenName(personDto.getMotherMaidenName());
    person.setDeceased(personDto.getDeceased());
    person.setRetired(personDto.getRetired());
    if (personDto.getPrescriptionLineItems() != null) {
      person.setPrescriptionLineItems(personDto.getPrescriptionLineItems().stream()
          .map(PrescriptionLineItemDto -> convertToPrescriptionLineItemEntity(PrescriptionLineItemDto, person))
          .collect(Collectors.toList()));
    }
    return person;
  }

  private PrescriptionLineItem convertToPrescriptionLineItemEntity(PrescriptionLineItemDto prescriptionLineItemDto, Person person) {
    if (prescriptionLineItemDto == null) {
      return null;
    }
    return new PrescriptionLineItem(PrescriptionLineItemDto.getPrescriptionLineItemType(), PrescriptionLineItemDto.getPrescriptionLineItemValue(), person);
  }

  private MedicalHistory convertToMedicalHistoryEntity(MedicalHistoryDto medicalHistoryDto, Prescription Prescription) {
    if (medicalHistoryDto == null || medicalHistoryDto.getType() == null
        || medicalHistoryDto.getHistory() == null) {
      return null;
    }
    return new MedicalHistory(medicalHistoryDto.getType(), medicalHistoryDto.getHistory(), Prescription);
  }

  /**
   * Create dto from jpa model.
   *
   * @param Prescription jpa model.
   * @return Prescription created dto.
   */
  private PrescriptionDto PrescriptionToDto(Prescription Prescription) {
    return PrescriptionDto.builder()
        .id(Prescription.getId())
        .PrescriptionNumber(Prescription.getPrescriptionNumber())
        .personDto(personToDto(Prescription.getPerson()))
        .medicalHistory(Prescription.getMedicalHistory() != null
            ? Prescription.getMedicalHistory().stream()
            .map(this::medicalHistoryToDto)
            .collect(Collectors.toList())
            : null)
        .build();
  }

  /**
   * Create dto from jpa model.
   *
   * @param person jpa model.
   * @return created dto.
   */
  private PersonDto personToDto(Person person) {
    return PersonDto.builder()
        .id(person.getId())
        .nationalId(person.getNationalId())
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .dateOfBirth(person.getDateOfBirth())
        .sex(person.getSex())
        .isDoBEstimated(person.getIsDoBEstimated())
        .physicalAddress(person.getPhysicalAddress())
        .nextOfKinFullName(person.getNextOfKinFullName())
        .nextOfKinPrescriptionLineItem(person.getNextOfKinPrescriptionLineItem())
        .motherMaidenName(person.getMotherMaidenName())
        .deceased(person.getDeceased())
        .retired(person.getRetired())
        .PrescriptionLineItems(person.getPrescriptionLineItems() != null
            ? person.getPrescriptionLineItems().stream()
            .map(this::PrescriptionLineItemToDto)
            .collect(Collectors.toList())
            : null)
        .build();
  }

  /**
   * Convert PrescriptionLineItem entity to PrescriptionLineItemDto.
   *
   * @param PrescriptionLineItem PrescriptionLineItem entity.
   * @return PrescriptionLineItemDto.
   */
  private PrescriptionLineItemDto PrescriptionLineItemToDto(PrescriptionLineItem PrescriptionLineItem) {
    if (PrescriptionLineItem == null) {
      return null;
    }

    return PrescriptionLineItemDto.builder()
        .id(PrescriptionLineItem.getId())
        .PrescriptionLineItemType(PrescriptionLineItem.getPrescriptionLineItemType())
        .PrescriptionLineItemValue(PrescriptionLineItem.getPrescriptionLineItemValue())
        .build();
  }

  /**
   * Convert MedicalHistory entity to MedicalHistoryDto.
   *
   * @param medicalHistory MedicalHistory entity.
   * @return PrescriptionLineItemDto.
   */
  private MedicalHistoryDto medicalHistoryToDto(MedicalHistory medicalHistory) {
    if (medicalHistory == null) {
      return null;
    }

    return MedicalHistoryDto.builder()
        .id(medicalHistory.getId())
        .type(medicalHistory.getType())
        .history(medicalHistory.getHistory())
        .build();
  }

  private void updatePrescriptionEntity(Prescription Prescription, PrescriptionDto PrescriptionDto) {
    if (PrescriptionDto.getPrescriptionNumber() != null) {
      Prescription.setPrescriptionNumber(PrescriptionDto.getPrescriptionNumber());
    }
    if (PrescriptionDto.getPersonDto() != null) {
      updatePersonEntity(Prescription.getPerson(), PrescriptionDto.getPersonDto());
    }
    if (PrescriptionDto.getMedicalHistory() != null) {
      Prescription.getMedicalHistory().clear();
      Prescription.getMedicalHistory().addAll(PrescriptionDto.getMedicalHistory().stream()
          .map(medicalHistoryDto -> convertToMedicalHistoryEntity(medicalHistoryDto, Prescription))
          .collect(Collectors.toList()));
    }
  }

  private void updatePersonEntity(Person person, PersonDto personDto) {
    if (personDto == null || person == null) {
      return;
    }

    if (personDto.getFirstName() != null) {
      person.setFirstName(personDto.getFirstName());
    }
    if (personDto.getLastName() != null) {
      person.setLastName(personDto.getLastName());
    }
    if (personDto.getNickName() != null) {
      person.setNickName(personDto.getNickName());
    }
    if (personDto.getNationalId() != null) {
      person.setNationalId(personDto.getNationalId());
    }
    if (personDto.getSex() != null) {
      person.setSex(personDto.getSex());
    }
    if (personDto.getDateOfBirth() != null) {
      person.setDateOfBirth(personDto.getDateOfBirth());
    }
    if (personDto.getIsDoBEstimated() != null) {
      person.setIsDoBEstimated(personDto.getIsDoBEstimated());
    }
    if (personDto.getPhysicalAddress() != null) {
      person.setPhysicalAddress(personDto.getPhysicalAddress());
    }
    if (personDto.getNextOfKinFullName() != null) {
      person.setNextOfKinFullName(personDto.getNextOfKinFullName());
    }
    if (personDto.getNextOfKinPrescriptionLineItem() != null) {
      person.setNextOfKinPrescriptionLineItem(personDto.getNextOfKinPrescriptionLineItem());
    }
    if (personDto.getMotherMaidenName() != null) {
      person.setMotherMaidenName(personDto.getMotherMaidenName());
    }
    if (personDto.getDeceased() != null) {
      person.setDeceased(personDto.getDeceased());
    }
    if (personDto.getRetired() != null) {
      person.setRetired(personDto.getRetired());
    }

    if (personDto.getPrescriptionLineItems() != null) { //update PrescriptionLineItems
      person.getPrescriptionLineItems().clear();
      person.getPrescriptionLineItems().addAll(personDto.getPrescriptionLineItems().stream()
          .map(PrescriptionLineItemDto -> convertToPrescriptionLineItemEntity(PrescriptionLineItemDto, person))
          .collect(Collectors.toList()));
    }
  }
}
