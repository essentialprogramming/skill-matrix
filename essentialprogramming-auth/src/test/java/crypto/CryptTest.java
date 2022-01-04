package crypto;

import com.crypto.Crypt;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

final class CryptTest {


    @Test
    void should_be_equal_before_and_after_decrypt() throws GeneralSecurityException, UnsupportedEncodingException {

       String encrypted = Crypt.encrypt("razvan", "supercalifragilisticexpialidocious");
       String decrypted = Crypt.decrypt(encrypted, "supercalifragilisticexpialidocious");

       assert decrypted.equals("razvan");


    }
}