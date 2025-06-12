package hr.algebra.shiftschedulingapp.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

  private final SecretKeySpec secretKeySpec;
  private final boolean enabled;

  private static final String KEY_ALGORITHM = "AES";
  private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

  @SneakyThrows
  public CryptoConverter(
    @Value("${crypto-converter.cipher.key}") String base64CipherKey,
    @Value("${crypto-converter.enabled:true}") boolean enabled
  ) {
    this.enabled = enabled;

    if (enabled) {
      byte[] keyBytes = getDecoder().decode(base64CipherKey);
      if (keyBytes.length != 32) {
        throw new IllegalArgumentException("Invalid crypto converter key length: must be 32 bytes for AES-256");
      }
      this.secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    } else {
      this.secretKeySpec = null;
    }
  }

  @SneakyThrows
  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (!enabled || attribute == null) {
      return attribute;
    }

    try {
      Cipher cipher = getInstance(CIPHER_TRANSFORMATION);
      cipher.init(ENCRYPT_MODE, secretKeySpec);

      byte[] encrypted = cipher.doFinal(attribute.getBytes(UTF_8));
      return getEncoder().encodeToString(encrypted);
    } catch (Exception ex) {
      throw new IllegalStateException("Encryption error", ex);
    }
  }

  @SneakyThrows
  @Override
  public String convertToEntityAttribute(String dbData) {
    if (!enabled || dbData == null) {
      return dbData;
    }

    try {
      Cipher cipher = getInstance(CIPHER_TRANSFORMATION);
      cipher.init(DECRYPT_MODE, secretKeySpec);

      byte[] decoded = getDecoder().decode(dbData);
      return new String(cipher.doFinal(decoded), UTF_8);
    } catch (Exception ex) {
      throw new IllegalStateException("Decryption error", ex);
    }
  }
}
