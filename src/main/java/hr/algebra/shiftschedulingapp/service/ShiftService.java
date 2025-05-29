package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.ShiftCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.model.jpa.Shift;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import hr.algebra.shiftschedulingapp.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftService {

  private final ShiftRepository shiftRepository;
  private final GroupRepository groupRepository;

  public List<ShiftDto> getShifts(Long groupId, ShiftCriteriaDto shiftCriteriaDto) {
    return shiftRepository.findByCriteria(groupId, shiftCriteriaDto.getStartDate(), shiftCriteriaDto.getEndDate());
  }

  public void addShift(Long groupId, ShiftDto shiftDto) {
    validateShiftDuplication(groupId, shiftDto);

    shiftRepository.save(new Shift(
      groupRepository.getReferenceById(groupId),
      shiftDto.getDate(),
      shiftDto.getStartTime(),
      shiftDto.getEndTime()
    ));
  }

  public void modifyShift(Long groupId, ShiftDto shiftDto) {
    validateShiftExistence(groupId, shiftDto.getId());
    validateShiftDuplication(groupId, shiftDto);

    shiftRepository.save(new Shift(
      shiftDto.getId(),
      groupRepository.getReferenceById(groupId),
      shiftDto.getDate(),
      shiftDto.getStartTime(),
      shiftDto.getEndTime()
    ));
  }

  public void removeShift(Long groupId, Long id) {
    validateShiftExistence(groupId, id);
    shiftRepository.deleteById(id);
  }

  private void validateShiftDuplication(Long groupId, ShiftDto shiftDto) {
    if (shiftRepository.existsByGroup_IdAndDate_AndStartTime_AndEndTime(groupId, shiftDto.getDate(), shiftDto.getStartTime(), shiftDto.getEndTime())) {
      throw new RestException("A shift on this date with the same start and end time already exists in this group");
    }
  }

  private void validateShiftExistence(Long groupId, Long id) {
    if (!shiftRepository.existsByIdAndGroup_Id(id, groupId)) {
      throw new RestException("Shift not found");
    }
  }
}
