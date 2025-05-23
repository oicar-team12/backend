package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleDto;
import hr.algebra.shiftschedulingapp.model.dto.ScheduleGroupedDto;
import hr.algebra.shiftschedulingapp.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
public class ScheduleController {

  private final ScheduleService scheduleService;

  @GetMapping("schedules")
  @RequiresGroupMembership
  public List<ScheduleGroupedDto> getSchedules(@PathVariable Long groupId, @ModelAttribute ScheduleCriteriaDto scheduleCriteriaDto) {
    return scheduleService.getSchedules(groupId, scheduleCriteriaDto);
  }

  @PostMapping("schedule")
  @RequiresGroupManagerRole
  public void addSchedule(@PathVariable Long groupId, @RequestBody ScheduleDto scheduleDto) {
    scheduleService.addSchedule(groupId, scheduleDto);
  }

  @DeleteMapping("schedule/{id}")
  @RequiresGroupManagerRole
  public void removeSchedule(@PathVariable Long groupId, @PathVariable Long id) {
    scheduleService.removeSchedule(groupId, id);
  }
}
