package hr.algebra.shiftschedulingapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.shiftschedulingapp.exception.ForbiddenException;
import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.interfaces.ScheduleProjection;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleGroupedDto;
import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.model.dto.UserDto;
import hr.algebra.shiftschedulingapp.model.jpa.Schedule;
import hr.algebra.shiftschedulingapp.repository.GroupUserRepository;
import hr.algebra.shiftschedulingapp.repository.ScheduleRepository;
import hr.algebra.shiftschedulingapp.repository.ShiftRepository;
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
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final ShiftRepository shiftRepository;
  private final UserRepository userRepository;
  private final GroupUserRepository groupUserRepository;
  private final ObjectMapper objectMapper;

  public List<ScheduleGroupedDto> getSchedules(Long groupId, ScheduleCriteriaDto scheduleCriteriaDto) {
    validateScheduleViewership(groupId, scheduleCriteriaDto);

    List<ScheduleProjection> projections = scheduleRepository.findByCriteria(
      groupId, scheduleCriteriaDto.getUserId(), scheduleCriteriaDto.getStartDate(), scheduleCriteriaDto.getEndDate()
    );

    return projections.stream()
      .map(this::convertToGroupedDto)
      .toList();
  }

  public void addSchedule(Long groupId, ScheduleDto scheduleDto) {
    validateShiftAndUserExistence(scheduleDto.getShiftId(), scheduleDto.getUserId(), groupId);
    validateScheduleDuplication(scheduleDto);

    scheduleRepository.save(new Schedule(
      shiftRepository.getReferenceById(scheduleDto.getShiftId()),
      userRepository.getReferenceById(scheduleDto.getUserId())
    ));
  }

  public void removeSchedule(Long groupId, Long id) {
    validateScheduleExistence(groupId, id);
    scheduleRepository.deleteById(id);
  }

  private void validateScheduleViewership(Long groupId, ScheduleCriteriaDto scheduleCriteriaDto) {
    Long userId = getCurrentUser().getId();
    if (!groupUserRepository.getRoleByGroupIdAndUserId(groupId, getCurrentUser().getId()).equals(MANAGER)) {
      if (nonNull(scheduleCriteriaDto.getUserId()) && !userId.equals(scheduleCriteriaDto.getUserId())) {
        throw new ForbiddenException();
      }
      scheduleCriteriaDto.setUserId(userId);
    }
  }

  private void validateShiftAndUserExistence(Long shiftId, Long userId, Long groupId) {
    if (!shiftRepository.existsByIdAndGroup_Id(shiftId, groupId)) {
      throw new RestException("Shift not found");
    }

    if (!groupUserRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
      throw new RestException("User not found");
    }
  }

  private void validateScheduleDuplication(ScheduleDto scheduleDto) {
    if (scheduleRepository.existsByShift_IdAndUser_Id(scheduleDto.getShiftId(), scheduleDto.getUserId())) {
      throw new RestException("This user has already been scheduled for this shift");
    }
  }

  private void validateScheduleExistence(Long groupId, Long id) {
    if (!scheduleRepository.existsByShift_GroupId_AndId(groupId, id)) {
      throw new RestException("Schedule not found");
    }
  }

  @SneakyThrows
  private ScheduleGroupedDto convertToGroupedDto(ScheduleProjection projection) {
    ShiftDto shift = objectMapper.readValue(
      projection.getShift(),
      ShiftDto.class
    );
    List<UserDto> users = objectMapper.readValue(
      projection.getUsers(),
      new TypeReference<>() {}
    );

    return new ScheduleGroupedDto(shift, users);
  }
}
