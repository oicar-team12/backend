package hr.algebra.shiftschedulingapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.shiftschedulingapp.exception.ForbiddenException;
import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.interfaces.AvailabilityProjection;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityGroupedDto;
import hr.algebra.shiftschedulingapp.model.dto.UserDto;
import hr.algebra.shiftschedulingapp.model.jpa.Availability;
import hr.algebra.shiftschedulingapp.repository.AvailabilityRepository;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.MANAGER;
import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

  private final AvailabilityRepository availabilityRepository;
  private final GroupRepository groupRepository;
  private final GroupUserRepository groupUserRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  public List<AvailabilityGroupedDto> getAvailabilities(Long groupId, AvailabilityCriteriaDto availabilityCriteriaDto) {
    validateAvailabilityViewership(groupId, availabilityCriteriaDto);

    List<AvailabilityProjection> projections = availabilityRepository.findByCriteria(groupId, availabilityCriteriaDto.getUserId(), availabilityCriteriaDto.getStartDate(), availabilityCriteriaDto.getEndDate());

    return projections.stream()
      .map(this::convertToGroupedDto)
      .toList();
  }

  public void addAvailability(Long groupId, AvailabilityDto availabilityDto) {
    Long userId = getCurrentUser().getId();
    if (availabilityRepository.existsByGroupIdAndUserIdAndDate(groupId, userId, availabilityDto.getDate())) {
      throw new RestException("The specified date is already marked in this group");
    }

    availabilityRepository.save(new Availability(
      userRepository.getReferenceById(userId),
      groupRepository.getReferenceById(groupId),
      availabilityDto.getDate(),
      availabilityDto.isAvailable()
    ));
  }

  public void removeAvailability(Long groupId, Long id) {
    Long userId = getCurrentUser().getId();
    if (!availabilityRepository.existsByIdAndGroupIdAndUserId(id, groupId, userId)) {
      throw new RestException("Availability not found");
    }

    availabilityRepository.deleteById(id);
  }

  public void removeByGroupId(Long groupId) {
    availabilityRepository.deleteByGroupId(groupId);
  }

  private void validateAvailabilityViewership(Long groupId, AvailabilityCriteriaDto availabilityCriteriaDto) {
    Long userId = getCurrentUser().getId();
    if (!groupUserRepository.getRoleByGroupIdAndUserId(groupId, userId).equals(MANAGER)) {
      if (nonNull(availabilityCriteriaDto.getUserId()) && !userId.equals(availabilityCriteriaDto.getUserId())) {
        throw new ForbiddenException();
      }
      availabilityCriteriaDto.setUserId(userId);
    }
  }

  @SneakyThrows
  private AvailabilityGroupedDto convertToGroupedDto(AvailabilityProjection projection) {
    UserDto user = objectMapper.readValue(
      projection.getUser(),
      UserDto.class
    );
    List<AvailabilityDto> availabilities = objectMapper.readValue(
      projection.getAvailabilities(),
      new TypeReference<>() {}
    );

    return new AvailabilityGroupedDto(user, availabilities);
  }
}
