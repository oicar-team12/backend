package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresGroupManagerRole;
import hr.algebra.shiftschedulingapp.annotation.RequiresGroupMembership;
import hr.algebra.shiftschedulingapp.model.dto.ShiftCriteriaDto;
import hr.algebra.shiftschedulingapp.model.dto.ShiftDto;
import hr.algebra.shiftschedulingapp.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("group/{groupId}")
@RequiredArgsConstructor
public class ShiftController {

  private final ShiftService shiftService;

  @GetMapping("shifts")
  @RequiresGroupMembership
  public List<ShiftDto> getShifts(@PathVariable Long groupId, @ModelAttribute ShiftCriteriaDto shiftCriteriaDto) {
    return shiftService.getShifts(groupId, shiftCriteriaDto);
  }

  @PostMapping("shift")
  @RequiresGroupManagerRole
  public void addShift(@PathVariable Long groupId, @RequestBody ShiftDto shiftDto) {
    shiftService.addShift(groupId, shiftDto);
  }

  @PutMapping("shift/{id}")
  @RequiresGroupManagerRole
  public void updateShift(@PathVariable Long groupId, @PathVariable Long id, @RequestBody ShiftDto shiftDto) {
    shiftService.modifyShift(groupId, id, shiftDto);
  }

  @DeleteMapping("shift/{id}")
  @RequiresGroupManagerRole
  public void removeShift(@PathVariable Long groupId, @PathVariable Long id) {
    shiftService.removeShift(groupId, id);
  }
}
