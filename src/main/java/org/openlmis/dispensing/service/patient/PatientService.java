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

package org.openlmis.dispensing.service.patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
import java.util.Optional;
// import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.patient.MedicalHistory;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.domain.patient.Person;
import org.openlmis.dispensing.dto.patient.ContactDto;
import org.openlmis.dispensing.dto.patient.MedicalHistoryDto;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.dto.patient.PersonDto;
import org.openlmis.dispensing.repository.patient.PatientRepository;
import org.openlmis.dispensing.service.referencedata.FacilityReferenceDataService;
import org.openlmis.dispensing.util.PatientSpecifications;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {
  //private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  /**
   * Search for patients.
   *
   * @param patientNumber unique patient number.
   * @param firstName patient first name.
   * @param lastName patient last name.
   * @param dateOfBirth patient date of birth.
   * @return List of patients matching the criteria.
   */
  @Transactional(readOnly = true)
  public List<PatientDto> searchPatients(String patientNumber, String firstName, String lastName, String dateOfBirth, UUID facilityId, String nationalId) {
    Specification<Patient> spec = PatientSpecifications.bySearchCriteria(patientNumber, firstName, lastName, dateOfBirth, facilityId, nationalId);
    return patientRepository.findAll(spec).stream()
                                          .map(this::patientToDto)
                                          .collect(Collectors.toList());
  }

  /**
   * Update a Patient.
   *
   * @param id patient id.
   * @param dto patient dto.
   * @return a updated patient dto.
   */
  public PatientDto updatePatient(UUID id, PatientDto dto) {
    Optional<Patient> existingPatient = patientRepository.findById(id);

    if (!existingPatient.isPresent()) {
      return null;
    }

    Patient patient = existingPatient.get();
    updatePatientEntity(patient, dto);
    patient = patientRepository.save(patient);

    return patientToDto(patient);
  }

  /**
   * Create a Patient.
   *
   * @param patientDto patientDto.
   * @return id of created patientDto.
   */
  @Transactional
  public UUID createPatient(PatientDto patientDto) {
    Patient patient = convertToPatientEntity(patientDto);
    return patientRepository.save(patient).getId();
  }

  /**
   * Convert patient dto to jpa model (entity).
   *
   * @param patientDto Dto.
   * @return Patient.
   */
  private Patient convertToPatientEntity(PatientDto patientDto) {
    if (null == patientDto) {
      return null;
    }
    
    //given facility SHOULD exist
    if (facilityReferenceDataService.exists(patientDto.getFacilityId())) {
      Patient patient = new Patient();
      LocalDate today = LocalDate.now();

      String facilityCode = facilityReferenceDataService.findOne(patientDto.getFacilityId()).getCode();

      patient.setPatientNumber(generatePatientNumber(patientDto.getFacilityId(), facilityCode, today));
      patient.setPerson(convertToPersonEntity(patientDto.getPersonDto()));
      patient.setFacilityId(patientDto.getFacilityId());
      patient.setRegistrationDate(today);
      if (patientDto.getMedicalHistory() != null) {
        patient.setMedicalHistory(patientDto.getMedicalHistory().stream()
            .map(medicalHistoryDto -> convertToMedicalHistoryEntity(medicalHistoryDto, patient))
            .collect(Collectors.toList()));
      }
      return patient;
    }
    return null;
  }

  private Person convertToPersonEntity(PersonDto personDto) {
    if (personDto == null) {
      return null;
    }
    Person person = new Person();
    person.setFirstName(personDto.getFirstName());
    person.setLastName(personDto.getLastName());
    person.setNickName(personDto.getNickName());
    person.setNationalId(personDto.getNationalId());
    person.setDateOfBirth(personDto.getDateOfBirth());
    person.setIsDobEstimated(personDto.getIsDobEstimated());
    person.setSex(personDto.getSex());
    person.setPhysicalAddress(personDto.getPhysicalAddress());
    person.setNextOfKinFullName(personDto.getNextOfKinFullName());
    person.setNextOfKinContact(personDto.getNextOfKinContact());
    person.setMotherMaidenName(personDto.getMotherMaidenName());
    person.setDeceased(personDto.getDeceased());
    person.setRetired(personDto.getRetired());
    if (personDto.getContacts() != null) {
      person.setContacts(personDto.getContacts().stream()
                            .map(contactDto -> convertToContactEntity(contactDto, person))
                            .collect(Collectors.toList()));
    }
    return person;
  }

  private Contact convertToContactEntity(ContactDto contactDto, Person person) {
    if (contactDto == null) {
      return null;
    }
    return new Contact(contactDto.getContactType(),contactDto.getContactValue(), person);
  }

  private MedicalHistory convertToMedicalHistoryEntity(MedicalHistoryDto medicalHistoryDto, Patient patient) {
    if (medicalHistoryDto == null || medicalHistoryDto.getType() == null
          || medicalHistoryDto.getHistory() == null) {
      return null;
    }
    return new MedicalHistory(medicalHistoryDto.getType(), medicalHistoryDto.getHistory(), patient);
  }
  
  /**
   * Create dto from jpa model.
   *
   * @param patient jpa model.
   * @return Patient created dto.
   */
  private PatientDto patientToDto(Patient patient) {
    return PatientDto.builder()
      .id(patient.getId())
      .patientNumber(patient.getPatientNumber())
      .facilityId(patient.getFacilityId())
      .registrationDate(patient.getRegistrationDate())
      .personDto(personToDto(patient.getPerson()))
      .medicalHistory(patient.getMedicalHistory() != null
          ? patient.getMedicalHistory().stream()
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
      .nickName(person.getNickName())
      .dateOfBirth(person.getDateOfBirth())
      .sex(person.getSex())
      .isDobEstimated(person.getIsDobEstimated())
      .physicalAddress(person.getPhysicalAddress())
      .nextOfKinFullName(person.getNextOfKinFullName())
      .nextOfKinContact(person.getNextOfKinContact())
      .motherMaidenName(person.getMotherMaidenName())
      .deceased(person.getDeceased())
      .retired(person.getRetired())
      .contacts(person.getContacts() != null 
          ? person.getContacts().stream()
                                .map(this::contactToDto)
                                .collect(Collectors.toList())
          : null)
      .build();
  }

  /**
   * Convert Contact entity to ContactDto.
   *
   * @param contact Contact entity.
   * @return ContactDto.
   */
  private ContactDto contactToDto(Contact contact) {
    if (contact == null) {
      return null;
    }

    return ContactDto.builder()
      .id(contact.getId())
      .contactType(contact.getContactType())
      .contactValue(contact.getContactValue())
      .build();
  }

  /**
   * Convert MedicalHistory entity to MedicalHistoryDto.
   *
   * @param medicalHistory MedicalHistory entity.
   * @return ContactDto.
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

  private void updatePatientEntity(Patient patient, PatientDto patientDto) {

    if (patientDto.getPersonDto() != null) {
      updatePersonEntity(patient.getPerson(), patientDto.getPersonDto());
    }
    if (patientDto.getMedicalHistory() != null) {
      patient.getMedicalHistory().clear();
      patient.getMedicalHistory().addAll(patientDto.getMedicalHistory().stream()
          .map(medicalHistoryDto -> convertToMedicalHistoryEntity(medicalHistoryDto, patient))
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
    if (personDto.getIsDobEstimated() != null) {
      person.setIsDobEstimated(personDto.getIsDobEstimated());
    }
    if (personDto.getPhysicalAddress() != null) {
      person.setPhysicalAddress(personDto.getPhysicalAddress());
    }
    if (personDto.getNextOfKinFullName() != null) {
      person.setNextOfKinFullName(personDto.getNextOfKinFullName());
    }
    if (personDto.getNextOfKinContact() != null) {
      person.setNextOfKinContact(personDto.getNextOfKinContact());
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
    
    if (personDto.getContacts() != null) { //update contacts
      person.getContacts().clear();
      person.getContacts().addAll(personDto.getContacts().stream()
          .map(contactDto -> convertToContactEntity(contactDto, person))
          .collect(Collectors.toList()));
    }
  } 

  private synchronized String generatePatientNumber(UUID facilityId, String facilityCode, LocalDate date) {
    String datePart = date.format(DATE_FORMAT);
    //int countToday = patientRepository.countByFacilityIdAndRegistrationDate(facilityId, date);
    int countSoFar = patientRepository.countByFacilityId(facilityId);
    return facilityCode + "/" + datePart + "/" + String.format("%05d", countSoFar + 1);
  }

  /**
   * Get a Patient.
   *
   * @param id patient id.
   *
   * @return a patient dto.
   */
  public PatientDto getPatientById(UUID id) {
    Optional<Patient> patienOptional = patientRepository.findById(id);

    if (patienOptional.isPresent()) {
      return patientToDto(patienOptional.get());
    }
    return null;
  }
}
