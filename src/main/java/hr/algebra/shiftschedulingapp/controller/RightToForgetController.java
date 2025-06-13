package hr.algebra.shiftschedulingapp.controller;

import hr.algebra.shiftschedulingapp.annotation.RequiresAdmin;
import hr.algebra.shiftschedulingapp.enums.RightToForgetRequestDecision;
import hr.algebra.shiftschedulingapp.model.dto.RestErrorDto;
import hr.algebra.shiftschedulingapp.model.dto.UserDeleteRequestDto;
import hr.algebra.shiftschedulingapp.service.RightToForgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@RequestMapping("right-to-forget")
@RequiredArgsConstructor
@Tag(name = "Right to forget", description = "'Right to forget' management APIs")
public class RightToForgetController {

  private final RightToForgetService rightToForgetService;

  @Operation(
    summary = "Get 'right to forget' requests",
    description = "Returns requests that have not been approved or denied yet"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "List of requests returned successfully"
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not an administrator",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @GetMapping("requests")
  @RequiresAdmin
  public List<UserDeleteRequestDto> getRequests() {
    return rightToForgetService.getRequests();
  }

  @Operation(
    summary = "Request user deletion",
    description = "Makes a request for the user to be deleted"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Request saved successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Previous request is not yet finalized",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PostMapping("request")
  public void requestDeletion() {
    rightToForgetService.requestDeletion();
  }

  @Operation(
    summary = "Finalize the request",
    description = "Finalizes the request and when approved, logs out and deletes the user"
  )
  @Parameter(
    name = "decision",
    description = "Decision for the request",
    in = PATH
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Request finalization successful"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Request not found / request has already been finalized",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    ),
    @ApiResponse(
      responseCode = "403",
      description = "User is not an administrator",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @PutMapping("request/{id}/decision/{decision}")
  @RequiresAdmin
  public void finalizeRequest(@PathVariable Long id, @PathVariable RightToForgetRequestDecision decision) {
    rightToForgetService.finalizeRequest(id, decision);
  }

  @Operation(
    summary = "Delete the request",
    description = "Deletes the user's request to be forgotten"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Request deleted successfully"
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Request not found",
      content = @Content(schema = @Schema(implementation = RestErrorDto.class))
    )
  })
  @DeleteMapping("request")
  public void deleteRequest() {
    rightToForgetService.deleteRequest();
  }
}
