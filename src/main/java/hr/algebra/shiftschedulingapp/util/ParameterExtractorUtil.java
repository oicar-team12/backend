package hr.algebra.shiftschedulingapp.util;

import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ParameterExtractorUtil {

  public static Long extractGroupId(JoinPoint joinPoint, String targetParamName) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Parameter[] parameters = signature.getMethod().getParameters();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < parameters.length; i++) {
      if (isMatchingParameter(parameters[i], targetParamName)) {
        return (Long) args[i];
      }
    }

    throw new IllegalArgumentException("Group ID parameter '" + targetParamName + "' not found");
  }

  private static boolean isMatchingParameter(Parameter parameter, String targetParamName) {
    return getParameterName(parameter).equals(targetParamName);
  }

  private static String getParameterName(Parameter parameter) {
    return extractPathVariableName(parameter)
      .or(() -> extractRequestParamName(parameter))
      .orElse(parameter.getName());
  }

  private static Optional<String> extractPathVariableName(Parameter parameter) {
    return ofNullable(parameter.getAnnotation(PathVariable.class))
      .map(PathVariable::value)
      .filter(value -> !value.isEmpty())
      .or(() -> of(parameter.getName()));
  }

  private static Optional<String> extractRequestParamName(Parameter parameter) {
    return ofNullable(parameter.getAnnotation(RequestParam.class))
      .map(RequestParam::value)
      .filter(value -> !value.isEmpty())
      .or(() -> of(parameter.getName()));
  }
}
