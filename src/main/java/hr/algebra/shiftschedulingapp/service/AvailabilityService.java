package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.jpa.Availability;
import hr.algebra.shiftschedulingapp.repository.AvailabilityRepository;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityService {

  private final AvailabilityRepository availabilityRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public List<AvailabilityDto> getUserAvailabilities(Long groupId, Long userId) {
    return availabilityRepository.findByGroupIdAndUserId(groupId, userId);
  }

  public void addAvailability(Long groupId, AvailabilityDto availabilityDto) {
    Long userId = getCurrentUser().getId();
    if (availabilityRepository.existsByGroup_IdAndUser_IdAndDate(groupId, userId, availabilityDto.getDate())) {
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
    if (!availabilityRepository.existsByIdAndGroup_IdAndUser_Id(id, groupId, userId)) {
      throw new RestException("Availability not found");
    }

    availabilityRepository.deleteById(id);
  }
}
