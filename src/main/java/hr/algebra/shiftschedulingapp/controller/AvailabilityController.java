package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityDto;
import hr.algebra.shiftschedulingapp.model.dto.AvailabilityGroupedDto;
import hr.algebra.shiftschedulingapp.service.AvailabilityService;
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
public class AvailabilityController {

  private final AvailabilityService availabilityService;

  @GetMapping("availabilities")
  @RequiresGroupMembership
  public List<AvailabilityGroupedDto> getAvailabilities(@PathVariable Long groupId, @ModelAttribute AvailabilityCriteriaDto availabilityCriteriaDto) {
    return availabilityService.getAvailabilities(groupId, availabilityCriteriaDto);
  }

  @PostMapping("availability")
  @RequiresGroupMembership
  public void addAvailability(@PathVariable Long groupId, @RequestBody AvailabilityDto availabilityDto) {
    availabilityService.addAvailability(groupId, availabilityDto);
  }

  @DeleteMapping("availability/{id}")
  @RequiresGroupMembership
  public void removeAvailability(@PathVariable Long groupId, @PathVariable Long id) {
    availabilityService.removeAvailability(groupId, id);
  }
}
