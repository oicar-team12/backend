package hr.algebra.shiftschedulingapp.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Group DTO")
public class GroupDto {

  @Schema(
    description = "Group ID",
    example = "1"
  )
  private Long id;

  @NotBlank
  @Size(min = 3)
  @Schema(
    description = "Group name",
    example = "First Group"
  )
  private String name;

  public GroupDto(String name) {
    this.name = name;
  }
}
