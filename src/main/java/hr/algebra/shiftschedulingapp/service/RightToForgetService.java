package hr.algebra.shiftschedulingapp.service;

import hr.algebra.shiftschedulingapp.enums.RightToForgetRequestDecision;
import hr.algebra.shiftschedulingapp.exception.RestException;
import hr.algebra.shiftschedulingapp.model.dto.UserDeleteRequestDto;
import hr.algebra.shiftschedulingapp.model.jpa.UserDeleteRequest;
import hr.algebra.shiftschedulingapp.repository.UserDeleteRequestsRepository;
import hr.algebra.shiftschedulingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hr.algebra.shiftschedulingapp.enums.RightToForgetRequestDecision.APPROVE;
import static hr.algebra.shiftschedulingapp.util.AuthUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class RightToForgetService {

  private final UserService userService;
  private final RefreshTokenService refreshTokenService;
  private final AccessTokenService accessTokenService;
  private final UserDeleteRequestsRepository userDeleteRequestsRepository;
  private final UserRepository userRepository;

  public List<UserDeleteRequestDto> getRequests() {
    return userDeleteRequestsRepository.findByIsApprovedIsNull();
  }

  public void requestDeletion() {
    Long userId = getCurrentUser().getId();
    if (userDeleteRequestsRepository.existsByUserIdAndIsApprovedIsNull(userId)) {
      throw new RestException("Previous request is not yet finalized");
    }
    userDeleteRequestsRepository.save(new UserDeleteRequest(userRepository.getReferenceById(userId)));
  }

  public void finalizeRequest(Long id, RightToForgetRequestDecision decision) {
    UserDeleteRequest request = validateRequestExistenceAndGet(id);
    if (request.getIsApproved() != null) {
      throw new RestException("Request has already been finalized");
    }

    request.setIsApproved(APPROVE.equals(decision));
    userDeleteRequestsRepository.save(request);

    if (APPROVE.equals(decision)) {
      Long userId = request.getUser().getId();

      refreshTokenService.revokeTokenByUserId(userId);
      accessTokenService.revokeTokenByUserId(userId);

      userService.deleteUser(userId);
    }
  }

  public void deleteRequest() {
    if (userDeleteRequestsRepository.deleteTop1ByUserIdAndIsApprovedIsNullOrderByCreatedAtDesc(getCurrentUser().getId()) < 1) {
      throw new RestException("Request not found");
    }
  }

  private UserDeleteRequest validateRequestExistenceAndGet(Long id) {
    return userDeleteRequestsRepository.findById(id)
      .orElseThrow(() -> new RestException("Request not found"));
  }
}
