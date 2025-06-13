package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.model.dto.GroupDto;
import hr.algebra.shiftschedulingapp.model.jpa.Group;
import hr.algebra.shiftschedulingapp.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hr.algebra.shiftschedulingapp.enums.GroupUserRole.MANAGER;
import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupUserService groupUserService;
  private final AvailabilityService availabilityService;
  private final ShiftService shiftService;
  private final ScheduleService scheduleService;

  public GroupDto createGroup(GroupDto groupDto) {
    Group group = groupRepository.save(new Group(groupDto.getName()));
    groupUserService.addUserToGroup(group.getId(), getCurrentUser().getId(), MANAGER);
    return new GroupDto(group.getId(), group.getName());
  }

  public GroupDto modifyGroup(Long id, GroupDto groupDto) {
    Group group = groupRepository.getReferenceById(id);
    group.setName(groupDto.getName());

    groupRepository.save(group);
    return new GroupDto(group.getId(), group.getName());
  }

  public void deleteGroup(Long id) {
    scheduleService.removeByGroupId(id);
    shiftService.removeByGroupId(id);
    availabilityService.removeByGroupId(id);
    groupUserService.removeByGroupId(id);
    groupRepository.deleteById(id);
  }
}
